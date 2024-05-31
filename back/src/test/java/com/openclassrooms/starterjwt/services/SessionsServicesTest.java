package com.openclassrooms.starterjwt.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class SessionsServicesTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @Nested
    class SessionCreation {

        @Test
        void shouldCreateSessionSuccessfully() {
            Session session = new Session();
            when(sessionRepository.save(any(Session.class))).thenReturn(session);

            Session createdSession = sessionService.create(session);

            assertNotNull(createdSession);
            verify(sessionRepository).save(session);
        }
    }

    @Nested
    class SessionDeletion {

        @Test
        void shouldDeleteSession() {
            sessionService.delete(1L);
            verify(sessionRepository).deleteById(1L);
        }
    }

    @Nested
    class SessionRetrieval {

        @Test
        void shouldFindAllSessions() {
            Session session = new Session();
            when(sessionRepository.findAll()).thenReturn(Collections.singletonList(session));

            List<Session> sessions = sessionService.findAll();

            assertNotNull(sessions);
            assertEquals(1, sessions.size());
        }

        @Test
        void shouldReturnSessionIfExists() {
            Session session = new Session();
            when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));

            Session foundSession = sessionService.getById(1L);

            assertNotNull(foundSession);
        }

        @Test
        void shouldReturnNullWhenSessionNotExists() {
            when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

            Session foundSession = sessionService.getById(1L);

            assertNull(foundSession);
        }
    }

    @Nested
    class SessionUpdate {

        @Test
        void shouldUpdateSession() {
            Session session = new Session();
            when(sessionRepository.save(any(Session.class))).thenReturn(session);

            Session updatedSession = sessionService.update(1L, session);

            assertNotNull(updatedSession);
            assertEquals(1L, updatedSession.getId());
            verify(sessionRepository).save(session);
        }
    }

    @Nested
    class SessionParticipation {

        @Test
        void shouldAllowParticipation() {
            Session session = new Session();
            session.setUsers(new ArrayList<>());
            User user = new User();
            user.setId(1L);
            when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(sessionRepository.save(any(Session.class))).thenReturn(session);

            sessionService.participate(1L, 1L);

            verify(sessionRepository).save(session);
            assertEquals(1, session.getUsers().size());
        }

        @Test
        void shouldThrowNotFoundExceptionIfSessionNotFound() {
            when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 1L));
        }

        @Test
        void shouldThrowBadRequestExceptionIfAlreadyParticipating() {
            Session session = new Session();
            User user = new User();
            user.setId(1L);
            session.setUsers(Collections.singletonList(user));

            when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

            assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 1L));
        }
    }

    @Nested
    class StopParticipation {

        @Test
        void shouldRemoveParticipationSuccessfully() {
            User user = new User();
            user.setId(1L);
            Session session = new Session();
            session.setUsers(Collections.singletonList(user));

            when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));
            when(sessionRepository.save(any(Session.class))).thenReturn(session);

            sessionService.noLongerParticipate(1L, 1L);

            assertFalse(session.getUsers().contains(user));
            verify(sessionRepository).save(session);
        }

        @Test
        void shouldThrowNotFoundExceptionIfSessionNotFound() {
            when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 1L));
        }

        @Test
        void shouldThrowBadRequestExceptionIfNotParticipating() {
            Session session = new Session();
            session.setUsers(Collections.emptyList());

            when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));

            assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 1L));
        }
    }
}
