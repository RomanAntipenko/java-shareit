package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserClient userClient;
    @Autowired
    MockMvc mockMvc;
    UserDto firstUserDto;
    UserDto secondUserDto;

    @BeforeEach
    void init() {
        firstUserDto = UserDto.builder()
                .name("maks")
                .email("maks220@mail.ru")
                .id(1L)
                .build();

        secondUserDto = UserDto.builder()
                .name("sanya")
                .email("gera789@mail.ru")
                .id(2L)
                .build();
    }

    @SneakyThrows
    @Test
    void getUsers() {
        List<UserDto> expected = List.of(firstUserDto);

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(
                expected));

        Mockito
                .when(userClient.getAllUsers())
                .thenReturn(responseEntity);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value(firstUserDto.getName()));
    }

    @SneakyThrows
    @Test
    void createUser() {
        UserDto userDtoBeforeSave = UserDto.builder()
                .name(firstUserDto.getName())
                .email(firstUserDto.getEmail())
                .build();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(
                firstUserDto));

        Mockito
                .when(userClient.createUser(userDtoBeforeSave))
                .thenReturn(responseEntity);

        mockMvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoBeforeSave)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(firstUserDto.getName()));
    }

    @SneakyThrows
    @Test
    void patchUser() {
        UserDto update = UserDto.builder()
                .name("Рома")
                .build();

        UserDto after = UserDto.builder()
                .id(1L)
                .email(firstUserDto.getEmail())
                .name("Рома")
                .build();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(
                after));

        Mockito
                .when(userClient.updateUser(1L, update))
                .thenReturn(responseEntity);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Рома"));
    }

    @SneakyThrows
    @Test
    void getUserById() {
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(
                firstUserDto));

        Mockito
                .when(userClient.getUser(1L))
                .thenReturn(responseEntity);

        mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(firstUserDto.getName()));
    }

    @SneakyThrows
    @Test
    void deleteUserById() {
        mockMvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());
    }
}