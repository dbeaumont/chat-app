# Backend — Spring Boot (API + Sécurité)

## Sommaire
- [Aperçu](#aperçu)
- [Sécurité Spring](#sécurité-spring)
  - [Resource Server JWT](#resource-server-jwt)
  - [Validation du token (issuer / JWK)](#validation-du-token-issuer--jwk)
  - [CORS](#cors)
  - [Autorisations (routes publiques/privées)](#autorisations-routes-publiquesprivées)
  - [Gestion des erreurs](#gestion-des-erreurs)
- [Configuration](#configuration)
  - [application.yml](#applicationyml)
  - [Variables d’environnement](#variables-denvironnement)
- [Tests manuels (curl)](#tests-manuels-curl)
- [Profils](#profils)

## Aperçu
Le backend expose `/api/**` (REST) et fonctionne en **stateless**. L’authentification est réalisée via un JWT OIDC (Keycloak).

## Sécurité Spring

### Resource Server JWT
Configuration principale : `SecurityFilterChain` active :
- `http.cors(Customizer.withDefaults())`
- `http.csrf(csrf -> csrf.disable())`
- `authorizeHttpRequests` avec `permitAll` sur certaines routes et `authenticated()` pour le reste
- `oauth2ResourceServer(oauth2 -> oauth2.jwt())`
- `sessionManagement(...STATELESS)`
- `authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())`

### Validation du token (issuer / JWK)
- **Issuer attendu** via `spring.security.oauth2.resourceserver.jwt.issuer-uri` (ENV `OIDC_ISSUER_URI`).
- **Clés publiques (JWK)** via `spring.security.oauth2.resourceserver.jwt.jwk-set-uri` (ENV `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI`).  
  > Dans notre setup, `issuer` pointe vers `http://localhost:8081` (côté navigateur), tandis que `jwk-set-uri` pointe vers `http://keycloak:8080` (côté réseau Docker).

### CORS
- Filtre `CorsFilter` configuré à partir de `CORS_ALLOWED_ORIGINS`.  
- `OPTIONS /**` est `permitAll()` pour laisser passer le **préflight**.  
- `allowedHeaders: *`, `allowedMethods: GET, POST, PUT, DELETE, OPTIONS, PATCH`, `maxAge: 3600`.

### Autorisations (routes publiques/privées)
- **Public** : `/actuator/health`, `/actuator/info`, `/v3/api-docs/**`, `/swagger-ui.html`, `/swagger-ui/**`, `/webjars/**`, `/favicon.ico`, `/api/public/**`.
- **Privé** : tout le reste (`/api/**`) requiert un JWT valide.

### Gestion des erreurs
- **401 Unauthorized** : absence/invalidité du JWT.  
- **403 Forbidden** : authentifié mais non autorisé (si vous ajoutez des règles fines par rôles).  
- Entrée par défaut : `BearerTokenAuthenticationEntryPoint` pour réponses 401 standardisées.

## Configuration

### `application.yml`
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${OIDC_ISSUER_URI:http://localhost:8081/realms/demo}
          jwk-set-uri: ${SPRING_SECURITY_OAUTH2_RESOURCE_SERVER_JWT_JWK_SET_URI:http://localhost:8081/realms/demo/protocol/openid-connect/certs}
```

### Variables d’environnement
- `OIDC_ISSUER_URI` : `http://localhost:8081/realms/demo`
- `SPRING_SECURITY_OAUTH2_RESOURCE_SERVER_JWT_JWK_SET_URI` : `http://keycloak:8080/realms/demo/protocol/openid-connect/certs`
- `CORS_ALLOWED_ORIGINS` : `http://localhost,http://localhost:8888,...`

## Tests manuels (curl)
```bash
# Préflight CORS
curl -i -X OPTIONS http://localhost:9080/api/messages   -H 'Origin: http://localhost:8888'   -H 'Access-Control-Request-Method: GET'

# Appel protégé avec JWT
export TOKEN='eyJhbGciOiJSUzI1NiIsInR5cCI...'   # access_token obtenu via le front
curl -i http://localhost:9080/api/messages   -H "Authorization: Bearer $TOKEN"
```

## Profils
- **dev** : logs + H2 (si configuré), réglages de développement.  
- **prod** : durcissement + connexion PostgreSQL via variables d’environnement.  
- **it** : tests d’intégration avec dépendances optionnelles (ex: Testcontainers si activé).
