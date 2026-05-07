import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

export interface ConfirmData {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  resolve: (value: boolean) => void;
}

@Injectable({
  providedIn: 'root'
})
export class ConfirmService {
  private confirmSubject = new Subject<ConfirmData>();
  confirm$ = this.confirmSubject.asObservable();

  confirm(title: string, message: string, confirmText: string = 'Confirm', cancelText: string = 'Cancel'): Promise<boolean> {
    return new Promise((resolve) => {
      this.confirmSubject.next({
        title,
        message,
        confirmText,
        cancelText,
        resolve
      });
    });
  }
}
