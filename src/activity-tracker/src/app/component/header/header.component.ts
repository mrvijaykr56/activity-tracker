import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { GlobalUserService } from 'src/app/service/global-user.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {

  constructor(public globalUserService: GlobalUserService, private router: Router) { }

  logout() {
    this.globalUserService.clearUser();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return this.globalUserService.isLoggedIn();
  }
}
