# Troubleshooting — Chat App (Angular • Spring Boot • Keycloak • PostgreSQL)

Cette page recense les **symptômes fréquents**, leur **cause probable**, et le **correctif** recommandé.
Elle inclut des commandes de diagnostic et un **arbre de décision** (Mermaid) pour aller vite.

## Table des problèmes courants

| Symptôme | Cause la plus probable | Correctif rapide |
|---|---|---|
| `401 Unauthorized` (API) | Pas de header `Authorization` | Vérifier que l’**AuthInterceptor** est bien enregistré et que l’utilisateur est loggé |
| `401 Unauthorized` + `invalid_token` | `iss` du JWT ≠ `OIDC_ISSUER_URI` | Aligner `OIDC_ISSUER_URI` avec l’issuer du token (ex: `http://localhost:8081/realms/demo`) |
| `401` après quelques minutes | Token expiré | Rafraîchir la session côté front / relancer login |
| `403 Forbidden` | Auth OK mais pas autorisé | Ajuster les règles d’accès (rôles/authorities/antMatchers) si vous en ajoutez |
| `CORS error` / Préflight en échec | Origine non autorisée | Ajouter l’URL dans `CORS_ALLOWED_ORIGINS` (docker-compose) |
| `OPTIONS ...` → `401` | Préflight bloqué | S’assurer que `OPTIONS /**` est `permitAll()` côté `SecurityFilterChain` |
| Impossible de valider la signature JWT | JWK Set injoignable depuis le backend | Définir `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI` vers `http://keycloak:8080/.../certs` |
| `Invalid redirect_uri` (Keycloak) | Mauvaise URL de redirection | Mettre `http://localhost:8888/` dans le client Keycloak |
| Login boucle sans fin | `state`/`nonce` erroné ou storage bloqué | Vider stockage local du browser / inspecter console |
| `net::ERR_BLOCKED_BY_CLIENT` | Extension bloqueur / privacy | Tester en navigation privée ou désactiver l’extension |
| `Mixed Content` (HTTPS) | Front en HTTPS, back en HTTP | Activer HTTPS côté back ou proxy, ou basculer tout en HTTP en dev |
| `Invalid CORS request` | En-têtes manquants | Laisser `allowedHeaders: *` et exposer `Authorization` si besoin |
| `Keycloak realm import` échoue | Fichier JSON non chargé | Vérifier volume `./keycloak:/opt/keycloak/data/import` et logs au boot |
| `Port already in use` | Conflit de port local | Changer les ports mappés dans `docker-compose.yml` |
| `DB connection refused` | Postgres pas prêt | Dépendance `depends_on: condition: service_healthy` et healthcheck Postgres |
| Erreurs Flyway au démarrage | Migration incompatible | Désactiver/adapter Flyway selon profil ou corriger les scripts |
| Décalage d’horloge | `exp`/`nbf` invalides | Synchroniser l’horloge de l’hôte / conteneurs |

## Commandes de diagnostic utiles

### Depuis l’hôte
```bash
# Vérifier le préflight CORS
curl -i -X OPTIONS http://localhost:9080/api/messages \
  -H 'Origin: http://localhost:8888' \
  -H 'Access-Control-Request-Method: GET'

# Appel protégé (remplacez $TOKEN)
curl -i http://localhost:9080/api/messages -H "Authorization: Bearer $TOKEN"

# Inspecter les entêtes d’une réponse
curl -i http://localhost:9080/actuator/health
```

### Depuis le conteneur backend
```bash
docker compose exec backend sh -lc 'apk add --no-cache curl || true; curl -sS http://keycloak:8080/realms/demo/protocol/openid-connect/certs | head -c 200'
```

### Logs backend (recherches rapides)
```bash
docker compose logs backend | egrep -i "(Bearer|JWT|issuer|CORS|preflight|unauthorized|forbidden|JWK|cert)"
```

## Checklists rapides

### CORS
- [ ] `CORS_ALLOWED_ORIGINS` contient **l’origine exacte** (ex: `http://localhost:8888`)
- [ ] `OPTIONS /**` = `permitAll()` dans la `SecurityFilterChain`
- [ ] Méthodes autorisées : `GET, POST, PUT, DELETE, OPTIONS, PATCH`
- [ ] Headers autorisés : `*` (ou au minimum `Authorization, Content-Type`)

### OIDC / JWT
- [ ] Le **token** est bien envoyé : `Authorization: Bearer ...`
- [ ] Claim **`iss`** du token = `OIDC_ISSUER_URI` backend
- [ ] Le backend **atteint** le JWK Set (URL Docker) : `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI`
- [ ] `exp/nbf` non expiré, pas de gros décalage système

### Keycloak
- [ ] Client **`chat-app`** : **Redirect URI** `http://localhost:8888/*`
- [ ] PKCE **activé** (client public) ou pas de secret utilisé côté front
- [ ] Realm **demo** importé et **actif**

## Arbre de décision (Mermaid)

```mermaid
flowchart TD
    A[Erreur 401/403 ou CORS] --> B{CORS error visible ?}
    B -- Oui --> C[OPTIONS 401/403 ?]
    C -- Oui --> C1[Autoriser OPTIONS /** en permitAll]
    C -- Non --> C2[Ajouter l'origine exacte dans CORS_ALLOWED_ORIGINS]

    B -- Non --> D{Header Authorization présent ?}
    D -- Non --> D1[Front non loggé ou interceptor non actif<br/>→ Vérifier APP_INITIALIZER + AuthInterceptor]
    D -- Oui --> E{JWT valide ? (iss/exp/signature)}
    E -- Non --> E1[Aligner OIDC_ISSUER_URI avec iss du token<br/>et configurer JWK_SET_URI vers keycloak:8080]
    E -- Oui --> F{Toujours 403 ?}
    F -- Oui --> F1[Autorisation manquante (roles/scopes)<br/>→ Assouplir règles ou ajouter le rôle]
    F -- Non --> G[OK]
```

## Modèles de messages d’erreur (à connaître)

- `BearerTokenAuthenticationEntryPoint` → 401
- `JwtValidationException` / `InvalidTokenException` → vérifier `iss`, `exp`, signature
- `AccessDeniedException` → 403 (auth OK mais pas autorisé)
- `Cors` / `Preflight` en logs → revoir config CORS

## Bonnes pratiques

- En dev, rester **pleinement HTTP** ou **pleinement HTTPS**, éviter le mix.
- Documenter **exactement** les URLs vues par : navigateur (localhost) vs conteneurs (noms de services).
- Penser à **vider le cache** Nginx/front si vous changez `API_BASE_URL` en runtime.
