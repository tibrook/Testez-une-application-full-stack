package com.openclassrooms.starterjwt.security.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class AuthEntryPointJwtTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private AuthEntryPointJwt authEntryPointJwt;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
    
    @BeforeEach
    void setUp() {
        // Prepare mocks not handled by Mockito annotations
    }

    @Test
    @DisplayName("Should set unauthorized response with proper JSON when authentication fails")
    void whenCommence_thenSetUnauthorizedResponse() throws IOException, ServletException {
        // Setup
        when(request.getServletPath()).thenReturn("/api/test");
        when(authException.getMessage()).thenReturn("Unauthorized error");
        
        // Use a real ByteArrayOutputStream to capture the output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(outputStream));

        // Execute
        authEntryPointJwt.commence(request, response, authException);

        // Verify response setup
        verify(response, times(1)).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, times(1)).getOutputStream();
        
        // Deserialize the output to a Map
        @SuppressWarnings("unchecked")
        Map<String, Object> outputMap = objectMapper.readValue(outputStream.toString(), Map.class);

        // Verify that the correct JSON structure and content
        assertEquals(401, outputMap.get("status"));
        assertEquals("Unauthorized", outputMap.get("error"));
        assertEquals("Unauthorized error", outputMap.get("message"));
        assertEquals("/api/test", outputMap.get("path"));
    }

    // Helper class to deal with ServletOutputStream
    private static class DelegatingServletOutputStream extends javax.servlet.ServletOutputStream {
        private final OutputStream outputStream;

        public DelegatingServletOutputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(javax.servlet.WriteListener writeListener) {
            // Not used in this context
        }
    }
}
