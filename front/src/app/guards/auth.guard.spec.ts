import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { SessionService } from '../services/session.service';

describe('AuthGuard', () => {
  let authGuard: AuthGuard;
  let router: Router;
  let sessionService: SessionService;

  beforeEach(() => {
    const routerMock = { navigate: jest.fn() };
    const sessionServiceMock = { isLogged: false };

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: Router, useValue: routerMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ]
    });

    authGuard = TestBed.inject(AuthGuard);
    router = TestBed.inject(Router);
    sessionService = TestBed.inject(SessionService);
  });

  it('should redirect an unauthenticated user to the login page', () => {
    expect(authGuard.canActivate()).toEqual(false);
    expect(router.navigate).toHaveBeenCalledWith(['login']);
  });

  it('should allow the authenticated user to access the route', () => {
    sessionService.isLogged = true; // Simulate user is logged in
    expect(authGuard.canActivate()).toEqual(true);
    expect(router.navigate).not.toHaveBeenCalled(); // Ensure navigate was not called
  });
});
