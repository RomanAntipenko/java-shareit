package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    void userWithGoodBehaviorTest() {
        UserDto user = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    void userWithBadBehaviorTest() {
        UserDto user = UserDto.builder()
                .email("bla")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void userPatchWithGoodBehaviorTest() {
        UserDto user = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        user.setId(1l);
        UserDto userPatch = UserDto.builder()
                .name("bla")
                .build();
        mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userPatch)))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    void userGetWithGoodBehaviorTest() {
        UserDto user = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        UserDto user1 = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .id(1L)
                .build();
        mockMvc.perform(get("/users/1")
                        .contentType("application/json"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(user1)));
    }

    @SneakyThrows
    @Test
    void userGetListWithGoodBehaviorTest() {
        UserDto user = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));
        UserDto user1 = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .id(1L)
                .build();
        mockMvc.perform(get("/users")
                        .contentType("application/json"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user1))));
    }

    @SneakyThrows
    @Test
    void userDeleteWithGoodBehaviorTest() {
        UserDto user = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));

        mockMvc.perform(delete("/users/1")
                        .contentType("application/json"))
                .andExpect(status().is(200));

        mockMvc.perform(get("/users")
                        .contentType("application/json"))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(Collections.emptyList())));
    }
}
