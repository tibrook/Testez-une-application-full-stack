import { TestBed, fakeAsync, tick, flush } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { LoginComponent } from '../components/login/login.component';
import { AuthService } from '../services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule} from '@angular/material/icon';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { of } from 'rxjs';
import { ListComponent } from '../../sessions/components/list/list.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
describe('LoginComponent Integration', () => {
  let component: LoginComponent;
  let fixture: any;
  let authService: AuthService;
  let router: Router;
  let location: Location;

  beforeEach(() => {
    const authServiceMock = {
      login: jest.fn().mockReturnValue(of({ id: 1, token: 'abc123', type: 'Bearer', username: 'user', firstName: 'John', lastName: 'Doe', admin: true }))
    };

    TestBed.configureTestingModule({
      imports: [
        MatCardModule,
        MatFormFieldModule,
        BrowserAnimationsModule,
        MatIconModule,
        MatInputModule,
        ReactiveFormsModule,
        FormsModule,
        RouterTestingModule.withRoutes([
          { path: 'sessions', component: ListComponent }
        ])
      ],
      declarations: [LoginComponent, ListComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock }
      ]
    });

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    fixture.detectChanges();
  });

  it('should login and navigate to "sessions"', fakeAsync(() => {
    expect(component).toBeTruthy();

    component.form.controls['email'].setValue('test@example.com');
    component.form.controls['password'].setValue('password123');
    fixture.detectChanges();

    component.submit();
    // simulate the passage of time until all promises are resolved
    tick(); 

    expect(authService.login).toHaveBeenCalledTimes(1);
    flush();
    // check if the redirection has taken place
    expect(location.path()).toBe('/sessions');
  }));
});
