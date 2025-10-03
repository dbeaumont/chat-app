# Topologie réseau — Host vs Réseau Docker

```mermaid
flowchart LR
    subgraph Host[Machine hôte]
      Browser[Browser http://localhost:8888]
      KCui[Keycloak UI http://localhost:8081]
    end

    subgraph Docker[Bridge network: chat-net]
      FE[frontend:80]
      BE[backend:8080]
      KC[keycloak:8080]
      DB[(postgres:5432)]
    end

    Browser -->|HTTP| FE
    FE -->|HTTP| BE
    KCui --> KC

    %% Issuer vu par le browser
    Browser -. JWT iss .-> KCui

    %% JWK vu par le backend
    BE -->|HTTP| KC

    BE --> DB
```
**Points clés :**
- Le navigateur voit **Keycloak via `http://localhost:8081`** → le JWT a `iss=http://localhost:8081/realms/demo`.
- Le backend résout Keycloak via **`http://keycloak:8080`** (nom de service Docker) pour le **JWK Set**.
- C’est pourquoi on utilise **`OIDC_ISSUER_URI`** (côté browser) et un **`SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI`** (côté Docker) distincts.
