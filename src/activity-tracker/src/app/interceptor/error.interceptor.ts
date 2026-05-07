import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { catchError, map, Observable, throwError } from 'rxjs';
import { NotificationService } from '../service/notification.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(private notificationService: NotificationService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      map((event: HttpEvent<any>) => {
        // We can extract data from ApiResponse here if we want to flatten it
        // But for now, we'll let the services handle the 'data' field
        return event;
      }),
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'An unexpected error occurred';

        if (error.error && typeof error.error === 'object') {
          const apiResponse = error.error;
          if (apiResponse.message) {
            errorMessage = apiResponse.message;
          }
          
          // Handle validation errors
          if (apiResponse.errors && typeof apiResponse.errors === 'object') {
            const fieldErrors = apiResponse.errors;
            const messages = Object.values(fieldErrors).join(', ');
            if (messages) {
              errorMessage = `${errorMessage}: ${messages}`;
            }
          } else if (typeof apiResponse.errors === 'string') {
            errorMessage = `${errorMessage}: ${apiResponse.errors}`;
          }
        } else {
          errorMessage = error.message || errorMessage;
        }

        this.notificationService.error(errorMessage);
        return throwError(() => new Error(errorMessage));
      })
    );
  }
}
