import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { DetailComponent } from './detail.component';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionService } from '../../../../services/session.service';

import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionApiServiceMock: any;
  let teacherServiceMock: any;
  let matSnackBarMock: any;
  let routerMock: any;
  let sessionServiceMock: any;

  beforeEach(() => {
    sessionApiServiceMock = {
      detail: jest.fn().mockReturnValue(of({ id: '1', name: 'Test Session', users: [123], teacher_id: 456 })),
      delete: jest.fn().mockReturnValue(of({})),
      participate: jest.fn().mockReturnValue(of({})),
      unParticipate: jest.fn().mockReturnValue(of({}))
    };
    teacherServiceMock = { detail: jest.fn().mockReturnValue(of({ id: 456, name: 'Test Teacher' })) };
    matSnackBarMock = { open: jest.fn() };
    routerMock = { navigate: jest.fn() };
    sessionServiceMock = { sessionInformation: { id: 123, admin: true } };

    TestBed.configureTestingModule({
      declarations: [DetailComponent],
      imports: [
        ReactiveFormsModule,
        MatCardModule,
        MatIconModule,
        MatButtonModule,
        BrowserAnimationsModule
      ],
      providers: [
        FormBuilder,
        { provide: SessionApiService, useValue: sessionApiServiceMock },
        { provide: TeacherService, useValue: teacherServiceMock },
        { provide: MatSnackBar, useValue: matSnackBarMock },
        { provide: Router, useValue: routerMock },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } },
        { provide: SessionService, useValue: sessionServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch session details on initialization', () => {
    const mockSession = { id: '1', name: 'Test Session', users: [123], teacher_id: 456 };
    sessionApiServiceMock.detail.mockReturnValue(of(mockSession));
    teacherServiceMock.detail.mockReturnValue(of({ id: 456, name: 'Test Teacher' }));

    component.ngOnInit();

    expect(sessionApiServiceMock.detail).toHaveBeenCalledWith('1');
    expect(component.session).toEqual(mockSession);
    expect(teacherServiceMock.detail).toHaveBeenCalledWith('456');
    expect(component.teacher).toEqual({ id: 456, name: 'Test Teacher' });
  });

  it('should delete session and navigate on success', () => {
    sessionApiServiceMock.delete.mockReturnValue(of({}));
    jest.spyOn(routerMock, 'navigate');

    component.delete();

    expect(sessionApiServiceMock.delete).toHaveBeenCalledWith(component.sessionId);
    expect(matSnackBarMock.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(routerMock.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should handle participation correctly', () => {
    sessionApiServiceMock.participate.mockReturnValue(of({}));
    sessionApiServiceMock.detail.mockReturnValue(of({ id: '1', name: 'Test Session', users: [123], teacher_id: 456 }));
    teacherServiceMock.detail.mockReturnValue(of({ id: 456, name: 'Test Teacher' }));

    component.participate();

    expect(sessionApiServiceMock.participate).toHaveBeenCalledWith(component.sessionId, component.userId);

    // Simulate the fetchSession call
    fixture.detectChanges();
    expect(component.session).toEqual({ id: '1', name: 'Test Session', users: [123], teacher_id: 456 });
  });

  it('should handle un-participation correctly', () => {
    sessionApiServiceMock.unParticipate.mockReturnValue(of({}));
    sessionApiServiceMock.detail.mockReturnValue(of({ id: '1', name: 'Test Session', users: [123], teacher_id: 456 }));
    teacherServiceMock.detail.mockReturnValue(of({ id: 456, name: 'Test Teacher' }));

    component.unParticipate();

    expect(sessionApiServiceMock.unParticipate).toHaveBeenCalledWith(component.sessionId, component.userId);

    // Simulate the fetchSession call
    fixture.detectChanges();
    expect(component.session).toEqual({ id: '1', name: 'Test Session', users: [123], teacher_id: 456 });
  });
  it('should call window.history.back on back', () => {
    jest.spyOn(window.history, 'back');

    component.back();

    expect(window.history.back).toHaveBeenCalled();
  });
});
