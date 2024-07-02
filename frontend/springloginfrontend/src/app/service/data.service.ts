import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private isAuthenticated = new BehaviorSubject<boolean>(false);
  data = this.isAuthenticated.asObservable();
  private title = new BehaviorSubject<string>('Login');
  titleData = this.title.asObservable();

  updateAuthentication(data: boolean) {
    this.isAuthenticated.next(data);
  }

  updateTitle(data: string) {
    this.title.next(data);
  }

  getTitle(): Observable<string> {
    return this.titleData;
  }
}
