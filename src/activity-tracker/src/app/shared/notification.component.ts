import { Component, OnInit } from '@angular/core';
import { Notification, NotificationService } from '../service/notification.service';

@Component({
  selector: 'app-notification',
  template: `
    <div class="notification-container">
      <div *ngFor="let n of notifications" 
           class="glass-alert animate-slide-down" 
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
            <button type="button" class="btn-close btn-close-white ms-3" (click)="remove(n)" aria-label="Close"></button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .notification-container {
      position: fixed;
      top: 5.5rem; /* Below sticky header */
      left: 50%;
      transform: translateX(-50%);
      z-index: 110000;
      width: auto;
      min-width: 340px;
      max-width: 90vw;
      display: flex;
      flex-direction: column;
      align-items: center;
      pointer-events: none;
    }
    .glass-alert {
      pointer-events: auto;
      background: rgba(15, 23, 42, 0.85);
      backdrop-filter: blur(16px);
      -webkit-backdrop-filter: blur(16px);
      border: 1px solid var(--glass-border);
      border-radius: 1.25rem;
      padding: 0.85rem 1.5rem;
      margin-bottom: 0.75rem;
      box-shadow: 0 10px 30px -5px rgba(0, 0, 0, 0.5);
      color: white;
      overflow: hidden;
      position: relative;
      display: flex;
      align-items: center;
      transition: all 0.3s ease;
    }
    
    .glass-alert::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        height: 100%;
        width: 6px;
    }

    .alert-success { 
        background: rgba(16, 185, 129, 0.15); 
        border-color: rgba(16, 185, 129, 0.3);
    }
    .alert-error { 
        background: rgba(239, 68, 68, 0.15); 
        border-color: rgba(239, 68, 68, 0.3);
    }
    .alert-info { 
        background: rgba(59, 130, 246, 0.15); 
        border-color: rgba(59, 130, 246, 0.3);
    }
    .alert-warning { 
        background: rgba(245, 158, 11, 0.15); 
        border-color: rgba(245, 158, 11, 0.3);
    }

    .alert-success::before { background: var(--success); }
    .alert-error::before { background: var(--error); }
    .alert-info::before { background: var(--info); }
    .alert-warning::before { background: #f59e0b; }

    .alert-icon { font-size: 1.25rem; display: flex; align-items: center; }
    .alert-message { font-weight: 500; font-size: 0.95rem; }
    .alert-success .alert-icon { color: var(--success); }
    .alert-error .alert-icon { color: var(--error); }
    .alert-info .alert-icon { color: var(--info); }
    .alert-warning .alert-icon { color: #f59e0b; }

    .animate-slide-down {
      animation: slideDown 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards;
    }

    @keyframes slideDown {
      from { transform: translateY(-20px) scale(0.95); opacity: 0; }
      to { transform: translateY(0) scale(1); opacity: 1; }
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

