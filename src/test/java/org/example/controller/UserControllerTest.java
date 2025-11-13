package org.example.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.dto.UserDto;
import org.example.entities.UserEntity;
import org.example.services.UserService;
import org.example.utils.MappingUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private MappingUtils mappingUtils;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllUsers_whenUsersExist_thenReturnsOk() throws Exception {
        List<UserEntity> userEntities = List.of(
                new UserEntity("Name1", "Name1@email.ru", 18),
                new UserEntity("Name2", "Name2@email.ru", 20)
        );

        when(userService.getAll()).thenReturn(userEntities);

        when(mappingUtils.mapToUserDto(userEntities.get(0)))
                .thenReturn(new UserDto("Name1", "Name1@email.ru", 18));

        when(mappingUtils.mapToUserDto(userEntities.get(1)))
                .thenReturn(new UserDto("Name2", "Name2@email.ru", 20));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Name1"))
                .andExpect(jsonPath("$[0].email").value("Name1@email.ru"))
                .andExpect(jsonPath("$[0].age").value(18))
                .andExpect(jsonPath("$[1].name").value("Name2"))
                .andExpect(jsonPath("$[1].email").value("Name2@email.ru"))
                .andExpect(jsonPath("$[1].age").value(20));

        verify(userService, times(1)).getAll();
        verify(mappingUtils, times(1)).mapToUserDto(userEntities.get(0));
        verify(mappingUtils, times(1)).mapToUserDto(userEntities.get(1));
    }

    @Test
    void getAllUsers_whenServiceThrowsException_thenReturns500() throws Exception {
        when(userService.getAll()).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().is(500))
                .andExpect(result -> {
                    assert result.getResponse().getContentAsString().isEmpty();
                });

        verify(userService, times(1)).getAll();
    }

    @Test
    void getByEmail_whenUserExists_thenReturnsOk() throws Exception {
        String email = "Name@email.com";
        UserEntity userEntity = new UserEntity("Name", email, 25);
        UserDto userDto = new UserDto("Name", email, 25);

        when(userService.getByEmail(email)).thenReturn(java.util.Optional.of(userEntity));

        when(mappingUtils.mapToUserDto(userEntity)).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/users/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Name"))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.age").value(25));

        verify(userService, times(1)).getByEmail(email);
        verify(mappingUtils, times(1)).mapToUserDto(userEntity);
    }

    @Test
    void getByEmail_whenServiceThrowsException_thenReturnsInternalServerError() throws Exception {
        String email = "Name@email.ru";

        when(userService.getByEmail(email)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/v1/users/{email}", email))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).getByEmail(email);
    }

    @Test
    void createUser_whenValidUser_thenReturnsCreated() throws Exception {
        UserDto userDto = new UserDto("Name", "Name@mail.ru", 24);
        UserEntity userEntity = new UserEntity("Name", "Name@mail.ru", 24);

        when(mappingUtils.mapToUSerEntity(any(UserDto.class))).thenReturn(userEntity);
        when(mappingUtils.mapToUserDto(any(UserEntity.class))).thenReturn(userDto);

        String userDtoJson = objectMapper.writeValueAsString(userDto);
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userDtoJson))
                        .andExpect(status().isCreated());

        verify(userService, times(1)).save(userEntity);
    }

    @Test
    void createUser_whenExceptionThrown_thenReturns500() throws Exception {
        UserDto userDto = new UserDto("Name", "Name@mail.ru", 24);
        UserEntity userEntity = new UserEntity("Name", "Name@mail.ru", 24);

        lenient().when(mappingUtils.mapToUSerEntity(any(UserDto.class))).thenReturn(userEntity);
        lenient().when(mappingUtils.mapToUserDto(any(UserEntity.class))).thenReturn(userDto);

        String userDtoJson = objectMapper.writeValueAsString(userDto);

        doThrow(new RuntimeException())
                .when(userService)
                .save(userEntity);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userDtoJson))
                .andExpect(status().is(500))
                .andExpect(result -> {
                    assert result.getResponse().getContentAsString().isEmpty();
                });

        verify(userService, times(1)).save(userEntity);
    }

    @Test
    void deleteUser_whenUserExists_thenReturnsNoContent() throws Exception {
        String email = "Name@mail.ru";

        doNothing().when(userService).deleteByEmail(email);

        mockMvc.perform(delete("/api/v1/users/{email}", email))
                .andExpect(status().is(204));

        verify(userService, times(1)).deleteByEmail(email);
    }

    @Test
    void deleteUser_whenServiceThrowsException_thenReturns500() throws Exception {
        String email = "Name@mail.ru";

        doThrow(new RuntimeException())
                .when(userService)
                .deleteByEmail(email);

        mockMvc.perform(delete("/api/v1/users/{email}",email))
                .andExpect(status().is(500))
                .andExpect(result -> {
            assert result.getResponse().getContentAsString().isEmpty();
        });

        verify(userService,times(1)).deleteByEmail(email);
    }

    @Test
    void updateUser_whenUserExists_thenReturnsOk() throws Exception {
        String email = "Name@mail.ru";

        UserDto userDto = new UserDto("Name", "Name@mail.ru", 18);
        UserEntity userEntity = new UserEntity("Name", "Name@mail.ru", 18);

        UserDto userDtoResult = new UserDto("NameResult", "NameResult@mail.ru", 20);
        UserEntity userEntityResult = new UserEntity("NameResult", "NameResult@mail.ru", 20);

        when(mappingUtils.mapToUSerEntity(any(UserDto.class))).thenReturn(userEntity);
        when(userService.updateUserByEmail(eq(email), any(UserEntity.class))).thenReturn(Optional.of(userEntityResult));
        when(mappingUtils.mapToUserDto(userEntityResult)).thenReturn(userDtoResult);

        String userDtoJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(post("/api/v1/users/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(userDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NameResult"))
                .andExpect(jsonPath("$.email").value("NameResult@mail.ru"))
                .andExpect(jsonPath("$.age").value(20));

        verify(userService, times(1)).updateUserByEmail(eq(email), any(UserEntity.class));
    }


    @Test
    void updateUser_whenUserNotFound_thenReturns404() throws Exception {
        String email = "Name@mail.ru";

        UserDto userDto = new UserDto("Name", "Name@mail.ru", 25);
        UserEntity userEntity = new UserEntity("Name", "Name@mail.ru", 25);

        when(mappingUtils.mapToUSerEntity(any(UserDto.class))).thenReturn(userEntity);

        when(userService.updateUserByEmail(eq(email), any(UserEntity.class))).thenReturn(Optional.empty());

        String userDtoJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(post("/api/v1/users/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(userDtoJson))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUserByEmail(eq(email), any(UserEntity.class));
    }


    @Test
    void updateUser_whenServiceThrowsException_thenReturns500() throws Exception {
        String email = "Name@mail.ru";

        UserDto userDto = new UserDto("Name", "Name@mail.ru", 18);
        UserEntity userEntity = new UserEntity("Name", "Name@mail.ru", 18);

        UserDto userDtoResult = new UserDto("NameResult", "NameResult@mail.ru", 20);
        UserEntity userEntityResult = new UserEntity("NameResult", "NameResult@mail.ru", 20);

        when(mappingUtils.mapToUSerEntity(any(UserDto.class))).thenReturn(userEntity);
        when(userService.updateUserByEmail(eq(email), any(UserEntity.class))).thenThrow(new RuntimeException());

        String userDtoJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(post("/api/v1/users/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(userDtoJson))
                .andExpect(status().is(500))
                        .andExpect(result -> {
                            assert result.getResponse().getContentAsString().isEmpty();
                        });

        verify(userService, times(1)).updateUserByEmail(eq(email), any(UserEntity.class));
    }
}