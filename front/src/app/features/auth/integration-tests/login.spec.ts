import { TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { LoginComponent } from '../components/login/login.component';
import { AuthService } from '../services/auth.service';
import { SessionService } from 'src/app/services/session.service';
import { of, throwError } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { ComponentFixture } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { ListComponent } from '../../sessions/components/list/list.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let router: Router;
  let sessionService: SessionService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        RouterTestingModule.withRoutes([
          { path: 'sessions', component: ListComponent } 
        ]),
        HttpClientTestingModule,
        MatCardModule,
        MatFormFieldModule, BrowserAnimationsModule, MatInputModule,MatIconModule
      ],
      declarations: [ LoginComponent,ListComponent ],
      providers: [
        AuthService,
        SessionService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

 
  it('should handle successful login', async () => {
    const response = {
        token: '123abc',
        type: 'Bearer',
        id: 1,
        username: 'testUser',
        firstName: 'Test',
        lastName: 'User',
        admin: false
    };
    jest.spyOn(authService, 'login').mockReturnValue(of(response));
    jest.spyOn(sessionService, 'logIn').mockReturnValue();
    jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.form.setValue({ email: 'test@example.com', password: 'securepassword' });
    component.submit();

    await fixture.whenStable();

    expect(sessionService.logIn).toHaveBeenCalledWith(response);
    expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should handle login error', () => {
    jest.spyOn(authService, 'login').mockReturnValue(throwError(() => new Error('Login failed')));
    jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.form.setValue({ email: 'test@example.com', password: 'wrongpassword' });
    component.submit();

    expect(component.onError).toBeTruthy();
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
