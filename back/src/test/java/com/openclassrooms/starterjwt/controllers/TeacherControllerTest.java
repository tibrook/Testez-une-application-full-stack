package com.openclassrooms.starterjwt.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;
	@Autowired
    private Validator validator;
	
    @MockBean
    private TeacherService teacherService;

    @MockBean
    private TeacherMapper teacherMapper;

    @Nested
    @DisplayName("GET /api/teacher/{id} Tests")
    class FindById {

    	@Test
    	@DisplayName("Should find teacher by ID and return mapped TeacherDto")
    	@WithMockUser(username = "admin@studio.com", roles = {"ADMIN"})
    	void shouldFindTeacherByIdAndReturnDto() throws Exception {
    	    Long teacherId = 1L;
    	    Teacher teacher = new Teacher();
    	    teacher.setId(teacherId);
    	    teacher.setFirstName("John");
    	    teacher.setLastName("Doe");

    	    TeacherDto teacherDto = new TeacherDto();
    	    teacherDto.setId(teacherId);
    	    teacherDto.setFirstName("John");
    	    teacherDto.setLastName("Doe");

    	    when(teacherService.findById(teacherId)).thenReturn(teacher);
    	    when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

    	    mockMvc.perform(get("/api/teacher/{id}", teacherId)
    	            .contentType(MediaType.APPLICATION_JSON))
    	        .andExpect(status().isOk())
    	        .andExpect(jsonPath("$.id").value(teacherId))
    	        .andExpect(jsonPath("$.firstName").value("John"))
    	        .andExpect(jsonPath("$.lastName").value("Doe"));
    	}
   

        @Test
        @DisplayName("Validate Teacher entity constraints are triggered")
        void validateConstraints() {
            Teacher teacher = new Teacher()
                .setFirstName(" ")
                .setLastName("");

            Set<ConstraintViolation<Teacher>> violations = validator.validate(teacher);
            assertThat(violations).isNotEmpty();
            assertThat(violations.stream().anyMatch(v -> v.getMessage().contains("must not be blank"))).isTrue();
        }
        
        @Test
        @DisplayName("Should return 404 when teacher ID does not exist")
        @WithMockUser(username = "admin@studio.com", roles = {"ADMIN"})
        void shouldNotFindTeacherById() throws Exception {
            long nonExistentTeacherId = 9999L;

            when(teacherService.findById(nonExistentTeacherId)).thenReturn(null);

            mockMvc.perform(get("/api/teacher/{id}", nonExistentTeacherId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 for bad teacher ID format")
        @WithMockUser(username = "admin@studio.com", roles = {"ADMIN"})
        void shouldRejectBadTeacherIdFormat() throws Exception {
            String badTeacherId = "BadId";

            mockMvc.perform(get("/api/teacher/{id}", badTeacherId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        }
    }


    @Nested
    @DisplayName("GET /api/teacher Tests")
    class FindAllTeachers {

        @Test
        @DisplayName("Should return all teachers")
        @WithMockUser(username = "admin@studio.com", roles = {"ADMIN"})
        void shouldReturnAllTeachers() throws Exception {
            Teacher teacher1 = new Teacher();
            Teacher teacher2 = new Teacher();

            teacher1.setId(1L);
            teacher2.setId(2L);
            
            List<Teacher> teachers = Arrays.asList(
            		teacher1,
            		teacher2
            );
            TeacherDto teacherDto1 = new TeacherDto();
            TeacherDto teacherDto2 = new TeacherDto();

            teacherDto1.setId(1L);
            teacherDto1.setFirstName("John");
            teacherDto1.setLastName("Doe");
            teacherDto2.setId(2L);
            teacherDto2.setFirstName("Johnny");
            teacherDto2.setLastName("DoeDoe");
            List<TeacherDto> teacherDtos = Arrays.asList(
            		teacherDto1,
            		teacherDto2
            );

            when(teacherService.findAll()).thenReturn(teachers);
            when(teacherMapper.toDto(teachers)).thenReturn(teacherDtos);

            mockMvc.perform(get("/api/teacher")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Johnny"))
                .andExpect(jsonPath("$[1].lastName").value("DoeDoe"));
        }
    }
}
