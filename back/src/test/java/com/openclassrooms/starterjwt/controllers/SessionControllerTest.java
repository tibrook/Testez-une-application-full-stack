package com.openclassrooms.starterjwt.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionMapper sessionMapper;

    @Nested
    @DisplayName("GET /api/session Tests")
    class GetAllSessionsTests {

    	@Test
        @DisplayName("Should return all sessions when requested by an admin")
        @WithMockUser(roles = {"ADMIN"})
        void shouldReturnAllSessions() throws Exception {
            // Instantiate and set properties separately
            Session session1 = new Session();
            session1.setId(1L);
            session1.setName("Yoga Session 1");

            Session session2 = new Session();
            session2.setId(2L);
            session2.setName("Yoga Session 2");

            List<Session> sessions = Arrays.asList(session1, session2);

            // Setup for SessionDto instances
            SessionDto sessionDto1 = new SessionDto();
            sessionDto1.setId(1L);
            sessionDto1.setName("Yoga Session 1");
            sessionDto1.setDate(new Date());
            sessionDto1.setTeacher_id(1L);
            sessionDto1.setDescription("Description for session 1");
            sessionDto1.setUsers(new ArrayList<>());

            SessionDto sessionDto2 = new SessionDto();
            sessionDto2.setId(2L);
            sessionDto2.setName("Yoga Session 2");
            sessionDto2.setDate(new Date());
            sessionDto2.setTeacher_id(2L);
            sessionDto2.setDescription("Description for session 2");
            sessionDto2.setUsers(new ArrayList<>());

            List<SessionDto> sessionDtos = Arrays.asList(sessionDto1, sessionDto2);

            when(sessionService.findAll()).thenReturn(sessions);
            when(sessionMapper.toDto(sessions)).thenReturn(sessionDtos);

            mockMvc.perform(get("/api/session")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
        }
    }

    @Nested
    @DisplayName("POST /api/session Tests")
    class CreateSessionTests {

        @Test
        @DisplayName("Should create a session successfully when valid data is provided")
        @WithMockUser(roles = {"ADMIN"})
        void shouldCreateSession() throws Exception {
            SessionDto sessionDto = new SessionDto();
            sessionDto.setName("New Yoga Session");
            sessionDto.setDate(new Date());
            sessionDto.setTeacher_id(1L);
            sessionDto.setDescription("Description");
            sessionDto.setUsers(new ArrayList<>());

            Session session = new Session().setId(1L).setName("New Yoga Session");
            when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
            when(sessionService.create(session)).thenReturn(session);
            when(sessionMapper.toDto(session)).thenReturn(sessionDto);

            mockMvc.perform(post("/api/session")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Yoga Session"));
        }
    }

    @Nested
    @DisplayName("GET /api/session/{id} Tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find session by ID when session exists")
        @WithMockUser(roles = {"ADMIN"})
        void shouldFindSessionById() throws Exception {
            Long sessionId = 1L;
            Session session = new Session();
            session.setId(sessionId);
            session.setName("Yoga Session 1");

            SessionDto sessionDto = new SessionDto();
            sessionDto.setId(sessionId);
            sessionDto.setName("Yoga Session 1");

            when(sessionService.getById(sessionId)).thenReturn(session);
            when(sessionMapper.toDto(session)).thenReturn(sessionDto);

            mockMvc.perform(get("/api/session/{id}", sessionId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga Session 1"));
        }

        @Test
        @DisplayName("Should return 404 when session ID does not exist")
        @WithMockUser(roles = {"ADMIN"})
        void shouldNotFindSessionById() throws Exception {
            Long nonExistentSessionId = 9999L;

            when(sessionService.getById(nonExistentSessionId)).thenReturn(null);

            mockMvc.perform(get("/api/session/{id}", nonExistentSessionId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 for bad session ID format")
        @WithMockUser(roles = {"ADMIN"})
        void shouldRejectBadSessionIdFormat() throws Exception {
            String badSessionId = "BadSessionId";

            mockMvc.perform(get("/api/session/{id}", badSessionId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }
    }
    @Nested
    @DisplayName("PUT /api/session/{id} Tests")
    class UpdateSessionTests {

    	@Test
        @DisplayName("Should update session successfully when provided valid data")
        @WithMockUser(roles = {"ADMIN"})
        void shouldUpdateSession() throws Exception {
            Long sessionId = 1L;
            SessionDto sessionDto = new SessionDto();
            sessionDto.setId(sessionId);
            sessionDto.setName("Updated Yoga Session");
            sessionDto.setDate(new Date());
            sessionDto.setTeacher_id(1L);
            sessionDto.setDescription("Updated description");
            sessionDto.setUsers(new ArrayList<>());

            Session updatedSession = new Session();
            updatedSession.setId(sessionId);
            updatedSession.setName("Updated Yoga Session");

            when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(updatedSession);
            when(sessionService.update(eq(sessionId), any(Session.class))).thenReturn(updatedSession);
            when(sessionMapper.toDto(updatedSession)).thenReturn(sessionDto);

            mockMvc.perform(put("/api/session/{id}", sessionId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Yoga Session"));
        }

        @Test
        @DisplayName("Should return 400 for bad session ID format on update")
        @WithMockUser(roles = {"ADMIN"})
        void shouldRejectBadSessionIdFormatOnUpdate() throws Exception {
            String badSessionId = "BadSessionId";
            SessionDto sessionDto = new SessionDto();

            mockMvc.perform(put("/api/session/{id}", badSessionId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
        }
    }
    @Nested
    @DisplayName("POST /api/session/{id}/participate/{userId} Tests")
    class ParticipationTests {

        @Test
        @DisplayName("Should allow participation")
        @WithMockUser(roles = {"USER"})
        void shouldAllowParticipation() throws Exception {
            Long sessionId = 1L;
            Long userId = 1L;

            doNothing().when(sessionService).participate(sessionId, userId);

            mockMvc.perform(post("/api/session/{id}/participate/{userId}", sessionId, userId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should allow cancellation of participation")
        @WithMockUser(roles = {"USER"})
        void shouldAllowCancellationOfParticipation() throws Exception {
            Long sessionId = 1L;
            Long userId = 1L;

            doNothing().when(sessionService).noLongerParticipate(sessionId, userId);

            mockMvc.perform(delete("/api/session/{id}/participate/{userId}", sessionId, userId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        }
    }
    @Nested
    @DisplayName("DELETE /api/session/{id} Tests")
    class DeleteSessionTests {

        @Test
        @DisplayName("Should delete session successfully")
        @WithMockUser(roles = {"ADMIN"})
        void shouldDeleteSessionSuccessfully() throws Exception {
            Long sessionId = 1L;

            Session existingSession = new Session();
            existingSession.setId(sessionId);

            when(sessionService.getById(sessionId)).thenReturn(existingSession);
            doNothing().when(sessionService).delete(sessionId);

            mockMvc.perform(delete("/api/session/{id}", sessionId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

            verify(sessionService, times(1)).delete(sessionId);
        }

        @Test
        @DisplayName("Should return 404 when session does not exist")
        @WithMockUser(roles = {"ADMIN"})
        void shouldReturnNotFoundWhenSessionDoesNotExist() throws Exception {
            Long nonExistentSessionId = 999L;

            when(sessionService.getById(nonExistentSessionId)).thenReturn(null);

            mockMvc.perform(delete("/api/session/{id}", nonExistentSessionId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 for bad session ID format on delete")
        @WithMockUser(roles = {"ADMIN"})
        void shouldRejectBadSessionIdFormatOnDelete() throws Exception {
            String badSessionId = "badId";

            mockMvc.perform(delete("/api/session/{id}", badSessionId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }
    }
    @Nested
    @DisplayName("NumberFormatException Tests")
    class NumberFormatExceptionTests {

        @Test
        @DisplayName("Should return 400 when creating with invalid data")
        @WithMockUser(roles = {"ADMIN"})
        void shouldReturnBadRequestWhenCreatingWithInvalidData() throws Exception {
            SessionDto sessionDto = new SessionDto(); // Assuming this has some fields that need to be validated

            mockMvc.perform(post("/api/session")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for non-numeric session ID during update")
        @WithMockUser(roles = {"ADMIN"})
        void shouldReturnBadRequestForNonNumericSessionIdUpdate() throws Exception {
            String nonNumericSessionId = "nonNumeric";
            SessionDto sessionDto = new SessionDto();
            sessionDto.setName("Yoga Updated Session");
            sessionDto.setDate(new Date());
            sessionDto.setTeacher_id(1L);
            sessionDto.setDescription("Updated Description");

            mockMvc.perform(put("/api/session/{id}", nonNumericSessionId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sessionDto)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when trying to participate with invalid session or user ID")
        @WithMockUser(roles = {"USER"})
        void shouldReturnBadRequestWhenParticipatingWithInvalidData() throws Exception {
            String invalidSessionId = "abc";
            String invalidUserId = "xyz";

            mockMvc.perform(post("/api/session/{id}/participate/{userId}", invalidSessionId, invalidUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when trying to cancel participation with invalid session or user ID")
        @WithMockUser(roles = {"USER"})
        void shouldReturnBadRequestWhenCancellingParticipationWithInvalidData() throws Exception {
            String invalidSessionId = "abc";
            String invalidUserId = "xyz";

            mockMvc.perform(delete("/api/session/{id}/participate/{userId}", invalidSessionId, invalidUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }
    }

 
}