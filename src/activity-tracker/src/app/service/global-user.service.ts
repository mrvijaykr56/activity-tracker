import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GlobalUserService {
  private readonly USER_KEY = 'tracker_user';
  private user = this.loadUser();

  // Method to set user data
  setUser(userData: Partial<any>): void {
    this.user = { ...this.user, ...userData }; // Update user data
    localStorage.setItem(this.USER_KEY, JSON.stringify(this.user));
  }

  // Method to get user data
  getUser(): any {
    return this.user; // Return the stored user data
  }

  isLoggedIn(): boolean {
    return !!(this.user && this.user.id !== null && this.user.id !== undefined && this.user.id !== '');
  }

  private loadUser(): any {
    const savedUser = localStorage.getItem(this.USER_KEY);
    return savedUser ? JSON.parse(savedUser) : {
      id: '',
      age: '',
      firstname: '',
      lastname: '',
      username: '',
      password: '',
    };
  }

  // Method to clear user data (e.g., on logout)
  clearUser(): void {
    this.user = {
      id: '',
      age: '',
      firstname: '',
      lastname: '',
      username: '',
      password: '',
    };
    localStorage.removeItem(this.USER_KEY);
  }
}