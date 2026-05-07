import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, takeUntil, finalize } from 'rxjs';
import { Category } from 'src/app/models/category.enum';
import { Day } from 'src/app/models/day.enum';
import { GlobalUserService } from 'src/app/service/global-user.service';
import { HomeService } from 'src/app/service/home.service';
import { LoadingService } from 'src/app/service/loading.service';
import { NotificationService } from 'src/app/service/notification.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  user: { id: string, age: string; firstname: string; lastname: string; username: string; password: string } | null = null;
  images: string[] = [
    'assets/images/2.png',
    'assets/images/3.png',
    'assets/images/4.png'
  ];
  currentImageIndex: number = 0;
  private unsubscribeAll: Subject<void> = new Subject<void>();

  // Expose Enums to template
  categories = Object.values(Category);
  daysList = Object.values(Day);

  // Data
  savedActivityListData: Array<any> = [];
  searchText: string = '';

  // State
  isSaving: boolean = false;
  isUpdating: boolean = false;
  editingActivityId: number | null = null;
  deletingActivityId: number | null = null;

  newActivity = {
    activityName: '',
    category: '',
    timeDuration: '',
    date: '',
    days: ''
  };

  constructor(
    private homeService: HomeService, 
    private globalUserService: GlobalUserService,
    private router: Router,
    private loadingService: LoadingService,
    private notificationService: NotificationService
  ) {
    this.user = this.globalUserService.getUser();
  }

  ngOnInit(): void {
    if (this.isLoggedIn()) {
      this.fetchSavedActivityList();
    }
  }

  isLoggedIn(): boolean {
    return this.globalUserService.isLoggedIn();
  }

  get filteredActivities() {
    if (!this.searchText) return this.savedActivityListData;
    const search = this.searchText.toLowerCase();
    return this.savedActivityListData.filter(a => 
      a.activityName.toLowerCase().includes(search) || 
      a.category.toLowerCase().includes(search)
    );
  }

  fetchSavedActivityList(): void {
    const userId = this.user?.id ? Number(this.user.id) : null;
    if (!userId) {
      this.notificationService.error('User ID is invalid.');
      return;
    }

    this.loadingService.show();
    this.homeService.getAllActivity(userId).pipe(
      takeUntil(this.unsubscribeAll),
      finalize(() => this.loadingService.hide())
    ).subscribe({
      next: (response) => {
        this.savedActivityListData = response.data;
      },
      error: () => {}
    });
  }

  addActivity() {
    if (this.newActivity.activityName && this.newActivity.category && this.newActivity.timeDuration && this.newActivity.date && this.newActivity.days) {
      const activityToSave = {
        ...this.newActivity,
        user: { id: this.user?.id }
      };

      this.isSaving = true;
      this.loadingService.show();
      this.homeService.saveActivity(activityToSave).pipe(
        takeUntil(this.unsubscribeAll),
        finalize(() => {
          this.loadingService.hide();
          this.isSaving = false;
        })
      ).subscribe({
        next: (response) => {
          this.notificationService.success(response.message || "Activity saved successfully");
          this.fetchSavedActivityList();
          this.resetNewActivity();
        },
        error: () => {}
      });
    } else {
      this.notificationService.warning('Please fill in all fields!');
    }
  }

  editActivity(activity: any) {
    this.editingActivityId = activity.id;
    this.newActivity = { ...activity };
    // Scroll to form or highlight
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  updateActivity() {
    if (!this.editingActivityId) return;

    this.isUpdating = true;
    this.loadingService.show();
    this.homeService.updateActivity(this.editingActivityId, this.newActivity).pipe(
      takeUntil(this.unsubscribeAll),
      finalize(() => {
        this.loadingService.hide();
        this.isUpdating = false;
      })
    ).subscribe({
      next: (response) => {
        this.notificationService.success(response.message || "Activity updated successfully");
        this.cancelEdit();
        this.fetchSavedActivityList();
      },
      error: () => {}
    });
  }

  cancelEdit() {
    this.editingActivityId = null;
    this.resetNewActivity();
  }

  deleteActivity(id: number) {
    this.deletingActivityId = id;
    this.homeService.deleteActivity(id).pipe(
      takeUntil(this.unsubscribeAll),
      finalize(() => this.deletingActivityId = null)
    ).subscribe({
      next: (response) => {
        this.notificationService.success(response.message || "Activity deleted successfully");
        this.fetchSavedActivityList();
      },
      error: () => {}
    });
  }

  resetNewActivity() {
    this.newActivity = {
      activityName: '',
      category: '',
      timeDuration: '',
      date: '',
      days: ''
    };
  }

  ngOnDestroy(): void {
    this.unsubscribeAll.next();
    this.unsubscribeAll.complete();
  }
}
