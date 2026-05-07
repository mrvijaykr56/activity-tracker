import { Component } from '@angular/core';
import { trigger, transition, style, query, animate, group } from '@angular/animations';
import { GlobalUserService } from './service/global-user.service';
import { Router } from '@angular/router';

const slideInAnimation = trigger('routeAnimations', [
  transition('* <=> *', [
    style({ position: 'relative' }),
    query(':enter, :leave', [
      style({
        position: 'absolute',
        top: 0,
        left: 0,
        width: '100%',
        opacity: 0
      })
    ], { optional: true }),
    query(':enter', [
      style({ opacity: 0, transform: 'translateY(10px)' })
    ], { optional: true }),
    group([
      query(':leave', [
        animate('300ms ease-out', style({ opacity: 0, transform: 'translateY(-10px)' }))
      ], { optional: true }),
      query(':enter', [
        animate('400ms 100ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ], { optional: true })
    ])
  ])
]);

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  animations: [slideInAnimation]
})
export class AppComponent {
  constructor(private globalUserService: GlobalUserService, private router: Router) {}

  prepareRoute(outlet: any) {
    return outlet && outlet.activatedRouteData && outlet.activatedRouteData['animation'];
  }

  isLoggedIn(): boolean {
    return this.globalUserService.isLoggedIn();
  }

  logout(): void {
    this.globalUserService.clearUser();
    this.router.navigate(['/login']);
  }
}
