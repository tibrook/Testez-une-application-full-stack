package com.openclassrooms.starterjwt.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";

    /**
     * Tests for user registration through AuthController.
     */
    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {
    	
    	@Test
        @DisplayName("Should reject invalid email format in registration")
        void shouldRejectInvalidEmailFormat() throws Exception {
            SignupRequest signUpRequest = new SignupRequest();
            signUpRequest.setEmail("invalid-email");
            signUpRequest.setPassword("password123");
            signUpRequest.setFirstName("John");
            signUpRequest.setLastName("Doe");

            mockMvc.perform(post(REGISTER_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
        }
    	
    	@Test
	    @DisplayName("Should reject empty first name in registration")
	    void shouldRejectEmptyFirstName() throws Exception {
	        SignupRequest signUpRequest = new SignupRequest();
	        signUpRequest.setEmail("user@example.com");
	        signUpRequest.setPassword("password123");
	        signUpRequest.setFirstName("");
	        signUpRequest.setLastName("Doe");

	        mockMvc.perform(post(REGISTER_URL)
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(signUpRequest)))
	            .andExpect(status().isBadRequest());
	    }
    	
	    @Test
	    @DisplayName("Should reject too short password in registration")
	    void shouldRejectTooShortPassword() throws Exception {
	        SignupRequest signUpRequest = new SignupRequest();
	        signUpRequest.setEmail("user@example.com");
	        signUpRequest.setPassword("short");
	        signUpRequest.setFirstName("John");
	        signUpRequest.setLastName("Doe");

	        mockMvc.perform(post(REGISTER_URL)
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(signUpRequest)))
	            .andExpect(status().isBadRequest());
	    }
	   
        @Test
        @DisplayName("Should reject registration with existing email")
        void shouldRejectRegistrationWithExistingEmail() throws Exception {
            String email = "test@test.com";
            SignupRequest signUpRequest = new SignupRequest();
            signUpRequest.setEmail(email);
            signUpRequest.setPassword("password123");
            signUpRequest.setFirstName("John");
            signUpRequest.setLastName("Doe");

            when(userRepository.existsByEmail(email)).thenReturn(true);

            mockMvc.perform(post(REGISTER_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
        }
        
        @Test
        @DisplayName("Should register user successfully")
        void shouldRegisterUserSuccessfully() throws Exception {
            SignupRequest signUpRequest = new SignupRequest();
            signUpRequest.setEmail("newuser@example.com");
            signUpRequest.setPassword("validPassword123");
            signUpRequest.setFirstName("John");
            signUpRequest.setLastName("Doe");

            when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");

            mockMvc.perform(post(REGISTER_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

            verify(userRepository).save(any(User.class)); 
        }
     
        @Test
        @DisplayName("Should handle validation errors for registration")
        void shouldHandleValidationErrorsForRegistration() throws Exception {
            SignupRequest signUpRequest = new SignupRequest();
            signUpRequest.setEmail("notAnEmail");
            signUpRequest.setPassword("123");
            signUpRequest.setFirstName("");
            signUpRequest.setLastName("Doe");

            mockMvc.perform(post(REGISTER_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest());
        }
    }
    /**
     * Tests for user login through AuthController.
     */
    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should reject login with incorrect password")
        void shouldRejectLoginWithIncorrectPassword() throws Exception {
            String email = "user@example.com";
            String password = "wrongPassword";
            
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(email);
            loginRequest.setPassword(password);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

            mockMvc.perform(post(LOGIN_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
        }
     
        @Test
        @DisplayName("Should login successfully with admin user")
        void shouldLoginSuccessfullyWithAdminUser() throws Exception {
            String email = "admin@example.com";
            String password = "password";
            boolean isAdmin = true;

            UserDetailsImpl userDetails = UserDetailsImpl.builder()
                    .firstName("Admin")
                    .lastName("Admin")
                    .username(email)
                    .password(password)
                    .admin(isAdmin)
                    .build();

            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(email);
            loginRequest.setPassword(password);

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null));
            User newUser = new User();
            newUser.setFirstName("Admin");
            newUser.setLastName("Admin");
            newUser.setPassword("encodedPassword");
            newUser.setEmail(email);
            newUser.setAdmin(isAdmin);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(newUser));

            mockMvc.perform(post(LOGIN_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admin").value(true));
        }

        @Test
        @DisplayName("Should login successfully with non-admin user")
        void shouldLoginSuccessfullyWithNonAdminUser() throws Exception {
            String email = "user@example.com";
            String password = "password";
            boolean isAdmin = false; 

            UserDetailsImpl userDetails = UserDetailsImpl.builder()
                    .firstName("User")
                    .lastName("User")
                    .username(email)
                    .password(password)
                    .admin(isAdmin)
                    .build();

            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setEmail(email);
            loginRequest.setPassword(password);
            User newUser = new User();
            newUser.setFirstName("Admin");
            newUser.setLastName("Admin");
            newUser.setPassword("encodedPassword");
            newUser.setEmail(email);
            newUser.setAdmin(isAdmin);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(newUser));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, null));
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(newUser));

            mockMvc.perform(post(LOGIN_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admin").value(false));
        }
    }
}