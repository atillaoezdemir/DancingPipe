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
//This component dedicated to displaying and managing settings related to the organ's operational parameters.
export class OrganSettingsComponent implements OnInit, OnDestroy {
  webClientData: WebClientDTO | undefined;
  //In keyboards Array length represents number of actual keyboards that Organ has.
  // If u want to change it -> change getKeyboardName() also!
  keyboards: any[] = Array(5).fill('disabled');
  selectedTempoLabel: number = TempoLabels.NORMAL;
  subscription: Subscription | undefined;
  consumerIsConnected: boolean = false;
  startCommandReceived: boolean = false;
  barLength: number = 0;
  title: string = 'stopped';
  composerName: string = 'stopped';


  constructor(
    private ngZone: NgZone,
    private sseService: SseService,
  ) {
  }

  //Initiates a subscription to the SseService to receive updates on the organ settings.
  //Processes incoming data within Angular's NgZone to ensure proper UI rendering and state management.
  ngOnInit(): void {
    this.subscription = this.sseService.getWebClientData().subscribe({
      next: (data: WebClientDTO) => {
        this.ngZone.run(() => {
          this.webClientData = data;
          this.consumerIsConnected = data.consumerConnected;
          this.selectedTempoLabel = data.currentTempo;
          this.updateKeyboards();
          this.startCommandReceived = data.startCommandReceived;
          this.barLength = data.barLength;
          this.title = data.title;
          this.composerName = data.composerName;
        });
      },
      error: (error) => console.error('Error receiving SSE loginData$:', error),
    });
  }

  //Dynamically adjusts the visual representation of the keyboards based on the current data.
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

  //Returns the appropriate image path based on the keyboard's status,
  //aiding in creating a visually intuitive interface.
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

//Maps each keyboard index to its corresponding name.
  getKeyboardName(index: number): string {
    switch (index) {
      case 0:
        return 'Great';
      case 1:
        return 'Swell';
      case 2:
        return 'Choir';
      case 3:
        return 'Pedal';
      case 4:
        return 'Solo/Echo';

      default:
        return 'Invalid index';
    }
  }

  //manage cleanup logic for component
  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
