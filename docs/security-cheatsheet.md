# Sécurité — Cheatsheet

## Backend (Spring Security)
- `cors()` activé + `OPTIONS /**` en `permitAll()`
- `csrf().disable()` (API stateless)
- `sessionManagement(STATELESS)`
- `oauth2ResourceServer(jwt())`
- `BearerTokenAuthenticationEntryPoint` pour 401 clairs
- Routes **publiques**: `/actuator/health`, `/actuator/info`, `/v3/api-docs/**`, `/swagger-ui/**`, `/api/public/**`
- Le reste **authentifié**

## Variables clés
- `OIDC_ISSUER_URI`: doit **matcher la claim `iss`** du token reçu par le browser
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI`: URL JWK côté réseau Docker
- `CORS_ALLOWED_ORIGINS`: origines front autorisées

## Diagnostics rapides
- **401** + `invalid_token` → vérifier `iss` / signature / exp / aud
- **401** + `No bearer token` → interceptor front absent
- **CORS** préflight échoue → vérifier `OPTIONS` et entêtes CORS
- **JWK unreachable** → vérifier l’URL `keycloak:8080` depuis le conteneur backend
