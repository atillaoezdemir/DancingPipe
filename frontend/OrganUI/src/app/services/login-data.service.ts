import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
//This service designed to manage user authentication.
export class LoginDataService {
  private isAuthenticated = new BehaviorSubject<boolean>(false);
  private username = new BehaviorSubject<string>('');

  loginData$ = this.isAuthenticated.asObservable();
  username$ = this.username.asObservable();

  //Updates whether the user is authenticated.
  updateAuthentication(data: boolean) {
    this.isAuthenticated.next(data);
  }

//Updates whether the user is authenticated.
  updateUsername(newUsername: string) {
    this.username.next(newUsername);
  }
}
