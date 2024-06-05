package com.openclassrooms.starterjwt.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    class WhenTokenIsValid {

        @Test
        void shouldAuthenticateUser() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
            when(jwtUtils.validateJwtToken("validToken")).thenReturn(true);
            when(jwtUtils.getUserNameFromJwtToken("validToken")).thenReturn("user@example.com");

            UserDetails userDetails = mock(UserDetails.class);
            when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
            when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

            authTokenFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }

    @Nested
    class WhenTokenIsInvalid {

        @Test
        void shouldNotAuthenticateUser() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
            when(jwtUtils.validateJwtToken("invalidToken")).thenReturn(false);

            authTokenFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }

    @Nested
    class WhenNoTokenIsProvided {

        @Test
        void shouldNotChangeAuthentication() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn(null);

            authTokenFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }

    @Nested
    class WhenTokenProcessingFails {

        @Test
        void shouldHandleExceptionAndContinueFilterChain() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
            when(jwtUtils.validateJwtToken("validToken")).thenThrow(new RuntimeException("Unexpected error"));

            authTokenFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }
}
