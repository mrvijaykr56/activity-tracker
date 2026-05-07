import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private url = '/tracker/users';

  constructor(private http: HttpClient) { }

  getUsers(): Observable<ApiResponse<any[]>> {
    return this.http.get<ApiResponse<any[]>>(`${this.url}`);
  }

  login(data: any): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.url}/login`, data);
  }

  register(data: any): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.url}`, data);
  }
}