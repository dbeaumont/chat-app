# Keycloak — Import du realm

Ce dossier contient un export de realm Keycloak **demo** avec le **client public** `chat-app` déjà configuré
pour l'application Angular (PKCE, standard flow, redirect URIs).

- Fichier : `realm-demo-with-client.json`
- Client : `chat-app` (public, PKCE S256)
- Redirect URIs : `http://localhost:8888/*`
- Web Origins : `http://localhost:8888`, `+`
- Utilisateur de test : `user/user`

## Import automatique (docker-compose)
Le `docker-compose.yml` monte ce dossier dans Keycloak :
```
./keycloak:/opt/keycloak/data/import
```
et démarre Keycloak en mode import :
```
command: ["start-dev", "--http-relative-path=/", "--import-realm", "--hostname-strict=false"]
```

Au premier lancement, Keycloak importe le realm `demo`.
