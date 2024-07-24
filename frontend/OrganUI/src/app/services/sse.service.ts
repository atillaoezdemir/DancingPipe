import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { WebClientDTO } from '../models/web-client-dto';
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root',
})
//The SseService is an Angular service designed to handle real-time data connections using Server-Sent Events (SSE).
//Plays a key role in ensuring that the user interface displays the most current system information.
export class SseService implements OnDestroy {
  private baseUrl = `${environment.apiUrl}/web`;
  private eventSource: EventSource | null = null;
  private dataSubject: BehaviorSubject<WebClientDTO> = new BehaviorSubject<WebClientDTO>({
    keyboardsInUse: 0,
    maxAvailableKeyboards: 0,
    command: '',
    currentTempo: 0,
    wasCommandExecuted: false,
    consumerConnected: false,
    startCommandReceived:false,
    barLength: 0,
    title: 'stopped',
    composerName: 'stopped'

  });

  constructor(private zone: NgZone) {
    this.initEventSource(this.baseUrl);
  }

  //Initializes the SSE connection to the server.
  private initEventSource(url: string): void {
    this.eventSource = new EventSource(url);
    this.eventSource.onmessage = (event) => {
      this.zone.run(() => {
        try {
          const data = JSON.parse(event.data) as WebClientDTO;
          this.dataSubject.next(data);
          console.log(data.composerName)
        } catch (error) {
          console.error('Error parsing loginData$:', error);
          this.dataSubject.next(this.dataSubject.value);
        }
      });
    };
//Creation of Dummy State on Error
    this.eventSource.onerror = (error) => {
      this.zone.run(() => {
        console.error('SSE error:', error);
        this.dataSubject.next({
          command: '',
          currentTempo: 0,
          wasCommandExecuted: false,
          keyboardsInUse: 0,
          maxAvailableKeyboards: 0,
          consumerConnected: false,
          startCommandReceived: false,
          barLength: 0,
          title: 'stopped',
          composerName: 'stopped'
        });
      });
    };
  }

//Provides an observable that components can subscribe to receive updates.
  getWebClientData(): Observable<WebClientDTO> {
    return this.dataSubject.asObservable();
  }

//manage cleanup logic for component
  ngOnDestroy(): void {
    if (this.eventSource) {
      this.eventSource.close();
    }
  }
}
