import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TeacherService } from './teacher.service';
import { Teacher } from '../interfaces/teacher.interface';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpTestingController: HttpTestingController;
  let sampleTeacher1 =  {
    id: 1,
    firstName: 'Test',
    lastName: 'User',
    createdAt: new Date(),
    updatedAt: new Date(),
  }; 
  let sampleTeacher2 =  {
    id: 2,
    firstName: 'Test2',
    lastName: 'User2',
    createdAt: new Date(),
    updatedAt: new Date(),
  }; 
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TeacherService]
    });
    service = TestBed.inject(TeacherService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  // Check if there is no outstanding request
  afterEach(() => {
    httpTestingController.verify(); 
  });

  it('should retrieve all teachers', () => {
      const mockTeachers: Teacher[] = [
        sampleTeacher1,
        sampleTeacher2
    ];

    service.all().subscribe(teachers => {
      expect(teachers.length).toBe(2);
      expect(teachers).toEqual(mockTeachers);
    });

    const req = httpTestingController.expectOne('api/teacher');
    expect(req.request.method).toBe('GET');
    req.flush(mockTeachers);
  });

  it('should retrieve details of a specific teacher', () => {
    const mockTeacher: Teacher = sampleTeacher1;

    service.detail('1').subscribe(teacher => {
      expect(teacher).toEqual(mockTeacher);
    });

    const req = httpTestingController.expectOne('api/teacher/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockTeacher);
  });
});
