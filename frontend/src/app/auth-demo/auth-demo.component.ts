
import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-auth-demo',
  templateUrl: './auth-demo.component.html'
})
export class AuthDemoComponent implements OnInit {
  me: any = null;
  error: any = null;

  constructor(public auth: AuthService, private http: HttpClient) {}

  ngOnInit(): void {
    this.auth.init().then(() => {
      if (this.auth.isAuthenticated()) {
        this.loadMe();
      }
    });
  }

  login() { this.auth.login(); }
  logout() { this.auth.logout(); }

  loadMe() {
    this.http.get(environment.apiBaseUrl + '/api/me').subscribe({
      next: (data) => { this.me = data; this.error = null; },
      error: (err) => { this.error = err; this.me = null; }
    });
  }
}
