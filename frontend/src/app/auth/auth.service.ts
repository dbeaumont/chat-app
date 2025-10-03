
import { Injectable } from '@angular/core';
import { OAuthService, OAuthEvent, AuthConfig, OAuthSuccessEvent } from 'angular-oauth2-oidc';
import { filter, map } from 'rxjs/operators';
import { authConfig } from './auth.config';

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(private oauth: OAuthService) {}

  init(): Promise<void> {
    this.oauth.configure(authConfig as AuthConfig);
    this.oauth.setupAutomaticSilentRefresh();
    return this.oauth.loadDiscoveryDocumentAndTryLogin().then(() => {
      if (!this.isAuthenticated()) {
        // optionally trigger login automatically
      }
    });
  }

  login(): void {
    this.oauth.initLoginFlow();
  }

  logout(): void {
    this.oauth.logOut();
  }

  isAuthenticated(): boolean {
    return this.oauth.hasValidAccessToken();
  }

  get accessToken(): string | null {
    return this.oauth.getAccessToken() || null;
  }

  claims(): any {
    const c = this.oauth.getIdentityClaims();
    return c || null;
  }

  events$() {
    return this.oauth.events.pipe(filter((e: OAuthEvent) => !!e));
  }
}
