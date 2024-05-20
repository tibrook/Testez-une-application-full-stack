import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { By } from '@angular/platform-browser';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let httpMock: HttpTestingController;
  let authServiceMock: AuthService;
  let fakeRouter: Router;

  beforeEach(async () => {
    fakeRouter = { navigate: jest.fn() } as any;

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
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
        { provide: Router, useValue: fakeRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    authServiceMock = TestBed.inject(AuthService);
    fixture.detectChanges();
  });

  // Unit Test
  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
  // Unit Test
  it('should have all form fields necessary to register', () => {
    const nativeEl = fixture.nativeElement;

    const form = nativeEl.querySelector('.register-form');
    expect(form).toBeTruthy();

    const firstNameInput = form.querySelector('input[formcontrolname=firstName]');
    expect(firstNameInput).toBeTruthy();

    const lastNameInput = form.querySelector('input[formcontrolname=lastName]');
    expect(lastNameInput).toBeTruthy();

    const emailInput = form.querySelector('input[formcontrolname=email]');
    expect(emailInput).toBeTruthy();

    const passwordInput = form.querySelector('input[formcontrolname=password]');
    expect(passwordInput).toBeTruthy();
  });
  // Unit Test
  it('should have a valid form when all fields are filled', () => {
    component.form.controls['firstName'].setValue('John');
    component.form.controls['lastName'].setValue('Doe');
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
  it('should redirect to login page on successful registration', () => {
    const navigateSpy = jest.spyOn(fakeRouter, 'navigate');

    component.form.controls['firstName'].setValue('John');
    component.form.controls['lastName'].setValue('Doe');
    component.form.controls['email'].setValue('john.doe@example.com');
    component.form.controls['password'].setValue('Password123');
    
    component.submit();

    const req = httpMock.expectOne({
      url: 'api/auth/register',
      method: 'POST'
    });

    req.flush({});

    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });
});
