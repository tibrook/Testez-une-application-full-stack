package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    @Nested
    class RetrieveAllTeachers {

        @Test
        void shouldReturnAllTeachers() {
            // Arrange
            Teacher teacher1 = new Teacher(); 
            Teacher teacher2 = new Teacher(); 
            when(teacherRepository.findAll()).thenReturn(Arrays.asList(teacher1, teacher2));

            // Act
            List<Teacher> teachers = teacherService.findAll();

            // Assert
            assertNotNull(teachers);
            assertEquals(2, teachers.size());
            verify(teacherRepository).findAll();
        }
    }

    @Nested
    class RetrieveTeacherById {

        @Test
        void shouldReturnTeacherWhenFound() {
            // Arrange
            Long id = 1L;
            Teacher teacher = new Teacher(); // Set properties as needed
            teacher.setId(id);
            when(teacherRepository.findById(id)).thenReturn(Optional.of(teacher));

            // Act
            Teacher foundTeacher = teacherService.findById(id);

            // Assert
            assertNotNull(foundTeacher);
            assertEquals(id, foundTeacher.getId());
            verify(teacherRepository).findById(id);
        }

        @Test
        void shouldReturnNullWhenNotFound() {
            // Arrange
            Long id = 1L;
            when(teacherRepository.findById(id)).thenReturn(Optional.empty());

            // Act
            Teacher foundTeacher = teacherService.findById(id);

            // Assert
            assertNull(foundTeacher);
        }
    }
}
