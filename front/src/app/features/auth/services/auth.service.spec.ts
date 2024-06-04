import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  //Check if there is no outstanding request
  afterEach(() => {
    httpMock.verify();
  });

  it('should post login request and return session info', () => {
    const mockSessionInfo = {
        token: '123abc',
        type: 'Bearer',
        id: 1,
        username: 'testUser',
        firstName: 'Test',
        lastName: 'User',
        admin: false
      };    
    const loginRequest: LoginRequest = { email: 'user@gmail.com', password: 'pass' };

    service.login(loginRequest).subscribe(response => {
      expect(response.token).toEqual(mockSessionInfo);
    });

    const req = httpMock.expectOne('api/auth/login');
    expect(req.request.method).toEqual('POST');
    req.flush(mockSessionInfo);
    expect(req.request.body).toEqual(loginRequest);
    // Verify that there are no outstanding requests
    httpMock.verify();
  });

  it('should post register request and return nothing', () => {
    const registerRequest: RegisterRequest = { firstName: 'user', lastName:"user",email: 'email@test.com', password: 'pass' };

    service.register(registerRequest).subscribe(response => {
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('api/auth/register');
    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual(registerRequest);
    req.flush(null);
  });
});
