package com.example.chat.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/*
@Configuration
public class CorsConfig {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter(
            @Value("${CORS_ALLOW_ALL:false}") boolean allowAll,
            @Value("${CORS_ALLOW_ORIGINS:}") String allowedOriginsRaw
    ) {
        CorsConfiguration config = new CorsConfiguration();

        if (allowAll) {
            log.warn("CORS is in ALLOW ALL mode. Do not use in production.");
            config.addAllowedOriginPattern("*"); // supports patterns + wildcard
            config.setAllowCredentials(true);
        } else {
            List<String> origins = Arrays.stream(allowedOriginsRaw.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            if (origins.isEmpty()) {
                log.info("No CORS origins configured. Only same-origin requests will be allowed.");
            } else {
                // Use patterns to support wildcards (Spring Framework 6+)
                origins.forEach(config::addAllowedOriginPattern);
                config.setAllowCredentials(true);
                log.info("Configured CORS allowed origins/patterns: {}", origins);
            }
        }

        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
*/
@Configuration
public class CorsConfig {

    @Value("${CORS_ALLOWED_ORIGINS:*}")
    private String allowedOrigins; 
    // Valeur par défaut : "*" (autorise tout si non défini)

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
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

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}