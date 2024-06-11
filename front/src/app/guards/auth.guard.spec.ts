import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { SessionService } from '../services/session.service';
import { Component } from '@angular/core';

describe('AuthGuard', () => {
  let authGuard: AuthGuard;
  let router: Router;
  let sessionService: SessionService;

  @Component({template: ''})
  class DummyComponent {}

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'login', component: DummyComponent },
          { path: 'protected', component: DummyComponent, canActivate: [AuthGuard] }
        ])
      ],
      providers: [
        AuthGuard,
        SessionService 
      ]
    });

    authGuard = TestBed.inject(AuthGuard);
    router = TestBed.inject(Router);
    sessionService = TestBed.inject(SessionService);
    jest.spyOn(router, 'navigate'); 
  });

  it('should redirect an unauthenticated user to the login page', fakeAsync(() => {
    sessionService.isLogged = false;
    router.navigate(['protected']); 
    tick(); 
    expect(router.url).toBe('/login'); 
  }));

  it('should allow an authenticated user to access the protected route', fakeAsync(() => {
    sessionService.isLogged = true;
    router.navigate(['protected']);
    tick(); 
    expect(router.url).toBe('/protected'); 
  }));
});
