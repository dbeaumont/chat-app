import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageService, Message } from './message.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app.component.html'
})
export class AppComponent {
  private api = inject(MessageService);
  input = signal('');
  messages = signal<Message[]>([]);
  loading = signal(false);

  constructor() { this.refresh(); }

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
}