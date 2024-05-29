package com.openclassrooms.starterjwt.security.services;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServicesTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;
        userService.delete(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void testFindByIdUserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User foundUser = userService.findById(userId);
        assertEquals(userId, foundUser.getId());
    }

    @Test
    void testFindByIdUserNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        User foundUser = userService.findById(1L);
        assertNull(foundUser);
    }
}
