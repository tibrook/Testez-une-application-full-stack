import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { RouterTestingModule } from '@angular/router/testing';
import { AppRoutingModule } from './app-routing.module'; 
import { AppComponent } from './app.component'; 
import { AuthGuard } from './guards/auth.guard';
import { UnauthGuard } from './guards/unauth.guard';

describe('AppRoutingModule', () => {
  let router: Router;
  let location: Location;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]), 
        AppRoutingModule 
      ],
      declarations: [AppComponent],
      providers: [
        { provide: AuthGuard, useValue: { canActivate: () => true } },
        { provide: UnauthGuard, useValue: { canActivate: () => true } }
      ]
    });

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    router.initialNavigation();
  });

  it('navigate to "" should redirect to AuthModule (Lazy Loaded)', fakeAsync(() => {
    router.navigate(['']);
    tick(); 
    expect(location.path()).toBe('');
  }));

  it('navigate to "sessions" should use AuthGuard and load SessionsModule (Lazy Loaded)', fakeAsync(() => {
    router.navigate(['sessions']);
    tick();
    expect(location.path()).toBe('/sessions');
  }));

  it('navigate to "me" should use AuthGuard and show MeComponent', fakeAsync(() => {
    router.navigate(['me']);
    tick();
    expect(location.path()).toBe('/me');
  }));

  it('navigate to non-existent route should redirect to NotFoundComponent', fakeAsync(() => {
    router.navigate(['non-existent-route']);
    tick();
    expect(location.path()).toBe('/404');
  }));
});
