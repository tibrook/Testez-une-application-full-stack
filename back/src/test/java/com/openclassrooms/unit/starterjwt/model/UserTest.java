package com.openclassrooms.unit.starterjwt.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.openclassrooms.starterjwt.models.User;

import java.time.LocalDateTime;

class UserTest {

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("email@example.com"); 
        user1.setLastName("Doe");
        user1.setFirstName("John");
        user1.setPassword("password123");
        user1.setAdmin(false);
        user1.setCreatedAt(now);
        user1.setUpdatedAt(now);
        
        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("email@example.com"); 
        user2.setLastName("Doe");
        user2.setFirstName("John");
        user2.setPassword("password123");
        user2.setAdmin(false);
        user2.setCreatedAt(now);
        user2.setUpdatedAt(now);


        assertTrue(user1.equals(user2));
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEqualsNegative() {
         LocalDateTime now = LocalDateTime.now();
    	 User user1 = new User();
         user1.setId(1L);
         user1.setEmail("email@example.com"); 
         user1.setLastName("Doe");
         user1.setFirstName("John");
         user1.setPassword("password123");
         user1.setAdmin(false);
         user1.setCreatedAt(now);
         user1.setUpdatedAt(now);
         
         User user2 = new User();
         user2.setId(2L);
         user2.setEmail("other@example.com"); 
         user2.setLastName("Doe");
         user2.setFirstName("John");
         user2.setPassword("password123");
         user2.setAdmin(true);
         user2.setCreatedAt(now);
         user2.setUpdatedAt(now);

        assertFalse(user1.equals(user2));
    }
}