import { Component, NgZone, OnInit } from '@angular/core';
import { SseService } from '../service/sse.service';
import { WebClientDTO } from '../model/web-client-dto';
import { NgClass, NgForOf } from '@angular/common';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-number-display',
  templateUrl: './number-display.component.html',
  styleUrls: ['./number-display.component.css'],
  imports: [NgClass, MatButton, NgForOf],
  standalone: true,
})
export class NumberDisplayComponent implements OnInit {
  webClientData: WebClientDTO[] = [];
  displayedData: WebClientDTO[] = [];
  showAll: boolean = false;
  private maxDisplay = 10;

  constructor(
    private sseService: SseService,
    private zone: NgZone,
  ) {}

  ngOnInit() {
    this.sseService.getWebClientData().subscribe({
      next: (dto: WebClientDTO) => {
        if (dto.keyboardsInUse !== -1) {
          this.zone.run(() => {
            this.webClientData.unshift(dto);
            this.updateDisplayedData();
          });
        }
      },
      error: (error) => console.error('Error received from SSE stream:', error),
    });
  }

  toggleView() {
    this.showAll = !this.showAll;
    this.updateDisplayedData();
  }

  clearData() {
    this.webClientData = [];
    this.displayedData = [];
  }

  private updateDisplayedData() {
    this.displayedData = this.showAll ? [...this.webClientData] : this.webClientData.slice(0, this.maxDisplay);
  }
}
