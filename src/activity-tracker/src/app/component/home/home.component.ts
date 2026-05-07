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

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  user: any = null;
  private unsubscribeAll: Subject<void> = new Subject<void>();

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
    private notificationService: NotificationService
  ) {
    this.user = this.globalUserService.getUser();
    this.activityForm = this.fb.group({
      activityName: ['', [Validators.required, Validators.minLength(3)]],
      category: ['', Validators.required],
      timeDuration: ['', Validators.required],
      date: ['', [Validators.required, Validators.pattern(/^\d{2}-\d{2}-\d{4}$/)]],
      days: ['', Validators.required]
    });
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

  fetchSavedActivityList(page: number = this.currentPage): void {
    const userId = this.user?.id ? Number(this.user.id) : null;
    if (!userId) return;

    this.currentPage = page;
    this.loadingService.show();
    this.homeService.getAllActivity(userId, this.currentPage, this.pageSize).pipe(
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
      ...this.activityForm.value,
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
        this.fetchSavedActivityList(0); // Reset to first page
        this.activityForm.reset({ category: '', days: '' });
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
    this.activityForm.reset({ category: '', days: '' });
  }

  deleteActivity(id: number) {
    if (!confirm('Are you sure you want to delete this activity?')) return;

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

  ngOnDestroy(): void {
    this.unsubscribeAll.next();
    this.unsubscribeAll.complete();
  }
}
