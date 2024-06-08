import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { UnauthGuard } from './unauth.guard';
import { SessionService } from '../services/session.service';
import { Router } from '@angular/router';
import { tick } from '@angular/core/testing';
import { Component } from '@angular/core';
import { fakeAsync } from '@angular/core/testing';
describe('UnauthGuard', () => {
  let unauthGuard: UnauthGuard;
  let router: Router;
  let sessionService: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'rentals', component: DummyComponent }
        ])
      ],
      providers: [
        UnauthGuard,
        SessionService 
      ]
    });

    unauthGuard = TestBed.inject(UnauthGuard);
    router = TestBed.inject(Router);
    sessionService = TestBed.inject(SessionService);
  });

  it('should redirect an authenticated user to the rentals page', fakeAsync(() => {
    sessionService.isLogged = true; 
    const spy = jest.spyOn(router, 'navigate');

    unauthGuard.canActivate();
    tick();

    expect(spy).toHaveBeenCalledWith(['rentals']);
  }));

  it('should allow access for an unauthenticated user', () => {
    sessionService.isLogged = false;
    expect(unauthGuard.canActivate()).toEqual(true);
    jest.spyOn(router, 'navigate');
    expect(router.navigate).not.toHaveBeenCalled();
  });
});

@Component({template: ''})
class DummyComponent {}
