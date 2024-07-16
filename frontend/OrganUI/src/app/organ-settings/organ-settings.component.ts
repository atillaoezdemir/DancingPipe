import {Component, NgZone, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from 'rxjs';
import {SseService} from '../services/sse.service';
import {WebClientDTO} from '../models/web-client-dto';
import {MatSlider, MatSliderThumb} from '@angular/material/slider';
import {NgClass, NgForOf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MatDivider} from '@angular/material/divider';
import {TempoLabels} from '../models/tempo-labels';
import {TempoPipe} from '../pipes/tempo-pipe.pipe';

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
  barLength:number= -1;
  title:string= 'stopped';
  composerName:string = 'stopped';

  constructor(
    private ngZone: NgZone,
    private sseService: SseService,
  ) {}

  ngOnInit(): void {
    this.subscription = this.sseService.getWebClientData().subscribe({
      next: (data: WebClientDTO) => {
        this.ngZone.run(() => {
          this.webClientData = data;
          this.consumerIsConnected = data.consumerConnected;
          this.selectedTempoLabel = data.currentTempo;
          this.updateKeyboards();
          this.startCommandReceived=data.startCommandReceived;
          this.barLength=data.barLength;
          console.log(this.barLength,data.barLength)
          this.title=data.title;
          console.log(this.title,data.title)
          this.composerName=data.composerName;
          console.log(this.composerName,data.composerName)
          // if (data.command == 'start' && data.consumerConnected) {
          //   this.startCommandReceived = true;
          // }
          // if ((data.command == 'stop' && data.consumerConnected) || !data.consumerConnected) {
          //   this.startCommandReceived = false;
          // }
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
        return 'active keyboard.png';
      case 'inactive':
        return 'inactive keyboard.png';
      default:
        return 'disabled keyboard.png';
    }
  }
}
