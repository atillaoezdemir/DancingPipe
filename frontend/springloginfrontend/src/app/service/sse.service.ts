import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { WebClientDTO } from '../model/web-client-dto';

@Injectable({
  providedIn: 'root',
})
export class SseService implements OnDestroy {
  private baseUrl = 'http://localhost:8080/web';
  private eventSource: EventSource | null = null;
  private dataSubject: BehaviorSubject<WebClientDTO> = new BehaviorSubject<WebClientDTO>({
    keyboardsInUse: -1,
    maxAvailableKeyboards: -1,
    command: '',
    currentTempo: -1,
    wasCommandExecuted: false,
    consumerIsConnected: false,
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
          consumerIsConnected: false,
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
