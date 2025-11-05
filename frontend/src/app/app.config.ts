// src/app/app.config.ts
import { ApplicationConfig, APP_INITIALIZER, inject, importProvidersFrom } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { OAuthModule, OAuthService, AuthConfig } from 'angular-oauth2-oidc';
import { authBearerInterceptor } from './auth-bearer.interceptor';
import { environment } from '../environments/environment';

const authConfig: AuthConfig = {
  issuer: environment.oidc.issuer,
  clientId: environment.oidc.clientId,
  redirectUri: environment.oidc.redirectUri,
  postLogoutRedirectUri: environment.oidc.postLogoutRedirectUri,
  responseType: 'code',              // Code + PKCE
  scope: environment.oidc.scope,
  requireHttps: environment.production,
  showDebugInformation: !environment.production
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
          allowedUrls: [environment.apiBaseUrl]
        }
      })
    ),
    { provide: APP_INITIALIZER, useFactory: initAuth, multi: true }
  ]
};
