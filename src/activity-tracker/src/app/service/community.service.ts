import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class CommunityService {
  private baseUrl = '/tracker/public/community';

  constructor(private http: HttpClient) { }

  getCommunityStats(): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.baseUrl}/stats`);
  }

  getLeaderboard(): Observable<ApiResponse<any[]>> {
    return this.http.get<ApiResponse<any[]>>('/tracker/public/leaderboard');
  }
}
