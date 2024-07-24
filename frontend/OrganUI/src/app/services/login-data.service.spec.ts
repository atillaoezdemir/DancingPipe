import {TestBed} from '@angular/core/testing';
import {LoginDataService} from './login-data.service';
import {take} from 'rxjs/operators';

describe('LoginDataService', () => {
  let service: LoginDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoginDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should update authentication status', (done: DoneFn) => {
    service.updateAuthentication(true);
    service.loginData$.pipe(take(1)).subscribe((isAuthenticated) => {
      expect(isAuthenticated).toBeTrue();
      done();
    });
  });

  it('should update username', (done: DoneFn) => {
    const testUsername = 'testUser';
    service.updateUsername(testUsername);
    service.username$.pipe(take(1)).subscribe((username) => {
      expect(username).toBe(testUsername);
      done();
    });
  });

  it('should emit initial authentication status as false', (done: DoneFn) => {
    service.loginData$.pipe(take(1)).subscribe((isAuthenticated) => {
      expect(isAuthenticated).toBeFalse();
      done();
    });
  });

  it('should emit initial username as an empty string', (done: DoneFn) => {
    service.username$.pipe(take(1)).subscribe((username) => {
      expect(username).toBe('');
      done();
    });
  });
});
