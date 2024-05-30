package com.openclassrooms.starterjwt.security.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

@ExtendWith(MockitoExtension.class)
class UserDetailsImplTest {

    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@example.com")
                .firstName("First")
                .lastName("Last")
                .admin(true)
                .password("password")
                .build();
    }

    @Nested
    class AccountStatus {

        @Test
        void shouldIndicateAccountIsNonExpired() {
            assertTrue(userDetails.isAccountNonExpired());
        }

        @Test
        void shouldIndicateAccountIsNonLocked() {
            assertTrue(userDetails.isAccountNonLocked());
        }

        @Test
        void shouldIndicateCredentialsAreNonExpired() {
            assertTrue(userDetails.isCredentialsNonExpired());
        }

        @Test
        void shouldIndicateAccountIsEnabled() {
            assertTrue(userDetails.isEnabled());
        }
    }

    @Nested
    class AuthorityManagement {

        @Test
        void shouldHaveNoAuthoritiesInitially() {
            assertEquals(new HashSet<>(), userDetails.getAuthorities());
        }
    }

    @Nested
    class ObjectEquality {

        @Test
        void shouldEqualItself() {
            assertEquals(userDetails, userDetails);
        }

        @Test
        void shouldEqualAnotherObjectWithSameValues() {
            UserDetailsImpl otherUserDetails = UserDetailsImpl.builder()
                    .id(1L)
                    .username("user@example.com")
                    .firstName("First")
                    .lastName("Last")
                    .admin(true)
                    .password("password")
                    .build();
            assertEquals(userDetails, otherUserDetails);
        }

        @Test
        void shouldNotEqualObjectWithDifferentValues() {
            UserDetailsImpl otherUserDetails = UserDetailsImpl.builder()
                    .id(2L)
                    .username("other@example.com")
                    .firstName("Other")
                    .lastName("User")
                    .admin(false)
                    .password("otherpassword")
                    .build();
            assertNotEquals(userDetails, otherUserDetails);
        }

        @Test
        void shouldNotEqualNull() {
            assertNotEquals(null, userDetails);
        }

        @Test
        void shouldNotEqualDifferentClass() {
            assertNotEquals(userDetails, new Object());
        }
    }
}
