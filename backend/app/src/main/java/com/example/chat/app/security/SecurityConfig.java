package com.example.chat.app.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/*
 * Le backend n'a pas besoin de se positionner comme client OIDC (ClientRegistrationRepository), c'est angular qui est le client OIDC dans notre cas
 * Le backend a juste besoin de valider le JWT reçu
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${CORS_ALLOWED_ORIGINS:*}")
  private String allowedOrigins; 
  // Valeur par défaut : "*" (autorise tout si non défini)

  private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

  private final List<String> permitAllPaths = List.of(
      "/actuator/health", "/actuator/info",
      "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
      "/webjars/**", "/favicon.ico", "/api/public/**"
  );

  
  @Value("${OIDC_LOGOUT_REDIRECT_URI:http://localhost:9080/}")
  private String postLogoutRedirectUri;


  private CorsConfiguration getCorsConfiguration() {
    CorsConfiguration config = new CorsConfiguration();
    
    // Convertit la variable d'env en liste
    List<String> origins = Arrays.stream(allowedOrigins.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());

    log.info("Startup: CORS allowed origins = {}", origins);

    if (origins.isEmpty()) {
        log.info("No CORS origins configured. Only same-origin requests will be allowed.");
    } else if (origins.contains("*")) {
        // Mode permissif (dev)
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowCredentials(false);
    } else {
        // Mode strict : liste définie
//            config.setAllowedOrigins(origins);
//            config.setAllowCredentials(true);
        origins.forEach(config::addAllowedOriginPattern);
        config.setAllowCredentials(true);
        log.info("Configured CORS allowed origins/patterns: {}", origins);
    }

    config.setAllowedHeaders(List.of("*"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    config.setMaxAge(3600L);

    return config;
  }


  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // CORS for API
        .cors(cors -> cors.configurationSource(request -> getCorsConfiguration()))
        
        // CSRF off for API
        .csrf(csrf -> csrf.disable())
        // Stateless for API
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Authorizations
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers(
              "/actuator/health", "/actuator/info",
              "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**",
              "/webjars/**", "/favicon.ico",
              "/api/public/**"
            ).permitAll()
            .anyRequest().authenticated()
        )
        // Resource server JWT only for API
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
        // Return 401/403 JSON for API instead of redirecting to login
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
            .accessDeniedHandler(new BearerTokenAccessDeniedHandler()));

    return http.build();
  }

}
