import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LoginDataService {
  private isAuthenticated = new BehaviorSubject<boolean>(false);
  private username = new BehaviorSubject<string>('');

  loginData = this.isAuthenticated.asObservable();
  username$ = this.username.asObservable();

  updateAuthentication(data: boolean) {
    this.isAuthenticated.next(data);
  }

  updateUsername(newUsername: string) {
    this.username.next(newUsername);
  }
}
