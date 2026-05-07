import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, delay } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class DeviceSyncService {

  private baseUrl = '/tracker/users/sync';

  constructor(private http: HttpClient) { }

  syncWithDevice(deviceName: string): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.baseUrl}/${deviceName}`, {});
  }

  getConnectedDevices(): Observable<ApiResponse<string[]>> {
    // This could also be an API call in the future
    return of({
      timestamp: new Date().toISOString(),
      status: 200,
      message: 'Connected devices retrieved',
      data: ['Apple Health', 'Fitbit'],
      errors: null
    });
  }
}
