package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class SessionsServicesTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSession() {
        Session session = new Session();
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        Session createdSession = sessionService.create(session);

        assertNotNull(createdSession);
        verify(sessionRepository).save(session);
    }

    @Test
    void testDeleteSession() {
        sessionService.delete(1L);
        verify(sessionRepository).deleteById(1L);
    }

    @Test
    void testFindAllSessions() {
        Session session = new Session();
        when(sessionRepository.findAll()).thenReturn(Collections.singletonList(session));

        List<Session> sessions = sessionService.findAll();

        assertNotNull(sessions);
        assertEquals(1, sessions.size());
    }

    @Test
    void testGetByIdSessionExists() {
        Session session = new Session();
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));

        Session foundSession = sessionService.getById(1L);

        assertNotNull(foundSession);
    }

    @Test
    void testGetByIdSessionNotExists() {
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Session foundSession = sessionService.getById(1L);

        assertNull(foundSession);
    }

    @Test
    void testUpdateSession() {
        Session session = new Session();
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        Session updatedSession = sessionService.update(1L, session);

        assertNotNull(updatedSession);
        assertEquals(1L, updatedSession.getId());
        verify(sessionRepository).save(session);
    }

    @Test
    void testParticipateSession() {
        Session session = new Session();
        session.setUsers(new ArrayList<>()); // Initialize the users list
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
    void testParticipateSessionNotFound() {
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            sessionService.participate(1L, 1L);
        });

        assertNotNull(exception);
    }
    @Test
    void testParticipateAlreadyParticipating() {
        Session session = new Session();
        User user = new User();
        user.setId(1L);
        session.setUsers(Collections.singletonList(user));

        when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(BadRequestException.class, () -> {
            sessionService.participate(1L, 1L);
        });

        assertNotNull(exception);
    }

    @Test
    void testNoLongerParticipateSessionNotFound() {
        when(sessionRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            sessionService.noLongerParticipate(1L, 1L);
        });

        assertNotNull(exception);
    }

    @Test
    void testNoLongerParticipateNotParticipating() {
        Session session = new Session();
        session.setUsers(Collections.emptyList());

        when(sessionRepository.findById(anyLong())).thenReturn(Optional.of(session));

        Exception exception = assertThrows(BadRequestException.class, () -> {
            sessionService.noLongerParticipate(1L, 1L);
        });

        assertNotNull(exception);
    }
    @Test
    void testNoLongerParticipateSession() {
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
}
