import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { StatisticsService } from 'src/app/service/statistics.service';
import { GamificationService } from 'src/app/service/gamification.service';
import { GoalService } from 'src/app/service/goal.service';
import { CommunityService } from 'src/app/service/community.service';
import { DeviceSyncService } from 'src/app/service/device-sync.service';
import { Category } from 'src/app/models/category.enum';
import { Chart, registerables } from 'chart.js';
Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, AfterViewInit {
  @ViewChild('categoryChart') categoryChartRef!: ElementRef;
  @ViewChild('trendChart') trendChartRef!: ElementRef;
  
  summaryData: any = null;
  gamificationData: any = null;
  
  // Caching
  private static cachedSummary: any = null;
  private static lastFetchTime: number = 0;
  private readonly CACHE_DURATION = 60000; // 60 seconds
  goals: any[] = [];
  wellnessTips: string[] = [];
  communityStats: any = null;
  leaderboard: any[] = [];
  categories = Object.values(Category);
  
  newGoal = { category: '', targetCount: 1 };
  isLoading: boolean = true;
  isSettingGoal: boolean = false;
  isSyncing: boolean = false;
  syncResult: any = null;

  constructor(
    private statisticsService: StatisticsService,
    private gamificationService: GamificationService,
    private goalService: GoalService,
    private communityService: CommunityService,
    private deviceSyncService: DeviceSyncService
  ) {}

  ngOnInit(): void {
    this.fetchData();
  }

  fetchData(): void {
    const now = Date.now();
    if (DashboardComponent.cachedSummary && (now - DashboardComponent.lastFetchTime < this.CACHE_DURATION)) {
      this.summaryData = DashboardComponent.cachedSummary;
      this.isLoading = false;
      this.fetchGamification();
      return;
    }

    this.isLoading = true;
    this.statisticsService.getSummary().subscribe({
      next: (response) => {
        this.summaryData = response.data;
        DashboardComponent.cachedSummary = response.data;
        DashboardComponent.lastFetchTime = now;
        this.fetchGamification();
      },
      error: () => this.isLoading = false
    });
  }

  fetchGamification(): void {
    this.gamificationService.getGamificationStatus().subscribe({
      next: (response) => {
        this.gamificationData = response.data;
        this.fetchGoals();
      },
      error: () => this.isLoading = false
    });
  }

  fetchGoals(): void {
    this.goalService.getGoals().subscribe({
      next: (response) => {
        this.goals = response.data;
        this.fetchCommunityStats();
      },
      error: () => this.isLoading = false
    });
  }

  fetchCommunityStats(): void {
    this.communityService.getCommunityStats().subscribe({
      next: (response) => {
        this.communityStats = response.data;
        this.fetchLeaderboard();
      },
      error: () => this.fetchLeaderboard()
    });
  }

  fetchLeaderboard(): void {
    this.communityService.getLeaderboard().subscribe({
      next: (response) => {
        this.leaderboard = response.data;
        this.fetchAiTips();
      },
      error: () => this.fetchAiTips()
    });
  }

  fetchAiTips(): void {
    this.statisticsService.getWellnessTips().subscribe({
      next: (response) => {
        this.wellnessTips = response.data;
        this.isLoading = false;
        setTimeout(() => {
          this.initCategoryChart();
          this.initTrendChart();
        }, 0);
      },
      error: () => this.isLoading = false
    });
  }

  onSync(deviceName: string): void {
    this.isSyncing = true;
    this.deviceSyncService.syncWithDevice(deviceName).subscribe({
      next: (response) => {
        this.isSyncing = false;
        this.syncResult = response.data;
        // In a real app, we would refresh the activity list too
      },
      error: () => this.isSyncing = false
    });
  }

  onSetGoal(): void {
    if (!this.newGoal.category) return;
    
    this.isSettingGoal = true;
    this.goalService.setGoal(this.newGoal).subscribe({
      next: () => {
        this.isSettingGoal = false;
        this.fetchGoals();
      },
      error: () => this.isSettingGoal = false
    });
  }

  getProgressPercentage(goal: any): number {
    const percentage = (goal.currentCount / goal.targetCount) * 100;
    return Math.min(percentage, 100);
  }

  ngAfterViewInit(): void {
    // Charts will be initialized after data is fetched
  }

  initCategoryChart(): void {
    if (!this.categoryChartRef || !this.summaryData) return;

    const ctx = this.categoryChartRef.nativeElement.getContext('2d');
    const labels = Object.keys(this.summaryData.categoryDistribution);
    const data = Object.values(this.summaryData.categoryDistribution);

    new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          data: data,
          backgroundColor: [
            'rgba(99, 102, 241, 0.8)',
            'rgba(16, 185, 129, 0.8)',
            'rgba(251, 113, 133, 0.8)',
            'rgba(59, 130, 246, 0.8)',
            'rgba(245, 158, 11, 0.8)'
          ],
          borderColor: 'rgba(255, 255, 255, 0.1)',
          borderWidth: 2
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom',
            labels: {
              color: '#ffffff',
              padding: 20,
              font: {
                size: 14,
                family: "'Outfit', sans-serif"
              }
            }
          }
        }
      }
    });
  }

  initTrendChart(): void {
    if (!this.trendChartRef || !this.summaryData) return;

    const ctx = this.trendChartRef.nativeElement.getContext('2d');
    const rawData = this.summaryData.dailyDistribution;
    const labels = Object.keys(rawData).reverse();
    const data = Object.values(rawData).reverse();

    new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Activities',
          data: data,
          backgroundColor: 'rgba(99, 102, 241, 0.6)',
          borderColor: 'rgba(99, 102, 241, 1)',
          borderWidth: 1,
          borderRadius: 5
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            grid: {
              color: 'rgba(255, 255, 255, 0.05)'
            },
            ticks: {
              color: '#94a3b8',
              stepSize: 1
            }
          },
          x: {
            grid: {
              display: false
            },
            ticks: {
              color: '#94a3b8'
            }
          }
        },
        plugins: {
          legend: {
            display: false
          }
        }
      }
    });
  }
}
