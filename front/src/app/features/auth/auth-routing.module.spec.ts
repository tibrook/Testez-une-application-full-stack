import { TestBed, fakeAsync, tick, async } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { AuthRoutingModule } from './auth-routing.module';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { Component } from '@angular/core';

@Component({ template: '' })
class DummyComponent {}

describe('AuthRoutingModule', () => {
  let router: Router;
  let location: Location;

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'login', component: LoginComponent },
          { path: 'register', component: RegisterComponent }
        ]),
        AuthRoutingModule
      ],
      declarations: [LoginComponent, RegisterComponent, DummyComponent]
    });

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    TestBed.createComponent(DummyComponent); // Create a component to attach the router outlet
    router.initialNavigation();
  }));

  it('should navigate to "login" loads LoginComponent', fakeAsync(() => {
    router.navigate(['login']);
    tick();
    expect(location.path()).toBe('/login');
    expect(router.url).toBe('/login');
  }));

  it('should navigate to "register" loads RegisterComponent', fakeAsync(() => {
    router.navigate(['register']);
    tick();
    expect(location.path()).toBe('/register');
    expect(router.url).toBe('/register');
  }));
});
