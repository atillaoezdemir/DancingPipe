import {Component, NgZone, OnInit} from '@angular/core';
import {SseService} from '../services/sse.service';
import {WebClientDTO} from '../models/web-client-dto';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {MatButton} from '@angular/material/button';
import {CommandDisplayNamePipe} from "../pipes/command-display-name.pipe";

@Component({
  selector: 'app-number-display',
  templateUrl: './number-display.component.html',
  styleUrls: ['./number-display.component.css'],
  imports: [NgClass, MatButton, NgForOf, NgIf, CommandDisplayNamePipe],
  standalone: true,
})
//This component designed to display real-time command data related to the operation of the Organ Sequencer.
export class NumberDisplayComponent implements OnInit {
  webClientData: WebClientDTO[] = [];
  displayedData: WebClientDTO[] = [];
  showAll: boolean = false;
  private maxDisplay = 10;

  constructor(
    private sseService: SseService,
    private zone: NgZone,
  ) {
  }

  //Sets up the subscription to the SSE data stream, initializing the component's state with live data from the server.
  ngOnInit() {
    this.sseService.getWebClientData().subscribe({
      next: (dto: WebClientDTO) => {
        if (dto.keyboardsInUse > 0 || dto.command === "stop") {
          this.zone.run(() => {
            this.webClientData.unshift(dto);
            this.updateDisplayedData();
          });
        }
      },
      error: (error) => console.error('Error received from SSE stream:', error),
    });
  }

  //Sets up the subscription to the SSE data stream, initializing the component's state with live data from the server.
  toggleView() {
    this.showAll = !this.showAll;
    this.updateDisplayedData();
  }

  //Clears the current command data from the display, offering a way to reset the view or manage data visibility.
  clearData() {
    this.webClientData = [];
    this.displayedData = [];
  }

  //Manages which data is shown based on user interactions, such as toggling between full and limited views.
  private updateDisplayedData() {
    this.displayedData = this.showAll ? [...this.webClientData] : this.webClientData.slice(0, this.maxDisplay);
  }
}
