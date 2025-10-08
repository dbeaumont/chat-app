import { Component, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { OAuthService } from 'angular-oauth2-oidc';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-auth-demo',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatSnackBarModule, RouterLink],
  templateUrl: './auth-demo.component.html'
})
export class AuthDemoComponent implements OnInit {
  me = signal<any | null>(null);
  error = signal<any | null>(null);

  constructor(private snack: MatSnackBar, public oauth: OAuthService, private http: HttpClient) {}

  ngOnInit() {
    // Nothing
  }

  login() { this.oauth.initLoginFlow(); }
  logout() { this.oauth.logOut(); }
  refresh() { this.oauth.silentRefresh(); }

  loadMe() {
    this.http.get(environment.apiBaseUrl + '/api/me').subscribe({
      next: (data) => { this.me.set(data); this.error.set(null); this.toastOk('Profil chargé.'); },
      error: (err) => { this.error.set(err); this.me.set(null); this.toastErr('Erreur de chargement'); }
    });
  }

  private toastOk(msg:string){ this.snack.open(msg, 'Fermer', { duration: 2500 }); }
  private toastErr(msg:string){ this.snack.open(msg, 'Fermer', { duration: 4000 }); }
}
