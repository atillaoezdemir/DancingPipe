import { Component } from '@angular/core';
import {ConnectionsStatusDTO} from "../model/connections-status-dto";
import {Observable} from "rxjs";
import {ConnectionStatusService} from "../service/connection-status.service";
import {AsyncPipe, NgIf} from "@angular/common";

@Component({
  selector: 'app-status-view',
  standalone: true,
  imports: [
    AsyncPipe,
    NgIf
  ],
  templateUrl: './status-view.component.html',
  styleUrl: './status-view.component.css'
})
export class StatusViewComponent {
  connections: Observable<ConnectionsStatusDTO>;

  constructor(private connectionStatusService:ConnectionStatusService ) {
    this.connections = this.connectionStatusService.getStatus();
  }

}
