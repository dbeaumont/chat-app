package com.example.chat.app.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MeController {
  @GetMapping("/api/me")
  public Map<String, Object> me(Authentication auth) {
    if (auth == null) return Map.of("authenticated", false);

    Object principal = auth.getPrincipal();
    if (principal instanceof OidcUser oidc) {
      return Map.of(
          "authenticated", true,
          "name", oidc.getFullName(),
          "email", oidc.getEmail(),
          "claims", oidc.getClaims()
      );
    }
    return Map.of("authenticated", true, "principal", principal.toString());
  }
}
