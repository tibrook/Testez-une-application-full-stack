import { TestBed } from '@angular/core/testing';
import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;
  let sampleUser: SessionInformation;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
    sampleUser =  {
      token: '123abc',
      type: 'Bearer',
      id: 1,
      username: 'testUser',
      firstName: 'Test',
      lastName: 'User',
      admin: false
    }; 
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('$isLogged should initially emit false', done => {
    service.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(false);
      done();
    });
  });

  it('logIn should set isLogged to true and update sessionInformation', () => {
    service.logIn(sampleUser);
    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(sampleUser);
    service.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(true);
    });
  });

  it('logOut should set isLogged to false and clear sessionInformation', () => {
    service.logIn(sampleUser); 
    service.logOut();
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
    service.$isLogged().subscribe(isLogged => {
      expect(isLogged).toBe(false);
    });
  });

  it('should emit changes to isLogged when logging in and out', done => {
   let results: boolean[] = [];
    service.$isLogged().subscribe(isLogged => {
      results.push(isLogged);
      if (results.length === 3) {
        expect(results).toEqual([false, true, false]);
        done();
      }
    });
    service.logIn(sampleUser);
    service.logOut();
  });
});
