import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReactiveFormsModule} from '@angular/forms';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {LoginComponent} from './login.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {provideRouter, Router} from '@angular/router';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {LoginDataService} from '../services/login-data.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let httpTestingController: HttpTestingController;
  let loginDataService: jasmine.SpyObj<LoginDataService>;
  let router: Router;

  beforeEach(async () => {
    const loginDataServiceSpy = jasmine.createSpyObj('LoginDataService', ['updateAuthentication', 'updateUsername']);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        BrowserAnimationsModule,
        LoginComponent
      ],
      providers: [
        {provide: LoginDataService, useValue: loginDataServiceSpy},
        provideHttpClientTesting(),
        provideRouter([{path: 'dashboard', redirectTo: ''}])
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    httpTestingController = TestBed.inject(HttpTestingController);
    loginDataService = TestBed.inject(LoginDataService) as jasmine.SpyObj<LoginDataService>;
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });


  it('should toggle password visibility', () => {
    expect(component.hide).toBeTrue();
    component.toggleVisibility();
    expect(component.hide).toBeFalse();
    component.toggleVisibility();
    expect(component.hide).toBeTrue();
  });

  it('should not submit the form if it is invalid', () => {
    component.form.controls['login'].setValue('');
    component.onSubmit();
    expect(component.form.valid).toBeFalse();
    expect(component.isAuthenticatedFailed).toBeFalse(); // No change
  });


});
