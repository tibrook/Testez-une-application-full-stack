import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';
import { User } from '../interfaces/user.interface';

describe('UserService', () => {
  let service: UserService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService]
    });

    service = TestBed.inject(UserService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify(); 
  });
  it('should retrieve a user by ID', () => {
    const expectedUser: User = { id: 1, email: 'john@example.com',lastName: 'Doe',firstName: 'John',admin: true,password:"TestingPassword", createdAt: new Date(), updatedAt: new Date() };

    service.getById('1').subscribe(user => {
      expect(user).toEqual(expectedUser);
    });

    const req = httpTestingController.expectOne('api/user/1');
    expect(req.request.method).toBe('GET');
    req.flush(expectedUser); 
  });

  it('should delete a user by ID', () => {
    const id = '1';
    const expectedResponse = { success: true };

    service.delete(id).subscribe(response => {
      expect(response).toEqual(expectedResponse);
    });

    const req = httpTestingController.expectOne(`api/user/${id}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(expectedResponse); 
  });
});
