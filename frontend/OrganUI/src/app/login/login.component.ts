import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginDataService } from '../services/login-data.service';

import { MatFormFieldModule, MatLabel } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { NgIf } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { Router } from '@angular/router';
import {environment} from "../../environments/environment";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    HttpClientModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatLabel,
    NgIf,
    MatIcon,
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  form!: FormGroup;
  isAuthenticatedFailed: boolean = false;
  hide = true;
  apiUrl = environment.apiUrl;

  constructor(
    private formBuilder: FormBuilder,
    private http: HttpClient,
    private loginDataService: LoginDataService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      login: ['guest', Validators.required],
      password: ['guest1', Validators.required],
    });
  }

  toggleVisibility() {
    this.hide = !this.hide;
  }

  onSubmit() {
    if (this.form.valid) {
      this.checkCredentials(this.form.value);
    }
  }

  checkCredentials(credentials: any) {
    const { login, password } = credentials;
    this.submitCredentials(login, password).subscribe(
      (isAuthenticated: boolean) => {
        if (isAuthenticated) {
          this.isAuthenticatedFailed = false;
          this.loginDataService.updateAuthentication(true);
          this.loginDataService.updateUsername(login);
          this.router
            .navigate(['/dashboard'])
            .then(() => console.log('Navigation to dashboard successful'))
            .catch((error) => console.error('Navigation to dashboard failed', error));
        } else {
          this.isAuthenticatedFailed = true;
        }
      },
      (error: any) => {
        console.error('Login failed with error:', error);
        this.isAuthenticatedFailed = true;
        this.form.reset();
      },
    );
  }

  submitCredentials(username: string, password: string): Observable<boolean> {
    const url = `${this.apiUrl}/web/login`;
    const body = { username: username, password: password };
    return this.http.post<boolean>(url, body);
  }
}
