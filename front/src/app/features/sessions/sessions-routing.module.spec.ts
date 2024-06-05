import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { SessionsRoutingModule } from './sessions-routing.module';
import { DetailComponent } from './components/detail/detail.component';
import { FormComponent } from './components/form/form.component';
import { ListComponent } from './components/list/list.component';
import { Component } from '@angular/core';

@Component({ template: '' })
class DummyComponent {}

describe('SessionsRoutingModule', () => {
  let router: Router;
  let location: Location;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: '', component: ListComponent },
          { path: 'detail/:id', component: DetailComponent },
          { path: 'create', component: FormComponent },
          { path: 'update/:id', component: FormComponent }
        ]),
        SessionsRoutingModule
      ],
      declarations: [ListComponent, DetailComponent, FormComponent, DummyComponent]
    });

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    TestBed.createComponent(DummyComponent); // Create a component to attach the router outlet
    router.initialNavigation(); // Trigger the initial navigation
  });

  it('should navigate to "" and load ListComponent', fakeAsync(() => {
    router.navigate(['']);
    tick();
    expect(location.path()).toBe('/');
    expect(router.url).toBe('/');
  }));

  it('should navigate to "detail/:id" and load DetailComponent', fakeAsync(() => {
    router.navigate(['detail', 1]);
    tick();
    expect(location.path()).toBe('/detail/1');
    expect(router.url).toBe('/detail/1');
  }));

  it('should navigate to "create" and load FormComponent', fakeAsync(() => {
    router.navigate(['create']);
    tick();
    expect(location.path()).toBe('/create');
    expect(router.url).toBe('/create');
  }));

  it('should navigate to "update/:id" and load FormComponent', fakeAsync(() => {
    router.navigate(['update', 1]);
    tick();
    expect(location.path()).toBe('/update/1');
    expect(router.url).toBe('/update/1');
  }));
});
