import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, takeUntil, finalize } from 'rxjs';
import { GlobalUserService } from 'src/app/service/global-user.service';
import { LoginService } from 'src/app/service/login.service';
import { LoadingService } from 'src/app/service/loading.service';
import { NotificationService } from 'src/app/service/notification.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnDestroy {
  username: string = '';
  password: string = '';
  isLoggingIn: boolean = false;

  private unsubscribeAll: Subject<void> = new Subject<void>();
  title = 'activitytracker';

  constructor(
    private service: LoginService,
    private router: Router,
    private globalUserService: GlobalUserService,
    private loadingService: LoadingService,
    private notificationService: NotificationService
  ) { }

  login() {
    this.isLoggingIn = true;
    this.loadingService.show();
    const loginData = { username: this.username, password: this.password };

    this.service.login(loginData).pipe(
      takeUntil(this.unsubscribeAll),
      finalize(() => {
        this.loadingService.hide();
        this.isLoggingIn = false;
      })
    ).subscribe({
      next: (response: any) => {
        if (response.status === 200 || response.message === 'Login Successful') {
          this.globalUserService.setUser(response.data.user);
          this.notificationService.success(response.message);
          this.router.navigate(['/home']);
        } else {
          this.notificationService.warning(response.message || 'Login failed');
        }
      },
      error: (error: any) => {
        console.error('Error occurred during login:', error);
        // Error is already handled by the service's notification call
      }
    });
  }

  ngOnDestroy(): void {
    this.unsubscribeAll.next();
    this.unsubscribeAll.complete();
  }
}