import { Component, OnInit, NgZone } from '@angular/core';
import { SseService } from '../service/sse.service';
import { WebClientDTO } from '../model/web-client-dto';
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-number-display',
  templateUrl: './number-display.component.html',
  styleUrls: ['./number-display.component.css'],
  imports: [
    NgForOf
  ],
  standalone: true
})
export class NumberDisplayComponent implements OnInit {
  webClientData: WebClientDTO[] = [];

  constructor(private sseService: SseService, private zone: NgZone) {}

  ngOnInit() {
    this.sseService.getWebClientData().subscribe({
      next: (dto: WebClientDTO) => {
        if (dto.keyboardsInUse !== -1) {
          this.zone.run(() => {
            this.webClientData.unshift(dto);
          });
        }
      },
      error: (error) => {
        console.error('Error received from SSE stream:', error);
      }
    });
  }

  clearData() {
    this.webClientData = [];
  }
}
