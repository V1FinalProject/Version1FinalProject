import { inject } from '@angular/core';
import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { nominatorGuard, reviewerGuard } from './core/guards/role.guard';
import { AuthService } from './core/services/auth.service';

export const routes: Routes = [
  {
    // Send people to the right home screen: coordinators to the dashboard,
    // everyone else to the form.
    path: '',
    pathMatch: 'full',
    redirectTo: () => {
      const user = inject(AuthService).user();
      if (!user) {
        return '/login';
      }
      return user.role === 'coordinator' ? '/review' : '/nominate';
    },
  },
  {
    path: 'login',
    title: 'Sign in · Star Awards',
    loadComponent: () => import('./features/login/login').then((m) => m.Login),
  },
  {
    path: 'nominate',
    title: 'Submit a nomination · Star Awards',
    canActivate: [authGuard, nominatorGuard],
    loadComponent: () => import('./features/nominate/nominate').then((m) => m.Nominate),
  },
  {
    path: 'review',
    title: 'Review dashboard · Star Awards',
    canActivate: [reviewerGuard],
    loadComponent: () => import('./features/review/review').then((m) => m.Review),
  },
  { path: '**', redirectTo: '' },
];
