import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule} from '@angular/material/icon';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatInputModule } from '@angular/material/input';
import { MeComponent } from './me.component';
import { UserService } from '../../services/user.service';
import { SessionService } from '../../services/session.service';
import { User } from '../../interfaces/user.interface';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userServiceMock: Partial<UserService>;
  let sessionServiceMock: Partial<SessionService>;
  let matSnackBarMock: Partial<MatSnackBar>;
  let routerMock: Partial<Router>;
  let spyHistoryBack: jest.SpyInstance; 
  beforeEach(async () => {
    userServiceMock = {
      getById: jest.fn().mockReturnValue(of({ id: '1', name: 'Test User' })),
      delete: jest.fn().mockReturnValue(of({}))
    };

    sessionServiceMock = {
      sessionInformation: {
        token: '123abc',
        type: 'Bearer',
        id: 1,
        username: 'testUser',
        firstName: 'Test',
        lastName: 'User',
        admin: false
      },
      logOut: jest.fn()
    };

    matSnackBarMock = {
      open: jest.fn()
    };

    routerMock = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [ MeComponent ],
      imports: [ MatCardModule, MatFormFieldModule, MatIconModule, MatInputModule, BrowserAnimationsModule ],
      providers: [
        { provide: UserService, useValue: userServiceMock },
        { provide: SessionService, useValue: sessionServiceMock },
        { provide: MatSnackBar, useValue: matSnackBarMock },
        { provide: Router, useValue: routerMock }
      ]
    }).compileComponents();
    
    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    spyHistoryBack = jest.spyOn(window.history, 'back');

  });
  afterEach(() => {
    spyHistoryBack.mockRestore(); 
  });
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load user information on initialization', () => {
    expect(userServiceMock.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual({ id: '1', name: 'Test User' });
  });

  it('should navigate back on back button press', () => {
    component.back();
    expect(spyHistoryBack).toHaveBeenCalled();
  });

  it('should delete user and navigate to home on delete', () => {
    component.delete();
    expect(userServiceMock.delete).toHaveBeenCalledWith('1');
    expect(matSnackBarMock.open).toHaveBeenCalledWith("Your account has been deleted !", 'Close', { duration: 3000 });
    expect(sessionServiceMock.logOut).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/']);
  });
});
