import {Component, OnInit} from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {DataService} from "./service/data.service";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
  title: string | undefined;
  constructor(private dataService:DataService) {
  }

  ngOnInit(): void {
    this.dataService.getTitle().subscribe(title=>{
      this.title=title;
    })
  }

}

