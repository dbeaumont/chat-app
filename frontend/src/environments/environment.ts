export const environment = {
  "production": false,
  "oidc": {
    "issuer": "http://localhost:8081/realms/demo",
    "clientId": "chat-app",
    "redirectUri": "http://localhost:8888/",
    "scope": "openid profile email",
    "silentRefreshRedirectUri": "http://localhost:8888/silent-refresh.html",
    "postLogoutRedirectUri": "http://localhost:8888/"
  },
  "apiBaseUrl": "http://localhost:9080"
} as const;
