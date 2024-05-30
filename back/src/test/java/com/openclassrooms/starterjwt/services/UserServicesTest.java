package com.openclassrooms.starterjwt.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServicesTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    class UserDeletion {

        @Test
        void shouldDeleteUser() {
            Long userId = 1L;
            userService.delete(userId);
            verify(userRepository).deleteById(userId);
        }
    }

    @Nested
    class UserRetrieval {

        @Test
        void shouldReturnUserWhenExists() {
            Long userId = 1L;
            User user = new User();
            user.setId(userId);
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

            User foundUser = userService.findById(userId);
            
            assertNotNull(foundUser);
            assertEquals(userId, foundUser.getId());
        }

        @Test
        void shouldReturnNullWhenUserNotExists() {
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            User foundUser = userService.findById(1L);
            
            assertNull(foundUser);
        }
    }
}
