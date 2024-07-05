import {Component, NgZone, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {SseService} from '../service/sse.service';
import {WebClientDTO} from '../model/web-client-dto';
import {MatSlider, MatSliderThumb} from '@angular/material/slider';
import {NgClass, NgForOf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MatDivider} from '@angular/material/divider';
import {TempoLabels} from '../model/enums';
import {TempoPipe} from '../pipes/TempoPipe';

@Component({
  selector: 'app-organ-settings',
  templateUrl: './organ-settings.component.html',
  styleUrls: ['./organ-settings.component.css'],
  imports: [MatSlider, MatSliderThumb, NgForOf, FormsModule, NgClass, MatDivider, TempoPipe],
  standalone: true,
})
export class OrganSettingsComponent implements OnInit, OnDestroy {
  webClientData: WebClientDTO | undefined;
  keyboards: any[] = Array(5).fill('disabled');
  selectedTempoLabel: number = TempoLabels.NORMAL;
  private subscription: Subscription | undefined;
  consumerIsConnected: boolean = false;
  startCommandReceived: boolean = false;

  constructor(
    private ngZone: NgZone,
    private sseService: SseService,
  ) {}

  ngOnInit(): void {
    this.subscription = this.sseService.getWebClientData().subscribe({
      next: (data: WebClientDTO) => {
        this.ngZone.run(() => {
          this.webClientData = data;
          this.consumerIsConnected = data.consumerIsConnected;
          this.selectedTempoLabel = data.currentTempo;
          this.updateKeyboards();
          if (data.command == 'start' && data.consumerIsConnected) {
            this.startCommandReceived = true;
          }
          if ((data.command == 'stop' && data.consumerIsConnected) || !data.consumerIsConnected) {
            this.startCommandReceived = false;
          }
        });
      },
      error: (error) => console.error('Error receiving SSE loginData:', error),
    });
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  updateKeyboards(): void {
    this.keyboards = Array(5).fill('disabled');

    if (this.webClientData) {
      let max: number = this.webClientData.maxAvailableKeyboards < 0 ? 0 : this.webClientData.maxAvailableKeyboards;
      let inUse: number = this.webClientData.keyboardsInUse < 0 ? 0 : this.webClientData.keyboardsInUse;
      for (let i = 0; i < inUse; i++) {
        this.keyboards[i] = 'active';
      }

      for (let i = inUse; i < max; i++) {
        this.keyboards[i] = 'inactive';
      }
    }
  }

  getKeyboardImage(index: number): string {
    const status = this.keyboards[index];
    switch (status) {
      case 'active':
        return 'assets/active keyboard.png';
      case 'inactive':
        return 'assets/inactive keyboard.png';
      default:
        return 'assets/disabled keyboard.png';
    }
  }
}
