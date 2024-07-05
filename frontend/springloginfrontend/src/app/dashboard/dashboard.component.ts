import { Component } from '@angular/core';
import { NumberDisplayComponent } from '../number-display/number-display.component';
import { OrganSettingsComponent } from '../organ-settings/organ-settings.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NumberDisplayComponent, OrganSettingsComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent {}
