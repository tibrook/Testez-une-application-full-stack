package com.openclassrooms.unit.starterjwt.model;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;
@ExtendWith(MockitoExtension.class)
public class SessionsTest {

    @InjectMocks
    private SessionMapper sessionMapper = Mappers.getMapper(SessionMapper.class);

    @Mock
    private UserService userService;

    @Mock
    private TeacherService teacherService;

    @BeforeEach
    void setup() {
     
    	lenient().when(userService.findById(1L)).thenReturn(new User());
	    lenient().when(teacherService.findById(1L)).thenReturn(null);
    }

    @Test
    @DisplayName("Convert SessionDto to Session entity")
    void testToEntity() {
        SessionDto sessionDto = new SessionDto();
        sessionDto.setTeacher_id(1L);
        sessionDto.setUsers(Arrays.asList(1L));

        Session session = sessionMapper.toEntity(sessionDto);
        
        // Verify the mapping
        assertEquals(session.getTeacher(), null);
        assertEquals(session.getUsers().size(), 1);
    }

    @Test
    @DisplayName("Convert Session entity to SessionDto")
    void testToDto() {
        Session session = new Session();
        User user = new User();
        user.setId(1L);
        session.setUsers(Arrays.asList(user));

        SessionDto sessionDto = sessionMapper.toDto(session);
        
        // Verify the mapping
        assertEquals(sessionDto.getUsers().size(), 1);
        assertEquals(sessionDto.getUsers().get(0), 1L);
    }
}
