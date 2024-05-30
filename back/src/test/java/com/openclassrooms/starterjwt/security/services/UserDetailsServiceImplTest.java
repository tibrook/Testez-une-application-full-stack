package com.openclassrooms.starterjwt.security.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Nested
    class LoadUserByUsername {

        @Test
        void shouldReturnUserDetailsWhenUserIsFound() {
            User user = User.builder()
                    .id(1L)
                    .email("user@example.com")
                    .lastName("Last")
                    .firstName("First")
                    .password("password")
                    .build();

            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

            UserDetails userDetails = userDetailsService.loadUserByUsername("user@example.com");

            assertNotNull(userDetails);
            assertEquals("user@example.com", userDetails.getUsername());
            assertEquals("password", userDetails.getPassword());
        }

        @Test
        void shouldThrowUsernameNotFoundExceptionWhenUserIsNotFound() {
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            UsernameNotFoundException exception = assertThrows(
                    UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername("user@example.com")
            );

            assertEquals("User Not Found with email: user@example.com", exception.getMessage());
        }
    }
}
