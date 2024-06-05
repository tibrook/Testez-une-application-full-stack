import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { FormComponent } from './form.component';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from '../../../../services/teacher.service';
import { Session } from '../../interfaces/session.interface';
import { SessionService } from 'src/app/services/session.service';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule} from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionApiServiceMock: any;
  let teacherServiceMock: any;
  let matSnackBarMock: any;
  let routerMock: any;
  let activatedRouteMock: any;
  let sessionServiceMock: any; 

  beforeEach(() => {
    // Mock services
    sessionApiServiceMock = {
      create: jest.fn().mockReturnValue(of({})),
      update: jest.fn().mockReturnValue(of({})),
      detail: jest.fn().mockReturnValue(of({ name: 'Session Name', date: '2021-01-01', teacher_id: '123', description: 'A session' }))
    };
    teacherServiceMock = { all: jest.fn().mockReturnValue(of([])) };
    matSnackBarMock = { open: jest.fn() };
    routerMock = { navigate: jest.fn(), url: '/update' }; 
    activatedRouteMock = { snapshot: { paramMap: { get: jest.fn().mockReturnValue('1') } } };
    sessionServiceMock = {
      sessionInformation: { admin: true } 
    };
    // Configure testing module
    TestBed.configureTestingModule({
      declarations: [FormComponent],
      imports: [ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatIconModule, BrowserAnimationsModule,MatInputModule,MatSelectModule],
      providers: [
        FormBuilder,
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: MatSnackBar, useValue: matSnackBarMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: SessionService, useValue: sessionServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  it('should redirect non-admin users to /sessions', () => {
    sessionServiceMock.sessionInformation = { admin: false };
  
    fixture.detectChanges();
    component.ngOnInit();
  
    expect(routerMock.navigate).toHaveBeenCalledWith(['/sessions']);
  });
  it('should initialize the form with default values if not updating', () => {
    routerMock.url = '/';
    component.onUpdate = false; 
    fixture.detectChanges(); 
    component.ngOnInit();

    expect(component.sessionForm?.value).toEqual({
      name: '',
      date: '',
      teacher_id: '',
      description: ''
    });
  });

  it('should fill the form with session details if updating', () => {
    component.onUpdate = true;
    component.ngOnInit();
    expect(component.sessionForm?.value).toEqual({
      name: 'Session Name',
      date: '2021-01-01',
      teacher_id: '123',
      description: 'A session'
    });
  });

  it('should call create method and navigate on submit if not updating', () => {
    component.onUpdate = false;
    component.sessionForm = new FormBuilder().group({
      name: ['Test Session', Validators.required],
      date: ['2021-01-01', Validators.required],
      teacher_id: ['123', Validators.required],
      description: ['Description here', [Validators.required, Validators.max(2000)]]
    });

    component.submit();
    expect(sessionApiServiceMock.create).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['sessions']);
    expect(matSnackBarMock.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
  });

  it('should call update method and navigate on submit if updating', () => {
    component.onUpdate = true;
    component.sessionForm = new FormBuilder().group({
      name: ['Updated Session', Validators.required],
      date: ['2021-01-02', Validators.required],
      teacher_id: ['123', Validators.required],
      description: ['Updated description', [Validators.required, Validators.max(2000)]]
    });

    component.submit();
    expect(sessionApiServiceMock.update).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['sessions']);
    expect(matSnackBarMock.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
  });

});
