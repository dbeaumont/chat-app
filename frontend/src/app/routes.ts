// src/app/routes.ts
import { Routes } from '@angular/router';
import { MessagesComponent } from './messages/messages.component';
import { AuthDemoComponent } from './auth-demo/auth-demo.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'messages' },
  { path: 'auth', component: AuthDemoComponent },
  { path: 'messages', component: MessagesComponent },
  { path: '**', redirectTo: 'messages' }
];
