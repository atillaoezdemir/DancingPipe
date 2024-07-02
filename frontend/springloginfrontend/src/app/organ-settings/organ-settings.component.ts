import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { SseService } from '../service/sse.service';
import { WebClientDTO } from '../model/web-client-dto';
import { MatSlider, MatSliderThumb } from "@angular/material/slider";
import {NgClass, NgForOf} from "@angular/common";
import { FormsModule } from "@angular/forms";

@Component({
  selector: 'app-organ-settings',
  templateUrl: './organ-settings.component.html',
  styleUrls: ['./organ-settings.component.css'],
  imports: [
    MatSlider,
    MatSliderThumb,
    NgForOf,
    FormsModule,
    NgClass
  ],
  standalone: true
})
export class OrganSettingsComponent implements OnInit, OnDestroy {
  webClientData: WebClientDTO | undefined;
  keyboards: any[] = Array(5).fill('disabled');
  selectedTempoLabel: string = 'Normal';
  private subscription: Subscription | undefined;
  isOnline: boolean = false;

  tempoLabels: { [key: number]: string } = {
    1: 'Very Slow',
    2: 'Slow',
    3: 'Normal',
    4: 'Fast',
    5: 'Very Fast'
  };

  constructor(private sseService: SseService) {}

  ngOnInit(): void {
    this.subscription = this.sseService.getWebClientData().subscribe({
      next: (data: WebClientDTO) => {
        this.webClientData = data;
        this.updateKeyboards();
        this.selectedTempoLabel = this.tempoLabels[data.currentTempo] || 'Normal';
      },
      error: (error) => console.error('Error receiving SSE data:', error)
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
      for (let i = 0; i < this.webClientData.maxAvailableKeyboards; i++) {
        this.keyboards[i] = 'inactive';
      }

      for (let i = 0; i < this.webClientData.keyboardsInUse; i++) {
        this.keyboards[i] = 'active';
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
