import { Component, OnInit } from '@angular/core';
import { ConfirmData, ConfirmService } from '../service/confirm.service';

@Component({
  selector: 'app-confirm',
  template: `
    <div class="confirm-overlay" *ngIf="activeData" (click)="onCancel()">
      <div class="glass-card confirm-modal animate-fade-in" (click)="$event.stopPropagation()">
        <div class="confirm-header">
          <h3 class="mb-0 text-high-contrast">{{ activeData.title }}</h3>
          <button class="btn-close btn-close-white" (click)="onCancel()" aria-label="Close"></button>
        </div>
        <div class="confirm-body">
          <p class="text-readable-secondary mb-0">{{ activeData.message }}</p>
        </div>
        <div class="confirm-footer">
          <button class="btn btn-link text-readable-secondary text-decoration-none me-3 hover-effect" (click)="onCancel()">
            {{ activeData.cancelText }}
          </button>
          <button class="glass-btn px-4 py-2" (click)="onConfirm()">
            {{ activeData.confirmText }}
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .confirm-overlay {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.4);
      backdrop-filter: blur(8px);
      -webkit-backdrop-filter: blur(8px);
      z-index: 120000;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 1.5rem;
    }
    .confirm-modal {
      width: 100%;
      max-width: 450px;
      padding: 2.5rem !important;
      background: rgba(15, 23, 42, 0.8) !important;
      border: 1px solid rgba(255, 255, 255, 0.15) !important;
    }
    .confirm-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
    }
    .confirm-body {
      margin-bottom: 2.5rem;
      font-size: 1.1rem;
      line-height: 1.6;
    }
    .confirm-footer {
      display: flex;
      justify-content: flex-end;
      align-items: center;
    }
  `]
})
export class ConfirmComponent implements OnInit {
  activeData: ConfirmData | null = null;

  constructor(private confirmService: ConfirmService) {}

  ngOnInit() {
    this.confirmService.confirm$.subscribe(data => {
      this.activeData = data;
    });
  }

  onConfirm() {
    if (this.activeData) {
      this.activeData.resolve(true);
      this.activeData = null;
    }
  }

  onCancel() {
    if (this.activeData) {
      this.activeData.resolve(false);
      this.activeData = null;
    }
  }
}
