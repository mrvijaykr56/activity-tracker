import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GlobalUserService {
  private readonly USER_KEY = 'tracker_user';
  private readonly TOKEN_KEY = 'tracker_token';
  private user = this.loadUser();

  // Method to set user data
  setUser(userData: any): void {
    this.user = userData;
    localStorage.setItem(this.USER_KEY, JSON.stringify(this.user));
  }

  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  // Method to get user data
  getUser(): any {
    return this.user; // Return the stored user data
  }

  isLoggedIn(): boolean {
    return !!this.getToken() && !!(this.user && this.user.id);
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
    this.user = null;
    localStorage.removeItem(this.USER_KEY);
    localStorage.removeItem(this.TOKEN_KEY);
  }
}