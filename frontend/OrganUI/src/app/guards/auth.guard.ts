import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { LoginDataService } from '../services/login-data.service';
import { map } from 'rxjs/operators';

//This guard ensures that only authenticated users can access dashboard.
export const authGuard: CanActivateFn = () => {
  const dataService = inject(LoginDataService);
  const router = inject(Router);

  return dataService.loginData$.pipe(
    map((isAuthenticated) => {
      if (!isAuthenticated) {
        return router.createUrlTree(['/login']);
      }
      return true;
    }),
  );
};
