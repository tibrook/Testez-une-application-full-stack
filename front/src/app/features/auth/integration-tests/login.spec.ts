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
import { NgZone } from '@angular/core';
import { NavigationExtras } from '@angular/router';
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
        SessionService,
        {
          provide: Router,
          useFactory: (ngZone: NgZone) => ({
            navigate: jest.fn((commands: any[], extras?: NavigationExtras) => ngZone.run(() => router.navigate(commands, extras)))
          }),
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.get(Router);
    fixture.detectChanges();
  });

  it('should handle successful login', async() => {
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
    const navigateSpy = jest.spyOn(router, 'navigate');
    const logInSpy = jest.spyOn(sessionService, 'logIn');

    component.form.setValue({ email: 'test@example.com', password: 'securepassword' });
    component.submit();

    await fixture.whenStable();

    expect(logInSpy).toHaveBeenCalledWith(response);
    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
  });

  it('should handle login error', () => {
    jest.spyOn(authService, 'login').mockReturnValue(throwError(() => new Error('Login failed')));
    const navigateSpy = jest.spyOn(router, 'navigate');

    component.form.setValue({ email: 'test@example.com', password: 'wrongpassword' });
    component.submit();

    expect(component.onError).toBeTruthy();
    expect(navigateSpy).not.toHaveBeenCalled();
  });
  it('should render child components based on route', async () => {
    const router: Router = TestBed.inject(Router);
    const location: Location = TestBed.inject(Location);
    await router.navigate(['me']); // Assurez-vous que 'me' est un chemin valide défini dans votre routage
    await fixture.whenStable();
    expect(location.path()).toBe('/me');
    expect(fixture.nativeElement.querySelector('app-me')).not.toBeNull(); // Assurez-vous que le composant 'app-me' est utilisé
  });
  
});
