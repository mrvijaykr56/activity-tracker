import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Activity } from '../models/activity.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class HomeService {
  private url = '/tracker/users/activity';

  constructor(private http: HttpClient) { }

  getAllActivity(page: number = 0, size: number = 5, search: string = ''): Observable<ApiResponse<any>> {
    let query = `?page=${page}&size=${size}`;
    if (search) {
      query += `&search=${encodeURIComponent(search)}`;
    }
    return this.http.get<ApiResponse<any>>(`${this.url}/my-activities${query}`);
  }

  saveActivity(data: any): Observable<ApiResponse<Activity>> {
    return this.http.post<ApiResponse<Activity>>(`${this.url}/add`, data);
  }

  updateActivity(id: number, data: any): Observable<ApiResponse<Activity>> {
    return this.http.put<ApiResponse<Activity>>(`${this.url}/${id}`, data);
  }

  deleteActivity(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.url}/${id}`);
  }
}