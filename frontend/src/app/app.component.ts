import { Component, inject, signal } from '@angular/core';
import { CommonModule, KeyValue } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { OAuthService } from 'angular-oauth2-oidc';
import { MessageService, Message } from './message.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app.component.html'
})
export class AppComponent {
  private api = inject(MessageService);
  private oauth = inject(OAuthService);
  input = signal('');
  messages = signal<Message[]>([]);
  loading = signal(false);
  isAuthenticated = signal(false);
  displayName = signal<string | null>(null);
  userClaims = signal<Record<string, unknown> | null>(null);
  menuOpen = signal(false);
  profileVisible = signal(false);
  readonly claimSorter = (a: KeyValue<string, unknown>, b: KeyValue<string, unknown>) =>
    a.key.localeCompare(b.key);

  constructor() {
    this.syncSessionState();
    if (this.isAuthenticated()) {
      this.refresh();
    }

    this.oauth.events
      .pipe(takeUntilDestroyed())
      .subscribe(() => {
        const wasAuthenticated = this.isAuthenticated();
        this.syncSessionState();
        if (!wasAuthenticated && this.isAuthenticated()) {
          this.refresh();
        }
      });
  }

  refresh() {
    this.loading.set(true);
    this.api.list().subscribe({
      next: (data) => { this.messages.set(data); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  send() {
    const text = this.input().trim();
    if (!text) return;
    this.api.post(text).subscribe({
      next: (m) => { this.messages.set([...this.messages(), m]); this.input.set(''); },
    });
  }

  signOut() {
    this.menuOpen.set(false);
    this.profileVisible.set(false);
    this.oauth.logOut();
  }

  toggleMenu() {
    if (!this.isAuthenticated()) {
      return;
    }
    this.menuOpen.update((value) => !value);
  }

  openProfile() {
    if (!this.userClaims()) {
      return;
    }
    this.profileVisible.set(true);
    this.menuOpen.set(false);
  }

  closeProfile() {
    this.profileVisible.set(false);
  }

  private syncSessionState() {
    const authenticated = this.oauth.hasValidAccessToken();
    this.isAuthenticated.set(authenticated);
    const claims = authenticated
      ? (this.oauth.getIdentityClaims() as Record<string, unknown> | null)
      : null;
    this.userClaims.set(claims);
    this.displayName.set(this.resolveDisplayName(claims));
    if (!authenticated) {
      this.menuOpen.set(false);
      this.profileVisible.set(false);
    }
  }

  private resolveDisplayName(claims: Record<string, unknown> | null): string | null {
    if (!claims) {
      return null;
    }
    const nameLikeFields = ['name', 'preferred_username', 'given_name', 'email'];
    for (const field of nameLikeFields) {
      const value = claims[field];
      if (typeof value === 'string' && value.trim().length > 0) {
        return value;
      }
    }
    return 'Utilisateur';
  }
}
