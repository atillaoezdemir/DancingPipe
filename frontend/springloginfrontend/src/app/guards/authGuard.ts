import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { DataService } from '../service/data.service';
import { map } from 'rxjs/operators';

export const authGuard: CanActivateFn = () => {
  const dataService = inject(DataService);
  const router = inject(Router);

  return dataService.loginData.pipe(
    map((isAuthenticated) => {
      if (!isAuthenticated) {
        return router.createUrlTree(['/login']);
      }
      return true;
    }),
  );
};
