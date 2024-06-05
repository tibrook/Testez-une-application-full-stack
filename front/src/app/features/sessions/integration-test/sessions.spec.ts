import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SessionApiService } from '../services/session-api.service';
import { ListComponent } from '../components/list/list.component';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { SessionService } from 'src/app/services/session.service';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let sessionApiService: SessionApiService;
  let sessionService: SessionService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule, MatCardModule],
      declarations: [ListComponent],
      providers: [SessionApiService, SessionService]
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    sessionApiService = TestBed.inject(SessionApiService);
    sessionService = TestBed.inject(SessionService);

    // Mock the all() method to return dummy sessions
    jest.spyOn(sessionApiService, 'all').mockReturnValue(of([{ id: 1, name: 'Rentals available', description: 'Relaxing Yoga Session', date: new Date(), teacher_id: 101, users: [1, 2], createdAt: new Date(), updatedAt: new Date() }]));
    
    // Initialize sessionInformation here for general setup
    sessionService.sessionInformation = { id: 1, admin: false, token: "token", type: "type", username: "username", firstName: "firstName", lastName: "lastName" };
    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display sessions', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('mat-card-title').textContent).toContain('Rentals available');
  });

  it('should show create button for admin users', () => {
    // Set sessionInformation to admin true before checking button visibility
    sessionService.sessionInformation!.admin = true;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('button[routerLink="create"]')).not.toBeNull();
  });

  it('should not show create button for non-admin users', () => {
    // Ensure sessionInformation admin is false before checking button visibility
    sessionService.sessionInformation!.admin = false;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('button[routerLink="create"]')).toBeNull();
  });
});
