package com.openclassrooms.unit.starterjwt.model;
import org.junit.jupiter.api.Test;

import com.openclassrooms.starterjwt.models.Teacher;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
public class TeacherTest {
	@Test
    void testNoArgsConstructor() {
        Teacher teacher = new Teacher();
        assertNotNull(teacher);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setCreatedAt(now);
        teacher.setUpdatedAt(now);
        assertAll("Should create teacher with all properties",
            () -> assertEquals(1L, teacher.getId()),
            () -> assertEquals("Doe", teacher.getLastName()),
            () -> assertEquals("John", teacher.getFirstName()),
            () -> assertEquals(now, teacher.getCreatedAt()),
            () -> assertEquals(now, teacher.getUpdatedAt())
        );
    }

    @Test
    void testEquals() {
		 Teacher teacher1 = new Teacher();
	     teacher1.setId(1L);
	     teacher1.setFirstName("John");
	     teacher1.setLastName("Doe");
	     teacher1.setCreatedAt(null);
	     teacher1.setUpdatedAt(null);
	     Teacher teacher2 = new Teacher();
	     teacher2.setId(1L);
	     teacher2.setFirstName("John");
	     teacher2.setLastName("Doe");
	     teacher2.setCreatedAt(null);
	     teacher2.setUpdatedAt(null);

        assertEquals(teacher1, teacher2, "Two teachers with the same id should be equal");
    }

    @Test
    void testHashCode() {
    	Teacher teacher1 = new Teacher();
	     teacher1.setId(1L);
	     teacher1.setFirstName("John");
	     teacher1.setLastName("Doe");
	     teacher1.setCreatedAt(null);
	     teacher1.setUpdatedAt(null);
	     
	     Teacher teacher2 = new Teacher();
	     teacher2.setId(1L);
	     teacher2.setFirstName("John");
	     teacher2.setLastName("Doe");
	     teacher2.setCreatedAt(null);
	     teacher2.setUpdatedAt(null);
        assertEquals(teacher1.hashCode(), teacher2.hashCode(), "Hashcode should be the same for equal objects");
    }

    @Test
    void testToString() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        assertTrue(teacher.toString().contains("John"), "toString should contain the first name");
        assertTrue(teacher.toString().contains("Doe"), "toString should contain the last name");
    }

   
    @Test
    void testSettersAndGetters() {
        Teacher teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        
        assertAll("Ensure the getters and setters are working",
            () -> assertEquals(1L, teacher.getId()),
            () -> assertEquals("John", teacher.getFirstName()),
            () -> assertEquals("Doe", teacher.getLastName()),
            () -> assertNotNull(teacher.getCreatedAt()),
            () -> assertNotNull(teacher.getUpdatedAt())
        );
    }
}
