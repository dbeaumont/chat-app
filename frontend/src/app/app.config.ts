// src/app/app.config.ts
import { ApplicationConfig, APP_INITIALIZER, inject, importProvidersFrom } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';
import { OAuthModule, OAuthService, AuthConfig } from 'angular-oauth2-oidc';
import { authBearerInterceptor } from './auth-bearer.interceptor';
import { routes } from './routes';
import { environment } from '../environments/environment';

export function initAuthFactory() {
  const oauth = inject(OAuthService);
  const authConfig: AuthConfig = {
    issuer: environment.oidc.issuer,
    clientId: environment.oidc.clientId,
    redirectUri: environment.oidc.redirectUri,
    postLogoutRedirectUri: environment.oidc.postLogoutRedirectUri,
    responseType: 'code', // Authorization Code + PKCE
    scope: environment.oidc.scope,
    requireHttps: false, // dev only; set true in prod with HTTPS
    showDebugInformation: false,
    strictDiscoveryDocumentValidation: false
  };
  return async () => {
    oauth.configure(authConfig);
    // With code flow + refresh tokens, no silent-refresh page needed
    await oauth.loadDiscoveryDocumentAndTryLogin();
    // Optionally, trigger login if not authenticated
    // if (!oauth.hasValidAccessToken()) { oauth.initLoginFlow(); }
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideAnimations(),
    provideHttpClient(withInterceptors([authBearerInterceptor])),
    importProvidersFrom(OAuthModule.forRoot({
      resourceServer: { sendAccessToken: true, allowedUrls: [environment.apiBaseUrl] }
    })),
    { provide: APP_INITIALIZER, multi: true, useFactory: initAuthFactory }
  ]
};
