import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from 'src/app/service/login.service';
import { LoadingService } from 'src/app/service/loading.service';
import { NotificationService } from 'src/app/service/notification.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  user = {
    firstname: '',
    lastname: '',
    username: '',
    age: null,
    password: ''
  };

  isRegistering: boolean = false;

  constructor(
    private loginService: LoginService, 
    private router: Router,
    private loadingService: LoadingService,
    private notificationService: NotificationService
  ) { }

  onRegister() {
    this.isRegistering = true;
    this.loadingService.show();
    this.loginService.register(this.user).pipe(
      finalize(() => {
        this.loadingService.hide();
        this.isRegistering = false;
      })
    ).subscribe({
      next: (response) => {
        this.notificationService.success('Account created successfully! Please login.');
        this.router.navigate(['/']);
      },
      error: (err) => {
        console.error('Registration failed', err);
        // Error already handled by service
      }
    });
  }
}
