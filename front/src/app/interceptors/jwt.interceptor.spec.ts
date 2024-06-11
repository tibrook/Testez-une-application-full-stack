import { TestBed } from '@angular/core/testing';
import {  HttpHandler, HttpRequest, HttpResponse, HttpEvent } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { SessionService } from '../services/session.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { JwtInterceptor } from './jwt.interceptor';

describe('JwtInterceptor', () => {
  let interceptor: JwtInterceptor;
  let sessionService: SessionService;
  let httpHandler: HttpHandler;

  class MockHttpHandler extends HttpHandler {
    handle(req: HttpRequest<any>): Observable<HttpEvent<any>> {
      return of(new HttpResponse({
        status: 200,
        body: {} 
      }));
    }
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        JwtInterceptor,
        { provide: SessionService, useValue: { isLogged: false, sessionInformation: { token: '12345' } } },
        { provide: HttpHandler, useClass: MockHttpHandler }
      ]
    });

    interceptor = TestBed.inject(JwtInterceptor);
    sessionService = TestBed.inject(SessionService);
    httpHandler = new MockHttpHandler();
  });

  afterEach(() => {
    jest.clearAllMocks(); 
  });

  it('should add an Authorization header when the user is logged in', () => {
    sessionService.isLogged = true; 
    const httpRequest = new HttpRequest('GET', '/api/test');
    const handleSpy = jest.spyOn(httpHandler, 'handle').mockImplementation(() => of(new HttpResponse({
      status: 200,
      body: { data: 'some data' }
    })));

    interceptor.intercept(httpRequest, httpHandler).subscribe();

    expect(handleSpy).toHaveBeenCalled();
    const interceptedRequest = handleSpy.mock.calls[0][0] as HttpRequest<any>;
    expect(interceptedRequest.headers.has('Authorization')).toBeTruthy();
    expect(interceptedRequest.headers.get('Authorization')).toBe(`Bearer ${sessionService.sessionInformation!.token}`);
  });

  it('should not modify the request if the user is not logged in', () => {
    sessionService.isLogged = false; 
    const httpRequest = new HttpRequest('GET', '/api/test');
    const handleSpy = jest.spyOn(httpHandler, 'handle').mockImplementation(() => of(new HttpResponse({
      status: 200,
      body: { data: 'some data' }
    })));

    interceptor.intercept(httpRequest, httpHandler).subscribe();

    expect(handleSpy).toHaveBeenCalled();
    const interceptedRequest = handleSpy.mock.calls[0][0] as HttpRequest<any>;
    expect(interceptedRequest.headers.has('Authorization')).toBeFalsy();
  });
});
