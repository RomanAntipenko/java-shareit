package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepositoryImpl;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepositoryImpl;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    UserController userController;
    @Autowired
    UserService userService;
    @Autowired
    UserRepositoryImpl userRepository;
    @Autowired
    ItemController itemController;
    @Autowired
    ItemRepositoryImpl itemRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void clearAll() {
        userRepository.getUserMap().clear();
        userRepository.setIdGenerator(0);
        itemRepository.getItemMap().clear();
        itemRepository.setIdGenerator(0);
    }

    @SneakyThrows
    @Test
    void itemWithGoodBehaviorTest() {
        UserDto user = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));

        ItemDto item = ItemDto.builder()
                .available(true)
                .description("Описание")
                .name("Товар")
                .build();
        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(ItemDto.builder()
                        .id(1L)
                        .available(true)
                        .description("Описание")
                        .name("Товар")
                        .build())));
    }

    @SneakyThrows
    @Test
    void itemWithBadBehaviorWithoutUserIdTest() {
        UserDto user = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));

        ItemDto item = ItemDto.builder()
                .available(true)
                .description("Описание")
                .name("Товар")
                .build();
        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void itemWithBadBehaviorWithoutUserTest() {
        UserDto user = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));

        ItemDto item = ItemDto.builder()
                .available(true)
                .description("Описание")
                .name("Товар")
                .build();
        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 3L)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().is(404));
    }

    @SneakyThrows
    @Test
    void getItemWithGoodBehaviorTest() {
        UserDto user = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));

        ItemDto item = ItemDto.builder()
                .available(true)
                .description("Описание")
                .name("Товар")
                .build();
        mockMvc.perform(post("/items")
                .contentType("application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(item)));

        mockMvc.perform(get("/items/1")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(ItemDto.builder()
                        .id(1L)
                        .available(true)
                        .description("Описание")
                        .name("Товар")
                        .build())));

    }

    @SneakyThrows
    @Test
    void patchItemWithGoodBehaviorTest() {
        UserDto user = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));

        ItemDto item = ItemDto.builder()
                .available(true)
                .description("Описание")
                .name("Товар")
                .build();
        mockMvc.perform(post("/items")
                .contentType("application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(item)));

        ItemDto patchItem = ItemDto.builder()
                .name("Товар который пропатчили")
                .build();
        mockMvc.perform(patch("/items/1")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(patchItem)))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(ItemDto.builder()
                        .id(1L)
                        .available(true)
                        .description("Описание")
                        .name("Товар который пропатчили")
                        .build())));
    }

    @SneakyThrows
    @Test
    void patchItemWithBadBehaviorWithoutUserIdTest() {
        UserDto user = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));

        ItemDto item = ItemDto.builder()
                .available(true)
                .description("Описание")
                .name("Товар")
                .build();
        mockMvc.perform(post("/items")
                .contentType("application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(item)));

        ItemDto patchItem = ItemDto.builder()
                .name("Товар который пропатчили")
                .build();
        mockMvc.perform(patch("/items/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patchItem)))
                .andExpect(status().is(400));
    }

    @SneakyThrows
    @Test
    void getAllItemsByOwnerGoodBehaviorTest() {
        UserDto user = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));

        ItemDto item = ItemDto.builder()
                .available(true)
                .description("Описание")
                .name("Товар")
                .build();
        mockMvc.perform(post("/items")
                .contentType("application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(item)));

        mockMvc.perform(get("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(ItemDto.builder()
                        .id(1L)
                        .available(true)
                        .description("Описание")
                        .name("Товар")
                        .build()))));
    }

    @SneakyThrows
    @Test
    void getAllItemsWithWrongOwnerIdBadBehaviorTest() {
        UserDto user = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));

        ItemDto item = ItemDto.builder()
                .available(true)
                .description("Описание")
                .name("Товар")
                .build();
        mockMvc.perform(post("/items")
                .contentType("application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(item)));

        mockMvc.perform(get("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 3L))
                .andExpect(status().is(404));
    }

    @SneakyThrows
    @Test
    void searchItemsGoodBehaviorTest() {
        UserDto user = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();
        mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(user)));

        ItemDto item1 = ItemDto.builder()
                .available(true)
                .description("Описание2")
                .name("Товар Пила")
                .build();
        mockMvc.perform(post("/items")
                .contentType("application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(item1)));

        ItemDto item2 = ItemDto.builder()
                .available(true)
                .description("Описание")
                .name("Товар")
                .build();
        mockMvc.perform(post("/items")
                .contentType("application/json")
                .header("X-Sharer-User-Id", 1L)
                .content(objectMapper.writeValueAsString(item2)));

        mockMvc.perform(get("/items/search?text=пила")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is(200))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(ItemDto.builder()
                        .id(1L)
                        .available(true)
                        .description("Описание2")
                        .name("Товар Пила")
                        .build()))));
    }
}
