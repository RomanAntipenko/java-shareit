package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemClient itemClient;
    @Autowired
    MockMvc mockMvc;
    UserDto firstUserDto;
    UserDto secondUserDto;
    ItemDto firstItemDto;

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

        firstItemDto = ItemDto.builder()
                .id(1L)
                .available(true)
                .description("Gaming PC")
                .name("PC")
                .build();
    }

    @SneakyThrows
    @Test
    void createItem() {
        ItemDto firstItemDtoBeforeSaving = ItemDto.builder()
                .available(true)
                .description("Gaming PC")
                .name("PC")
                .build();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(firstItemDto));

        Mockito
                .when(itemClient.createItem(firstUserDto.getId(), firstItemDtoBeforeSaving))
                .thenReturn(responseEntity);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstItemDtoBeforeSaving)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("PC"));
    }

    @SneakyThrows
    @Test
    void patchItem() {
        ItemDto update = ItemDto.builder()
                .id(1L)
                .description("not Gaming PC")
                .name("not PC")
                .build();

        ItemDto afterUpdating = ItemDto.builder()
                .id(1L)
                .available(true)
                .description("not Gaming PC")
                .name("not PC")
                .build();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(afterUpdating));

        Mockito
                .when(itemClient.updateItem(firstUserDto.getId(), firstItemDto.getId(), update))
                .thenReturn(responseEntity);

        mockMvc.perform(patch("/items/{itemId}", firstItemDto.getId())
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("not PC"));
    }

    @SneakyThrows
    @Test
    void getItemsByOwner() {
        List<ItemDto> expected = List.of(firstItemDto);
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(expected));

        Mockito
                .when(itemClient.getAllItemsByOwner(1L, 0, 10))
                .thenReturn(responseEntity);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @SneakyThrows
    @Test
    void getItemById() {
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(firstItemDto));

        Mockito
                .when(itemClient.getItemById(1L, 1L))
                .thenReturn(responseEntity);

        mockMvc.perform(get("/items/{itemsId}", firstItemDto.getId())
                        .header("X-Sharer-User-Id", String.valueOf(1L)))
                .andExpect(status().isOk());

    }

    @SneakyThrows
    @Test
    void getItemByText() {
        List<ItemDto> expected = List.of(firstItemDto);
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(expected));

        Mockito
                .when(itemClient.searchItem(1L, "PC", 0, 10))
                .thenReturn(responseEntity);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .param("text", "PC")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("PC"));
    }

    @SneakyThrows
    @Test
    void createComment() {
        CommentDto commentDto = CommentDto.builder()
                .text("Все супер!!")
                .created(LocalDateTime.now())
                .authorName(firstUserDto.getName())
                .build();
        CommentDto commentDtoAfterSave = CommentDto.builder()
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .authorName(firstUserDto.getName())
                .id(1L)
                .build();
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(commentDtoAfterSave));

        Mockito
                .when(itemClient.postComment(1L, 1L, commentDto))
                .thenReturn(responseEntity);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Все супер!!"));
    }
}