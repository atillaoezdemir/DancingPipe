import {Injectable, NgZone} from '@angular/core';
import {Observable, BehaviorSubject} from 'rxjs';
import {map} from 'rxjs/operators';
import {WebClientDTO} from '../model/web-client-dto';

@Injectable({
  providedIn: 'root'
})
export class SseService {
  private baseUrl = 'http://localhost:8080/web';
  private eventSource: EventSource | null = null;
  private dataSubject: BehaviorSubject<WebClientDTO> = new BehaviorSubject<WebClientDTO>({
    keyboardsInUse: -1,
    maxAvailableKeyboards: -1,
    command: "",
    currentTempo: -1,
    wasCommandExecuted: false
  });

  constructor(private zone: NgZone) {
    this.initEventSource(this.baseUrl);
  }

  private initEventSource(url: string): void {
    this.eventSource = new EventSource(url);
    this.eventSource.onmessage = event => {
      this.zone.run(() => {
        try {
          const data = JSON.parse(event.data) as WebClientDTO;
          this.dataSubject.next(data);
        } catch (error) {
          console.error('Error parsing data:', error);
          this.dataSubject.next(this.dataSubject.value);
        }
      });
    };

    this.eventSource.onerror = error => {
      this.zone.run(() => {
        console.error('SSE error:', error);
        this.dataSubject.next({
          command: "",
          currentTempo: -1,
          wasCommandExecuted: false,
          keyboardsInUse: -1,
          maxAvailableKeyboards:-1
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
