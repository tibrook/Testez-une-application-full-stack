import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule  } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';

import { FormComponent } from './form.component';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { Session } from '../../interfaces/session.interface';
describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let httpMock: HttpTestingController;
  let sessionApiService: SessionApiService;
  let fakeRouter: Router;

  const mockSessionService = {
    sessionInformation: {
      admin: true
    }
  };

  const mockRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('1')
      }
    }
  };

  beforeEach(async () => {
    fakeRouter = {
      navigate: jest.fn(),
      url: ''
    } as any;

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        NoopAnimationsModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: fakeRouter },
        { provide: ActivatedRoute, useValue: mockRoute },
        SessionApiService
      ],
      declarations: [FormComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    sessionApiService = TestBed.inject(SessionApiService);
    fixture.detectChanges();
  });

  // Unit Test
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // Unit Test
  it('should have all form fields necessary to create/update a session', () => {
    const nativeEl = fixture.nativeElement;

    const form = nativeEl.querySelector('form');
    expect(form).toBeTruthy();

    const nameInput = form.querySelector('input[formcontrolname=name]');
    expect(nameInput).toBeTruthy();

    const dateInput = form.querySelector('input[formcontrolname=date]');
    expect(dateInput).toBeTruthy();

    const teacherSelect = form.querySelector('mat-select[formcontrolname=teacher_id]');
    expect(teacherSelect).toBeTruthy();

    const descriptionTextarea = form.querySelector('textarea[formcontrolname=description]');
    expect(descriptionTextarea).toBeTruthy();
  });

  // Unit Test
  it('should have a valid form when all fields are filled', () => {
    component.sessionForm?.controls['name'].setValue('Yoga Session');
    component.sessionForm?.controls['date'].setValue('2023-05-20');
    component.sessionForm?.controls['teacher_id'].setValue('teacher1');
    component.sessionForm?.controls['description'].setValue('A relaxing yoga session');
    expect(component.sessionForm?.valid).toBeTruthy();
  });

  // Unit Test
  it('should disable the submit button if the form is invalid', () => {
    const nativeEl = fixture.nativeElement;

    component.sessionForm?.controls['name'].setValue('');
    fixture.detectChanges();

    const submitButton = nativeEl.querySelector('button[type="submit"]') as HTMLButtonElement;
    expect(submitButton.disabled).toBeTruthy();
  });

  // Integration Test
  it('should create a session and navigate to sessions page on successful creation', () => {
    const navigateSpy = jest.spyOn(fakeRouter, 'navigate');

    component.sessionForm?.controls['name'].setValue('Yoga Session');
    component.sessionForm?.controls['date'].setValue('2023-05-20');
    component.sessionForm?.controls['teacher_id'].setValue('teacher1');
    component.sessionForm?.controls['description'].setValue('A relaxing yoga session');

    component.submit();

    const req = httpMock.expectOne({
      url: 'api/session',
      method: 'POST'
    });

    req.flush({ id: '1', name: 'Yoga Session' });

    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
  });

  
  // Integration Test
  it('should update a session and navigate to sessions page on successful update', () => {
    component.onUpdate = true;
    (component as any).id = '1';
    const navigateSpy = jest.spyOn(fakeRouter, 'navigate');

    component.sessionForm?.controls['name'].setValue('Updated Yoga Session');
    component.sessionForm?.controls['date'].setValue('2023-05-20');
    component.sessionForm?.controls['teacher_id'].setValue('teacher1');
    component.sessionForm?.controls['description'].setValue('An updated relaxing yoga session');

    component.submit();

    const req = httpMock.expectOne({
      url: 'api/session/1',
      method: 'PUT'
    });

    req.flush({ id: '1', name: 'Updated Yoga Session' });

    expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
  });
  // Integration Test
  it('should navigate away if user is not admin', () => {
    mockSessionService.sessionInformation.admin = false;
    component.ngOnInit();
    expect(fakeRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });
  // Integration Test
  it('should initialize form for updating a session', () => {
    component.onUpdate = true;
    (component as any).id = '1';
    const session: Session = {
      id: 1,
      name: 'Yoga Session',
      date: new Date('2023-05-20'), 
      teacher_id: 1,
      description: 'A relaxing yoga session',
      users: []
    };

    jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(session));

    component.ngOnInit();
    expect(component.sessionForm?.value).toEqual({
      "date": "",
      "description": "",
      "name": "",
      "teacher_id": ""
    });
  });

});
