package com.openclassrooms.starterjwt.security.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    private final String username = "user@example.com";

    @Test
    @DisplayName("Generate valid JWT token")
    void generateValidJwtToken() {
    	 // Mocking Authentication
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .firstName("Toto")
                .lastName("Toto")
                .username("toto3@toto.com")
                .password("test!1234")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // When
        String token = jwtUtils.generateJwtToken(authentication);

        // Then
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);  // JWT consists of three parts
    }

    @Test
    @DisplayName("Get Username from JWT Token")
    void getUsernameFromJwtToken() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@example.com")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);

        String token = jwtUtils.generateJwtToken(authentication);
        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);

        assertThat(extractedUsername).isEqualTo(userDetails.getUsername());
    }

    @Test
    @DisplayName("Validate valid JWT token")
    void validateValidJwtToken() {
    	 // Mocking Authentication
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .firstName("Toto")
                .lastName("Toto")
                .username("toto3@toto.com")
                .password("test!1234")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        String token = jwtUtils.generateJwtToken(authentication);

        // When
        boolean isValid = jwtUtils.validateJwtToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Invalidate token with incorrect signature")
    void invalidateTokenWithIncorrectSignature() {
        // Mock the UserDetailsImpl to be used as principal
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@example.com")
                .password("password") // Normally not needed for token creation but provided for completeness
                .firstName("First")
                .lastName("Last")
                .build();

        // Mock the Authentication object
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);

        // Generate a valid token first
        String validToken = jwtUtils.generateJwtToken(auth);

        // Tamper the token by appending invalid characters
        String tamperedToken = validToken + "tampered";

        // Assert that the tampered token is not valid
        assertFalse(jwtUtils.validateJwtToken(tamperedToken), "Tampered token should be invalid");
    }

    @Test
    @DisplayName("Invalidate expired JWT token")
    void invalidateExpiredJwtToken() {
        // Given
        Date now = new Date();
        String expiredToken = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date(now.getTime() - 3600000)) // 1 hour ago
            .setExpiration(new Date(now.getTime() - 1800000)) // 30 minutes ago
            .signWith(SignatureAlgorithm.HS512,"testSecret") // Assuming getter for jwtSecret
            .compact();

        // Then
        assertFalse(jwtUtils.validateJwtToken(expiredToken));
    }

 // Simulate Unsupported JWT Token
    @Test
    @DisplayName("Invalidate unsupported JWT token")
    void invalidateUnsupportedJwtToken() {
        String unsupportedToken = "eyJhbGciOiJub25lIn0.eyJzdWIiOiJ1bnN1cHBvcnRlZEBleGFtcGxlLmNvbSJ9."; // No valid signature
        assertFalse(jwtUtils.validateJwtToken(unsupportedToken), "Unsupported token should be invalid");
    }

    @Test
    @DisplayName("Invalidate token with empty claims")
    void invalidateTokenWithEmptyClaims() {
        // Given: Create a JWT with an empty claims set but valid minimal structure
        String emptyClaimsToken = Jwts.builder()
            .setClaims(Jwts.claims()) // explicitly set an empty claims map
            .setSubject("") // technically not empty, but no meaningful subject
            .signWith(SignatureAlgorithm.HS512, "testSecret") // Using a consistent secret key for signing
            .compact();

        // Then: Validate the token and expect it to fail
        assertFalse(jwtUtils.validateJwtToken(emptyClaimsToken), "Token with empty claims should be invalid");
    }
    }
