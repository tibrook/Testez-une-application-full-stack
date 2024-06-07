import { ComponentFixture, TestBed } from '@angular/core/testing';
import {  RouterTestingModule } from '@angular/router/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ListComponent } from './list.component';
import { SessionService } from '../../../../services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { of } from 'rxjs';
describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListComponent ],
      imports: [
        MatCardModule,
        MatIconModule,
        RouterTestingModule,
      ],
      providers: [
        { provide: SessionService, useValue: { sessionInformation: { id: 1, username: 'user', admin: false } }},
        { provide: SessionApiService, useValue: { all: jest.fn().mockReturnValue(of([{ id: 1, name: 'Session 1', description: 'Description 1', date: new Date(), teacher_id: 1, users: [1, 2] }])) }}
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have sessions loaded on initialization', () => {
    component.sessions$.subscribe(sessions => {
      expect(sessions.length).toBe(1);
      expect(sessions[0].name).toEqual('Session 1');
    });
  });

  it('should get user session information', () => {
    expect(component.user).toEqual({ id: 1, username: 'user', admin: false });
  });
});
