import { Component, OnInit } from '@angular/core';
import { Notification, NotificationService } from '../service/notification.service';

@Component({
  selector: 'app-notification',
  template: `
    <div class="notification-container">
      <div *ngFor="let n of notifications" 
           class="glass-alert animate-slide-in" 
           [class.alert-success]="n.type === 'success'"
           [class.alert-error]="n.type === 'error'"
           [class.alert-info]="n.type === 'info'"
           [class.alert-warning]="n.type === 'warning'"
           role="alert">
        <div class="d-flex align-items-center">
            <div class="alert-icon me-3">
                <i *ngIf="n.type === 'success'" class="bi bi-check-circle-fill"></i>
                <i *ngIf="n.type === 'error'" class="bi bi-exclamation-triangle-fill"></i>
                <i *ngIf="n.type === 'info'" class="bi bi-info-circle-fill"></i>
                <i *ngIf="n.type === 'warning'" class="bi bi-exclamation-circle-fill"></i>
            </div>
            <div class="alert-message flex-grow-1">{{ n.message }}</div>
            <button type="button" class="btn-close btn-close-white ms-2" (click)="remove(n)" aria-label="Close"></button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .notification-container {
      position: fixed;
      top: 2rem;
      right: 2rem;
      z-index: 110000;
      width: 380px;
      max-width: calc(100vw - 4rem);
    }
    .glass-alert {
      background: rgba(30, 41, 59, 0.7);
      backdrop-filter: blur(12px);
      border: 1px solid var(--glass-border);
      border-radius: 1rem;
      padding: 1rem 1.25rem;
      margin-bottom: 1rem;
      box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.4);
      color: white;
      overflow: hidden;
      position: relative;
    }
    .glass-alert::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        height: 100%;
        width: 4px;
    }
    .alert-success::before { background: var(--success); }
    .alert-error::before { background: var(--error); }
    .alert-info::before { background: #3b82f6; }
    .alert-warning::before { background: #f59e0b; }

    .alert-icon { font-size: 1.25rem; }
    .alert-success .alert-icon { color: var(--success); }
    .alert-error .alert-icon { color: var(--error); }
    .alert-info .alert-icon { color: #3b82f6; }
    .alert-warning .alert-icon { color: #f59e0b; }

    .animate-slide-in {
      animation: slideIn 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards;
    }

    @keyframes slideIn {
      from { transform: translateX(100%) scale(0.9); opacity: 0; }
      to { transform: translateX(0) scale(1); opacity: 1; }
    }
  `]
})
export class NotificationComponent implements OnInit {
  notifications: Notification[] = [];

  constructor(private notificationService: NotificationService) {}

  ngOnInit() {
    this.notificationService.notifications$.subscribe(n => {
      this.notifications.push(n);
      setTimeout(() => this.remove(n), 5000);
    });
  }

  remove(n: Notification) {
    this.notifications = this.notifications.filter(x => x !== n);
  }
}

