import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { UnauthGuard } from './unauth.guard';
import { SessionService } from '../services/session.service';

describe('UnauthGuard', () => {
  let unauthGuard: UnauthGuard;
  let router: Router;
  let sessionService: SessionService;

  beforeEach(() => {
    const routerMock = { navigate: jest.fn() };
    const sessionServiceMock = { isLogged: false };

    TestBed.configureTestingModule({
      providers: [
        UnauthGuard,
        { provide: Router, useValue: routerMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ]
    });

    unauthGuard = TestBed.inject(UnauthGuard);
    router = TestBed.inject(Router);
    sessionService = TestBed.inject(SessionService);
  });

  it('should redirect an authenticated user to the rentals page', () => {
    sessionService.isLogged = true; // Simulate user is logged in
    expect(unauthGuard.canActivate()).toEqual(false);
    expect(router.navigate).toHaveBeenCalledWith(['rentals']);
  });

  it('should allow access for an unauthenticated user', () => {
    sessionService.isLogged = false; // Simulate user is not logged in
    expect(unauthGuard.canActivate()).toEqual(true);
    expect(router.navigate).not.toHaveBeenCalled(); // Ensure navigate was not called
  });
});
