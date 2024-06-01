package com.openclassrooms.starterjwt.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;

import java.lang.reflect.Field;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @Mock
    private Authentication authentication;

    @InjectMocks
    private JwtUtils jwtUtils;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Reflection to set private fields for testing
        jwtUtils = new JwtUtils();

        // Use reflection to set private fields
        Field jwtSecretField = JwtUtils.class.getDeclaredField("jwtSecret");
        jwtSecretField.setAccessible(true);
        jwtSecretField.set(jwtUtils, "myJwtSecret");

        Field jwtExpirationMsField = JwtUtils.class.getDeclaredField("jwtExpirationMs");
        jwtExpirationMsField.setAccessible(true);
        jwtExpirationMsField.set(jwtUtils, 3600000); // 1 hour for testing
    }

    @Nested
    class TokenGeneration {

        @Test
        @DisplayName("Should generate JWT token correctly")
        void shouldGenerateValidJwtToken() {
            UserDetailsImpl userPrincipal = createUserDetails("user@example.com");
            when(authentication.getPrincipal()).thenReturn(userPrincipal);

            String token = jwtUtils.generateJwtToken(authentication);
            assertNotNull(token);
            assertTrue(token.split("\\.").length == 3);  // Check Header, Payload, Signature format
        }
    }

    @Nested
    class TokenRetrieval {

        @Test
        void shouldReturnUsernameFromToken() {
            String username = "user@example.com";
            String token = generateTokenWithUsername(username);
            assertEquals(username, jwtUtils.getUserNameFromJwtToken(token));
        }
    }

    @Nested
    class TokenValidation {

        @Test
        void shouldValidateTokenWhenValid() {
            String token = generateTokenWithUsername("user@example.com");
            assertTrue(jwtUtils.validateJwtToken(token));
        }

        @Test
        void shouldInvalidateTokenWhenExpired() {
            String expiredToken = generateExpiredToken();
            assertFalse(jwtUtils.validateJwtToken(expiredToken));
        }
    }

    private UserDetailsImpl createUserDetails(String username) {
        return UserDetailsImpl.builder()
            .id(1L)
            .username(username)
            .password("password")
            .firstName("FirstName")
            .lastName("LastName")
            .build();
    }

    private String generateTokenWithUsername(String username) {
        UserDetailsImpl userPrincipal = createUserDetails(username);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        return jwtUtils.generateJwtToken(authentication);
    }

    private String generateExpiredToken() {
        // Generate an expired token for the test
        return Jwts.builder()
                .setSubject("user@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000))  // Set issued time to 2 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 3600000))  // Set expiration time to 1 hour ago
                .signWith(SignatureAlgorithm.HS512, "myJwtSecret")
                .compact();
    }

    private void setPrivateField(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = JwtUtils.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}
