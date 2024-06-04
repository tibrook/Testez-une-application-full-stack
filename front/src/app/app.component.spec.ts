import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { AppComponent } from './app.component';
import { ComponentFixture } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthService } from './features/auth/services/auth.service';
import { SessionService } from './services/session.service';
import { of } from 'rxjs';
describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let mockRouter: Partial<Router>;
  let mockSessionService: Partial<SessionService>;

  beforeEach(async () => {
    mockRouter = {
      navigate: jest.fn()
    };

    mockSessionService = {
      logOut: jest.fn(),
      $isLogged: jest.fn().mockReturnValue(of(true))  // Retourne un Observable de 'true'
    };

    await TestBed.configureTestingModule({
      declarations: [ AppComponent ],
      providers: [
        { provide: Router, useValue: mockRouter },
        { provide: SessionService, useValue: mockSessionService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return logged in status as observable', (done) => {
    component.$isLogged().subscribe(value => {
      expect(value).toBeTruthy();
      done();
    });
  });

  it('should log out and navigate to home', () => {
    component.logout();
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['']);
  });
});