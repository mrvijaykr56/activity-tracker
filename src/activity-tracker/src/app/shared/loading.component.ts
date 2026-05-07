import { Component } from '@angular/core';
import { LoadingService } from '../service/loading.service';

@Component({
  selector: 'app-loading',
  template: `
    <div *ngIf="loadingService.loading$ | async" class="loading-overlay">
      <div class="loader-container">
        <div class="custom-spinner"></div>
        <div class="loading-text">Synchronizing...</div>
      </div>
    </div>
  `,
  styles: [`
    .loading-overlay {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(15, 23, 42, 0.8);
      backdrop-filter: blur(8px);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 100000;
    }
    .loader-container {
      text-align: center;
    }
    .custom-spinner {
      width: 60px;
      height: 60px;
      border: 4px solid var(--glass-border);
      border-top: 4px solid #6366f1;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin: 0 auto 1.5rem;
      box-shadow: 0 0 20px rgba(99, 102, 241, 0.2);
    }
    .loading-text {
      color: white;
      font-weight: 600;
      letter-spacing: 0.1em;
      text-transform: uppercase;
      font-size: 0.8rem;
    }
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
  `]
})
export class LoadingComponent {
  constructor(public loadingService: LoadingService) {}
}

