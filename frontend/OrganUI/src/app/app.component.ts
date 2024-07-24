import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {filter} from 'rxjs';
import {LoginDataService} from './services/login-data.service';
import {map} from 'rxjs/operators';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, AsyncPipe],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  linkText: string = '';
  title: string = '';
  greeting: string = '';
  username: string = '';

  constructor(
    private router: Router,
    private loginDataService: LoginDataService,
  ) {
  }

  ngOnInit(): void {
    this.loginDataService.username$
      .pipe(map((name) => name.charAt(0).toUpperCase() + name.slice(1).toLowerCase()))
      .subscribe((username) => {
        this.username = username;
        this.updateGreeting();
      });

    this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe(() => {
      this.linkText = this.router.isActive('/dashboard', true) ? 'Log out' : '';
      this.title = this.router.isActive('/dashboard', true) ? 'Dancing Pipes' : 'Login';
      this.updateGreeting();
    });
  }

  private updateGreeting() {
    this.greeting = this.router.isActive('/dashboard', true) ? `Hello, ${this.username}!` : '';
  }
}
