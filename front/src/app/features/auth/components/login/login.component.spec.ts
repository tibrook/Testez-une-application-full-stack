import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let httpMock: HttpTestingController;
  let authServiceMock: AuthService;
  let sessionServiceMock: SessionService;
  let fakeRouter: Router;
  beforeEach(async () => {
    fakeRouter = { navigate: jest.fn() } as any;

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        FormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        HttpClientTestingModule
      ],
      providers: [
        AuthService,
        SessionService,
        { provide: Router, useValue: fakeRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    authServiceMock = TestBed.inject(AuthService);
    sessionServiceMock = TestBed.inject(SessionService);
    fixture.detectChanges();
  });

  // Unit Test
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Unit Test
  it('should have all form fields necessary to login', () => {
    const nativeEl = fixture.nativeElement;

    const form = nativeEl.querySelector('.login-form');
    expect(form).toBeTruthy();

    const emailInput = form.querySelector('input[formcontrolname=email]');
    expect(emailInput).toBeTruthy();

    const passwordInput = form.querySelector('input[formcontrolname=password]');
    expect(passwordInput).toBeTruthy();
  });

  // Unit Test
  it('should have a valid form when all fields are filled', () => {
    component.form.controls['email'].setValue('john.doe@example.com');
    component.form.controls['password'].setValue('Password123');
    expect(component.form.valid).toBeTruthy();
  });

  // Unit Test
  it('should disable the submit button if the form is invalid', () => {
    const nativeEl = fixture.nativeElement;

    component.form.controls['email'].setValue('');
    fixture.detectChanges();

    const submitButton = nativeEl.querySelector('button[type="submit"]') as HTMLButtonElement;
    expect(submitButton.disabled).toBeTruthy();
  });

  // Integration Test
  it('should redirect to sessions page on successful login', () => {
    const navigateSpy = jest.spyOn(fakeRouter, 'navigate');

    component.form.controls['email'].setValue('john.doe@example.com');
    component.form.controls['password'].setValue('Password123');
    
    component.submit();

    const req = httpMock.expectOne({
      url: 'api/auth/login',
      method: 'POST'
    });

    req.flush({ token: 'fake-jwt-token', user: { id: 1, name: 'John Doe' } });

    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
  });

  // Integration Test
  it('should display an error message on login failure', () => {
    component.form.controls['email'].setValue('john.doe@example.com');
    component.form.controls['password'].setValue('Password123');
    
    component.submit();

    const req = httpMock.expectOne({
      url: 'api/auth/login',
      method: 'POST'
    });

    req.flush({ errorMessage: 'Bad credentials' }, { status: 401, statusText: 'Internal Server Error' });

    expect(component.onError).toBeTruthy();
  });
});
