import { environment } from '../environments/environment';
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface Message { id: number; text: string; createdAt: string; }

@Injectable({ providedIn: 'root' })
export class MessageService {
  private http = inject(HttpClient);
  private base = (window as any).API_BASE_URL || environment.apiBaseUrl;

  list() {
    return this.http.get<Message[]>(`${this.base}/api/messages`);
  }
  post(text: string) {
    return this.http.post<Message>(`${this.base}/api/messages`, { text });
  }
}
