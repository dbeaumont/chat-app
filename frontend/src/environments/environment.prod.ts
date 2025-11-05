export const environment = {
  "production": true,
  "oidc": {
    "issuer": "https://localhost/keycloak/realms/demo",
    "clientId": "frontend",
    "redirectUri": "https://localhost/",
    "scope": "openid profile email",
    "silentRefreshRedirectUri": "https://localhost/silent-refresh.html",
    "postLogoutRedirectUri": "https://localhost/"
  },
  "apiBaseUrl": "https://localhost"
} as const;
