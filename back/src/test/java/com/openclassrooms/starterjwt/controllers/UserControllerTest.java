package com.openclassrooms.starterjwt.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Nested
    @DisplayName("GET /api/user/{id} Tests")
    class FindById {

        @Test
        @DisplayName("Should find user by ID when user exists")
        @WithMockUser(username = "yoga@studio.com", password = "test!1234", roles = {"ADMIN"})
        void shouldFindUserById() throws Exception {
            long userId = 1L;
            User user = new User();
            user.setId(userId);
            user.setEmail("yoga@studio.com");
            user.setLastName("Admin");
            user.setFirstName("Admin");
            user.setAdmin(true);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            mockMvc.perform(get("/api/user/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value("yoga@studio.com"))
                .andExpect(jsonPath("$.lastName").value("Admin"))
                .andExpect(jsonPath("$.firstName").value("Admin"))
                .andExpect(jsonPath("$.admin").value(true));
        }

        @Test
        @DisplayName("Should return 404 when user ID does not exist")
        @WithMockUser(username = "yoga@studio.com", password = "test!1234", roles = {"ADMIN"})
        void shouldNotFindUserById() throws Exception {
            long nonExistentUserId = 9999L;

            when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/user/{id}", nonExistentUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 for bad user ID format")
        @WithMockUser(username = "yoga@studio.com", password = "test!1234", roles = {"ADMIN"})
        void shouldRejectBadUserIdFormat() throws Exception {
            String badUserId = "BadUserId";

            mockMvc.perform(get("/api/user/{id}", badUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/user/{id} Tests")
    class DeleteUser {

        @Test
        @DisplayName("Should delete user when requested by admin")
        @WithMockUser(username = "yoga@studio.com", password = "test!1234", roles = {"ADMIN"})
        void testDelete() throws Exception {
            // Prepare
            long userId = 1L;
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            SecurityContextHolder.setContext(securityContext);
            User user = User.builder()
                    .id(Long.valueOf(userId))
                    .email("yoga@studio.com")
                    .firstName("First name")
                    .lastName("Last name")
                    .password("test!1234")
                    .admin(true)
                    .build();
            UserDetails userDetails = UserDetailsImpl.builder().username(user.getEmail()).build();


            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            mockMvc.perform(delete("/api/user/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should return 401 for unauthorized deletion attempt")
        void shouldRejectUnauthorizedDeletion() throws Exception {
            mockMvc.perform(delete("/api/user/154654654654")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return 404 for deletion of non-existent user")
        @WithMockUser(username = "yoga@studio.com", password = "test!1234", roles = {"ADMIN"})
        void shouldRejectNotFoundDeletion() throws Exception {
            mockMvc.perform(delete("/api/user/154654654654")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 for invalid user ID format on delete")
        @WithMockUser(username = "yoga@studio.com", password = "test!1234", roles = {"ADMIN"})
        void shouldRejectBadUserIdFormatDeletion() throws Exception {
            mockMvc.perform(delete("/api/user/{id}", false)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }
        
        @Test
        @DisplayName("Should reject unauthorized user trying to delete")
        @WithMockUser(username = "badUser@studio.com", password = "test!1234", roles = {"USER"})
        void testDeleteUnauthorizedUser() throws Exception {
            long userId = 1L;
            User user = new User();
            user.setId(userId);
            user.setEmail("yoga@studio.com");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            mockMvc.perform(delete("/api/user/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        }
    }
}
