import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { firstValueFrom } from 'rxjs';
import { MessageService, Message } from '../message.service';

@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    MatSnackBarModule, MatCardModule, MatListModule,
    MatProgressBarModule, MatButtonModule,
    MatFormFieldModule, MatInputModule, MatIconModule
  ],
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.scss']
})
export class MessagesComponent implements OnInit {
  private readonly pageSize = 10;
  loading = signal(false);
  data = signal<Message[]>([]);
  page = signal(1);
  draft: string = '';

  paged = computed(() => {
    const start = (this.page() - 1) * this.pageSize;
    return this.data().slice(start, start + this.pageSize);
  });
  pageCount = computed(() => Math.max(1, Math.ceil(this.data().length / this.pageSize)));

  constructor(private api: MessageService, private snack: MatSnackBar) {}

  async ngOnInit() { await this.fetch(); }

  async fetch() {
    this.loading.set(true);
    try {
      const res = await firstValueFrom(this.api.list());
      this.data.set(res ?? []);
      this.page.set(1);
    } catch (e) {
      this.snack.open('Erreur lors du chargement des messages', 'Fermer', { duration: 4000 });
    } finally {
      this.loading.set(false);
    }
  }

  async send() {
    const text = (this.draft || '').trim();
    if (!text) { return; }
    this.loading.set(true);
    try {
      const created = await firstValueFrom(this.api.post(text));
      // Insert at the top (assuming newest first); adjust if backend sorts differently
      this.data.set([created, ...this.data()]);
      this.draft = '';
      this.page.set(1);
      this.snack.open('Message envoyé', 'Fermer', { duration: 2500 });
    } catch (e) {
      this.snack.open('Échec de l\'envoi', 'Fermer', { duration: 4000 });
    } finally {
      this.loading.set(false);
    }
  }

  next() { if (this.page() < this.pageCount()) this.page.set(this.page() + 1); }
  prev() { if (this.page() > 1) this.page.set(this.page() - 1); }
}
