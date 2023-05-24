package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    UserController userController;
    @Autowired
    UserService userService;
    @Autowired
    UserRepositoryImpl userRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void clearUsers() {
        userRepository.getUserMap().clear();
        userRepository.getAtomicId().set(0);
    }

    @SneakyThrows
    @Test
    void userWithGoodBehaviorTest() {
        User user = User.builder()
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
        User user = User.builder()
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
        User user = User.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));

        User userPatch = User.builder()
                .name("bla")
                .build();
        mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userPatch)))
                .andExpect(status().is(200));
    }

    @SneakyThrows
    @Test
    void userPatchWithDublicateEmailTest() {
        User user = User.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));

        User user1 = User.builder()
                .email("user@user1.com")
                .name("user1")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user1)));

        User userPatch = User.builder()
                .name("bla")
                .email("user@user1.com")
                .build();
        mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userPatch)))
                .andExpect(status().is(500));
    }

    @SneakyThrows
    @Test
    void userGetWithGoodBehaviorTest() {
        User user = User.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));

        User user1 = User.builder()
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
        User user = User.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));

        User user1 = User.builder()
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
        User user = User.builder()
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
