import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { GlobalUserService } from 'src/app/service/global-user.service';
import { GamificationService } from 'src/app/service/gamification.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  streak: number = 0;

  constructor(
    public globalUserService: GlobalUserService, 
    private router: Router,
    private gamificationService: GamificationService
  ) { }

  ngOnInit(): void {
    if (this.isLoggedIn()) {
      this.fetchStreak();
    }
  }

  fetchStreak(): void {
    this.gamificationService.getGamificationStatus().subscribe({
      next: (response) => {
        this.streak = response.data.streak;
      },
      error: () => {}
    });
  }

  logout() {
    this.globalUserService.clearUser();
    this.router.navigate(['/login']);
  }

  isLoggedIn(): boolean {
    return this.globalUserService.isLoggedIn();
  }
}
