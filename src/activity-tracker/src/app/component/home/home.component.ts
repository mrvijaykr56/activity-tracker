import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject, takeUntil, finalize } from 'rxjs';
import { Category } from 'src/app/models/category.enum';
import { Day } from 'src/app/models/day.enum';
import { GlobalUserService } from 'src/app/service/global-user.service';
import { HomeService } from 'src/app/service/home.service';
import { LoadingService } from 'src/app/service/loading.service';
import { NotificationService } from 'src/app/service/notification.service';
import { ConfirmService } from 'src/app/service/confirm.service';

import { CommunityService } from 'src/app/service/community.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  user: any = null;
  private unsubscribeAll: Subject<void> = new Subject<void>();

  // Preview Data for Guest Landing
  communityStats: any = null;
  leaderboard: any[] = [];
  mockActivities = [
    { activityName: 'Morning Yoga', category: 'EXERCISE', timeDuration: '00:30', date: '2026-05-07', days: 'THURSDAY' },
    { activityName: 'Deep Work Session', category: 'WORK', timeDuration: '02:00', date: '2026-05-07', days: 'THURSDAY' },
    { activityName: 'Guitar Practice', category: 'HOBBY', timeDuration: '00:45', date: '2026-05-06', days: 'WEDNESDAY' }
  ];

  // Reactive Form
  activityForm: FormGroup;

  // Enums
  categories = Object.values(Category);
  daysList = Object.values(Day);

  // Pagination Data
  savedActivityListData: Array<any> = [];
  currentPage: number = 0;
  pageSize: number = 5;
  totalElements: number = 0;
  totalPages: number = 0;
  searchText: string = '';

  // State
  isSaving: boolean = false;
  isUpdating: boolean = false;
  editingActivityId: number | null = null;
  deletingActivityId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private homeService: HomeService, 
    private globalUserService: GlobalUserService,
    private router: Router,
    private loadingService: LoadingService,
    private notificationService: NotificationService,
    private confirmService: ConfirmService,
    private communityService: CommunityService
  ) {
    this.user = this.globalUserService.getUser();
    this.activityForm = this.fb.group({
      activityName: ['', [Validators.required, Validators.minLength(3)]],
      category: ['', Validators.required],
      timeDuration: ['', Validators.required],
      date: ['', [Validators.required]],
      days: ['', Validators.required]
    });
    this.setDefaultDateAndDay();
  }

  ngOnInit(): void {
    if (this.isLoggedIn()) {
      this.fetchSavedActivityList();
    } else {
      this.fetchPublicData();
    }
  }

  fetchPublicData(): void {
    this.communityService.getCommunityStats().subscribe({
      next: (response) => this.communityStats = response.data
    });
    this.communityService.getLeaderboard().subscribe({
      next: (response) => this.leaderboard = response.data
    });
  }

  isLoggedIn(): boolean {
    return this.globalUserService.isLoggedIn();
  }

  getCategoryCount(category: string): number {
    return this.savedActivityListData.filter(a => a.category === category).length;
  }


  fetchSavedActivityList(page: number = this.currentPage): void {
    this.currentPage = page;
    this.loadingService.show();
    this.homeService.getAllActivity(this.currentPage, this.pageSize, this.searchText).pipe(
      takeUntil(this.unsubscribeAll),
      finalize(() => this.loadingService.hide())
    ).subscribe({
      next: (response) => {
        const pageData = response.data;
        this.savedActivityListData = pageData.content;
        this.totalElements = pageData.totalElements;
        this.totalPages = pageData.totalPages;
      },
      error: () => {}
    });
  }

  onSearch() {
    this.fetchSavedActivityList(0);
  }

  onPageChange(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.fetchSavedActivityList(page);
    }
  }

  onSubmit() {
    if (this.activityForm.invalid) {
      this.activityForm.markAllAsTouched();
      this.notificationService.warning('Please fill in all fields correctly!');
      return;
    }

    if (this.editingActivityId) {
      this.updateActivity();
    } else {
      this.addActivity();
    }
  }

  addActivity() {
    const activityToSave = {
      ...this.activityForm.value
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
        this.fetchSavedActivityList(0); // Reset to first page
        this.resetForm();
      },
      error: () => {}
    });
  }

  editActivity(activity: any) {
    this.editingActivityId = activity.id;
    this.activityForm.patchValue({
      activityName: activity.activityName,
      category: activity.category,
      timeDuration: activity.timeDuration,
      date: activity.date,
      days: activity.days
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  updateActivity() {
    if (!this.editingActivityId) return;

    this.isUpdating = true;
    this.loadingService.show();
    this.homeService.updateActivity(this.editingActivityId, this.activityForm.value).pipe(
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
    this.resetForm();
  }

  private resetForm() {
    this.activityForm.reset({ category: '', days: '' });
    this.setDefaultDateAndDay();
  }

  private setDefaultDateAndDay() {
    const today = new Date();
    
    // Format Date: YYYY-MM-DD (Native date picker requirement)
    const day = String(today.getDate()).padStart(2, '0');
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const year = today.getFullYear();
    const formattedDate = `${year}-${month}-${day}`;
    
    // Format Day: SUNDAY, MONDAY, etc. (Matches Day enum)
    const days = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
    const currentDay = days[today.getDay()];
    
    this.activityForm.patchValue({
      date: formattedDate,
      days: currentDay
    });
  }

  async deleteActivity(id: number) {
    const confirmed = await this.confirmService.confirm(
      'Delete Activity',
      'Are you sure you want to delete this activity? This action cannot be undone.'
    );

    if (!confirmed) return;

    this.deletingActivityId = id;
    this.homeService.deleteActivity(id).pipe(
      takeUntil(this.unsubscribeAll),
      finalize(() => this.deletingActivityId = null)
    ).subscribe({
      next: (response) => {
        this.notificationService.success(response.message || "Activity deleted successfully");
        // If current page becomes empty and it's not the first page, go back
        if (this.savedActivityListData.length === 1 && this.currentPage > 0) {
          this.fetchSavedActivityList(this.currentPage - 1);
        } else {
          this.fetchSavedActivityList();
        }
      },
      error: () => {}
    });
  }

  exportToCsv(): void {
    if (this.savedActivityListData.length === 0) {
      this.notificationService.warning('No activities to export!');
      return;
    }

    // CSV Headers
    const headers = ['Activity Name', 'Category', 'Duration', 'Date', 'Day'];
    
    // Fetch all data for export (since current list is paginated)
    // For simplicity, we'll export the currently visible page or fetch all if possible.
    // In a real app, you'd call a special "export" endpoint.
    // Here we'll export the current list as a demonstration.
    
    const csvRows = this.savedActivityListData.map(activity => [
      `"${activity.activityName}"`,
      `"${activity.category}"`,
      `"${activity.timeDuration}"`,
      `"${activity.date}"`,
      `"${activity.days}"`
    ].join(','));

    const csvContent = [headers.join(','), ...csvRows].join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute('download', `activities_export_${new Date().toISOString().split('T')[0]}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    this.notificationService.success('Activities exported to CSV successfully!');
  }

  ngOnDestroy(): void {
    this.unsubscribeAll.next();
    this.unsubscribeAll.complete();
  }
}
