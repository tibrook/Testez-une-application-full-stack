import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { SessionService } from 'src/app/services/session.service';
import { of, throwError } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule} from '@angular/material/icon';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatInputModule } from '@angular/material/input';
describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let mockAuthService: any;
  let mockRouter: any;
  let mockSessionService: any;

  beforeEach(async () => {
    mockAuthService = {
      login: jest.fn()
    };
    mockRouter = {
      navigate: jest.fn()
    };
    mockSessionService = {
      logIn: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule,MatCardModule,MatFormFieldModule,MatIconModule,MatInputModule,BrowserAnimationsModule],
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
        { provide: SessionService, useValue: mockSessionService },
        FormBuilder
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should handle successful login', () => {
    const response = { token: '12345' };
    mockAuthService.login.mockReturnValue(of(response));
    component.submit();
    expect(mockSessionService.logIn).toHaveBeenCalledWith(response);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should handle login error', () => {
    mockAuthService.login.mockReturnValue(throwError(() => new Error('Failed')));
    component.submit();
    expect(component.onError).toBeTruthy();
  });
});
