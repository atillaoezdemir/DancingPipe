import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, Observable, of, startWith, tap} from "rxjs";
import {ConnectionsStatusDTO} from "../model/connections-status-dto";
import {OrganSettingsDto} from "../model/organ-settings-dto";

@Injectable({
  providedIn: 'root'
})
export class ConnectionStatusService {
  //todo remove hardcode url
  private apiUrl = 'http://localhost:8080/web';

  constructor(private http: HttpClient) {
  }

  getStatus(): Observable<ConnectionsStatusDTO> {
    const defaultValue: ConnectionsStatusDTO = {producer: false, consumer: false};
    return this.http.get<ConnectionsStatusDTO>(`${this.apiUrl}/status`).pipe(
      tap(data => console.log('Fetched data:', data)),
      startWith(defaultValue),
      catchError((error) => {
        console.error('Error fetching connection status:', error);
        return of(defaultValue);
      })
    );
  }

  getSettings(): Observable<OrganSettingsDto> {
    return this.http.get<OrganSettingsDto>(`${this.apiUrl}/settings`);
  }
}
