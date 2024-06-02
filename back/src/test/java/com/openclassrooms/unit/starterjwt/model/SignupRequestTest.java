package com.openclassrooms.unit.starterjwt.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.starterjwt.payload.request.SignupRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class SignupRequestTest {

    @Test
    @DisplayName("Should confirm equality for identical SignupRequest objects")
    public void shouldConfirmEqualityForIdenticalObjects() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("test@example.com");
        request1.setFirstName("Test");
        request1.setLastName("User");
        request1.setPassword("password");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test@example.com");
        request2.setFirstName("Test");
        request2.setLastName("User");
        request2.setPassword("password");

        assertTrue(request1.equals(request2) && request2.equals(request1));
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    @DisplayName("Should confirm inequality for different SignupRequest objects")
    public void shouldConfirmInequalityForDifferentObjects() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("test@example.com");
        request1.setFirstName("Test");
        request1.setLastName("User");
        request1.setPassword("password");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("test2@example.com");
        request2.setFirstName("Test2");
        request2.setLastName("User2");
        request2.setPassword("password2");

        assertFalse(request1.equals(request2));
        assertNotEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    @DisplayName("Should correctly generate toString for SignupRequest")
    public void shouldGenerateToStringCorrectly() {
        SignupRequest request = new SignupRequest();
        request.setEmail("test@example.com");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPassword("password");

        String expectedToString = "SignupRequest(email=test@example.com, firstName=Test, lastName=User, password=password)";
        assertEquals(expectedToString, request.toString());
    }
}
