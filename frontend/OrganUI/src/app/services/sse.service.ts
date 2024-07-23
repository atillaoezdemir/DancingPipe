import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { WebClientDTO } from '../models/web-client-dto';
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root',
})
export class SseService implements OnDestroy {
  private baseUrl = `${environment.apiUrl}/web`;
  private eventSource: EventSource | null = null;
  private dataSubject: BehaviorSubject<WebClientDTO> = new BehaviorSubject<WebClientDTO>({
    keyboardsInUse: -1,
    maxAvailableKeyboards: -1,
    command: '',
    currentTempo: -1,
    wasCommandExecuted: false,
    consumerConnected: false,
    startCommandReceived:false,
    barLength: -1,
    title: 'stopped',
    composerName: 'stopped'

  });

  constructor(private zone: NgZone) {
    this.initEventSource(this.baseUrl);
  }

  private initEventSource(url: string): void {
    this.eventSource = new EventSource(url);
    this.eventSource.onmessage = (event) => {
      this.zone.run(() => {
        try {
          const data = JSON.parse(event.data) as WebClientDTO;
          this.dataSubject.next(data);
          console.log(data.composerName)
        } catch (error) {
          console.error('Error parsing loginData:', error);
          this.dataSubject.next(this.dataSubject.value);
        }
      });
    };

    this.eventSource.onerror = (error) => {
      this.zone.run(() => {
        console.error('SSE error:', error);
        this.dataSubject.next({
          command: '',
          currentTempo: -1,
          wasCommandExecuted: false,
          keyboardsInUse: -1,
          maxAvailableKeyboards: -1,
          consumerConnected: false,
          startCommandReceived: false,
          barLength: -1,
          title: 'stopped',
          composerName: 'stopped'
        });
      });
    };
  }

  getWebClientData(): Observable<WebClientDTO> {
    return this.dataSubject.asObservable();
  }

  ngOnDestroy(): void {
    if (this.eventSource) {
      this.eventSource.close();
    }
  }
}
