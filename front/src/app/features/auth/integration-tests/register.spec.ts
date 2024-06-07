import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from '../components/register/register.component';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { of, throwError } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { LoginComponent } from '../components/login/login.component';
import { NgZone } from '@angular/core';
import { NavigationExtras } from '@angular/router';
describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([
          { path: 'sessions', component: LoginComponent } 
        ]),
        MatFormFieldModule,
        MatInputModule,
        BrowserAnimationsModule,
        NoopAnimationsModule,
        MatCardModule
      ],
      declarations: [ RegisterComponent , LoginComponent],
      providers: [
        AuthService,
        FormBuilder,
        {
          provide: Router,
          useFactory: (ngZone: NgZone) => ({
            navigate: jest.fn((commands: any[], extras?: NavigationExtras) => ngZone.run(() => router.navigate(commands, extras)))
          }),
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should handle successful registration and navigate to login', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    jest.spyOn(authService, 'register').mockReturnValue(of(undefined)); // Mock the registration call to succeed

    component.form.setValue({
      email: 'test@example.com',
      firstName: 'Test',
      lastName: 'User',
      password: 'password123'
    });

    component.submit();
    await fixture.whenStable();

    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });

  it('should handle registration error and show error message', () => {
    jest.spyOn(authService, 'register').mockReturnValue(throwError(() => new Error('Registration failed')));
    component.form.setValue({
      email: 'fail@example.com',
      firstName: 'Fail',
      lastName: 'User',
      password: 'password123'
    });

    component.submit();
    fixture.detectChanges();

    expect(component.onError).toBeTruthy();
    expect(fixture.nativeElement.querySelector('.error').textContent).toContain('An error occurred');
  });
});
