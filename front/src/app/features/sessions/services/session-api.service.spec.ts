import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';

describe('SessionApiService', () => {
  let service: SessionApiService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SessionApiService]
    });
    service = TestBed.inject(SessionApiService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify(); // Ensure that there are no outstanding requests
  });

  it('should retrieve all sessions', () => {
    const expectedSessions: Session[] = [
      { id: 1, name: 'Session 1', description: 'Description 1', date: new Date(),teacher_id:1, users:[1,2]},
      { id: 2, name: 'Session 2', description: 'Description 2', date: new Date(), teacher_id:2,users:[3,4] }
    ];

    service.all().subscribe(sessions => {
      expect(sessions).toEqual(expectedSessions);
    });

    const req = httpTestingController.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush(expectedSessions);
  });

  it('should retrieve session detail', () => {
    const expectedSession: Session = { id: 1, name: 'Session 1', description: 'Description 1', date: new Date(),teacher_id:1, users:[1,2]};

    service.detail('1').subscribe(session => {
      expect(session).toEqual(expectedSession);
    });

    const req = httpTestingController.expectOne('api/session/1');
    expect(req.request.method).toBe('GET');
    req.flush(expectedSession);
  });

  it('should delete a session', () => {
    service.delete('1').subscribe(response => {
      expect(response).toEqual({ success: true });
    });

    const req = httpTestingController.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({ success: true });
  });

  it('should create a session', () => {
    const newSession: Session = { id: 3, name: 'Session 3', description: 'Description 3', date: new Date(),teacher_id:1, users:[1,2]};

    service.create(newSession).subscribe(session => {
      expect(session).toEqual(newSession);
    });

    const req = httpTestingController.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newSession);
    req.flush(newSession);
  });

  it('should update a session', () => {
    const updatedSession: Session = { id: 3, name: 'Session 3', description: 'Description 3', date: new Date(),teacher_id:1, users:[1,2]};

    service.update('1', updatedSession).subscribe(session => {
      expect(session).toEqual(updatedSession);
    });

    const req = httpTestingController.expectOne(`api/session/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedSession);
    req.flush(updatedSession);
  });

  it('should handle session participation', () => {
    service.participate('1', '100').subscribe(response => {
      expect(response).toBeUndefined();
    });

    const req = httpTestingController.expectOne('api/session/1/participate/100');
    expect(req.request.method).toBe('POST');
    req.flush(null);
  });

  it('should handle unparticipation from a session', () => {
    service.unParticipate('1', '100').subscribe(response => {
      expect(response).toBeUndefined();
    });

    const req = httpTestingController.expectOne('api/session/1/participate/100');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
