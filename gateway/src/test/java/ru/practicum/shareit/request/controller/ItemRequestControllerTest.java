package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    RequestClient requestClient;
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
    void createRequest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .created(LocalDateTime.now())
                .description("Хочу PC")
                .build();
        ItemRequestDto itemRequestDtoAfterSave = ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("Хочу PC")
                .build();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(
                itemRequestDtoAfterSave));

        Mockito
                .when(requestClient.createRequest(firstUserDto.getId(), itemRequestDto))
                .thenReturn(responseEntity);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Хочу PC"));
    }

    @SneakyThrows
    @Test
    void getRequestsByRequestor() {
        ItemRequestDto itemRequestDtoAfterSave = ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("Хочу PC")
                .build();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(
                itemRequestDtoAfterSave));

        Mockito
                .when(requestClient.getRequestsByRequestor(firstUserDto.getId()))
                .thenReturn(responseEntity);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Хочу PC"));
    }

    @SneakyThrows
    @Test
    void getRequestByRequestId() {
        ItemRequestDto itemRequestDtoAfterSave = ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("Хочу PC")
                .build();

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(
                itemRequestDtoAfterSave));

        Mockito
                .when(requestClient.getRequestByRequestId(firstUserDto.getId(), itemRequestDtoAfterSave.getId()))
                .thenReturn(responseEntity);

        mockMvc.perform(get("/requests/{requestId}", itemRequestDtoAfterSave.getId())
                        .header("X-Sharer-User-Id", String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Хочу PC"));
    }

    @SneakyThrows
    @Test
    void getRequestsWithPagination() {
        ItemRequestDto itemRequestDtoAfterSave = ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("Хочу PC")
                .build();

        List<ItemRequestDto> expected = List.of(itemRequestDtoAfterSave);
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(expected));

        Mockito
                .when(requestClient.getRequestsWithPagination(1L, 0, 10))
                .thenReturn(responseEntity);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}