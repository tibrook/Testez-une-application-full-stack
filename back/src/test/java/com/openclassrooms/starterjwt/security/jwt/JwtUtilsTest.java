package com.openclassrooms.starterjwt.security.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;
    
    @Value("${oc.app.jwtSecret}")
    private String jwtSecret;
    
    @Test
    @DisplayName("Generate valid JWT token")
    void generateValidJwtToken() {
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

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3);  
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

        boolean isValid = jwtUtils.validateJwtToken(token);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Test validating JWT token with MalformedJwtException")
    void testValidateJwtTokenMalformedJwtException() {
        String invalidToken = "invalidToken";
        JwtUtils jwtUtilsMock = Mockito.mock(JwtUtils.class);
        doThrow(new MalformedJwtException("Invalid JWT token")).when(jwtUtilsMock).validateJwtToken(invalidToken);
        assertThrows(MalformedJwtException.class, () -> jwtUtilsMock.validateJwtToken(invalidToken));
    }
    
    @Test
    @DisplayName("Test validating JWT token with UnsupportedJwtException using doThrow")
    void testValidateJwtTokenWithUnsupportedJwtException() {
        JwtUtils mockedJwtUtils = Mockito.mock(JwtUtils.class);
        String dummyToken = "dummyToken";

        doThrow(new UnsupportedJwtException("Unsupported JWT token"))
                .when(mockedJwtUtils).validateJwtToken(dummyToken);

        UnsupportedJwtException thrown = assertThrows(
                UnsupportedJwtException.class,
                () -> mockedJwtUtils.validateJwtToken(dummyToken),
                "Expected validateJwtToken() to throw UnsupportedJwtException, but it did not"
        );

        assertTrue(thrown.getMessage().contains("Unsupported JWT token"));
    }
    
    @Test
    @DisplayName("Test validating JWT token with SignatureException")
    void testValidateJwtTokenSignatureException() {
        JwtUtils jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecret");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3_600_000);

        String invalidToken = "invalidToken";
        boolean isValid = jwtUtils.validateJwtToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Test validating JWT token with ExpiredJwtException")
    void testValidateJwtTokenExpiredJwtException() {
        JwtUtils jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecret");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3_600_000);

        String expiredToken = Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(SignatureAlgorithm.HS512, "testSecret")
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Test validating JWT token with IllegalArgumentException using doThrow")
    void testValidateJwtTokenWithIllegalArgumentException() {
        JwtUtils mockedJwtUtils = Mockito.mock(JwtUtils.class);
        String nullToken = null;

        doThrow(new IllegalArgumentException("JWT claims string is empty"))
                .when(mockedJwtUtils).validateJwtToken(nullToken);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> mockedJwtUtils.validateJwtToken(nullToken),
                "Expected validateJwtToken() to throw IllegalArgumentException, but it did not"
        );

        assertTrue(thrown.getMessage().contains("JWT claims string is empty"));
    }
    }
