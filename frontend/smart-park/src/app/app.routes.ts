import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: 'home', loadComponent: () => import('./pages/search/search-page.component').then(m => m.SearchPageComponent) },
  { path: 'results', loadComponent: () => import('./pages/result/results-page.component').then(m => m.ResultsPageComponent) },
  { path: '**', redirectTo: 'home' }
];
