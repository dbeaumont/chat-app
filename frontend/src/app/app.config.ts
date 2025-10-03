// src/app/app.config.ts
import { ApplicationConfig, APP_INITIALIZER, inject, importProvidersFrom } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { OAuthModule, OAuthService, AuthConfig } from 'angular-oauth2-oidc';
import { authBearerInterceptor } from './auth-bearer.interceptor';

// ⚠️ Mets ces valeurs en phase avec frontend/src/environments/environment*.ts
const authConfig: AuthConfig = {
  issuer: 'http://localhost:8081/realms/demo',
  clientId: 'chat-app',
  redirectUri: 'http://localhost:8888/',
  responseType: 'code',              // Code + PKCE
  scope: 'openid profile email',
  requireHttps: false,               // dev only (HTTP)
  showDebugInformation: false
};

// Déclenche la redirection si aucun token n’est trouvé.
export function initAuth() {
  const oauth = inject(OAuthService);
  return async () => {
    oauth.configure(authConfig);
    await oauth.loadDiscoveryDocumentAndTryLogin();
    if (!oauth.hasValidAccessToken()) {
      await oauth.initLoginFlow();   // 🔑 redirection vers Keycloak
    }
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptors([authBearerInterceptor])),
    importProvidersFrom(
      OAuthModule.forRoot({
        resourceServer: {
          sendAccessToken: true,
          allowedUrls: ['http://localhost:9080']
        }
      })
    ),
    { provide: APP_INITIALIZER, useFactory: initAuth, multi: true }
  ]
};