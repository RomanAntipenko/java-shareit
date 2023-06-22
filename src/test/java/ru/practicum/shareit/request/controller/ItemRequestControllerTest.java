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
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    User firstUser;
    User secondUser;
    ItemRequest secondItemRequest;
    ItemRequest firstItemRequest;

    @BeforeEach
    void init() {
        firstUser = new User();
        firstUser.setName("maks");
        firstUser.setEmail("maks220@mail.ru");
        firstUser.setId(1L);

        secondUser = new User();
        secondUser.setName("sanya");
        secondUser.setEmail("gera789@mail.ru");
        secondUser.setId(2L);

        firstItemRequest = new ItemRequest();
        firstItemRequest.setCreated(LocalDateTime.now());
        firstItemRequest.setRequestor(firstUser);
        firstItemRequest.setDescription("I wanna Hammer");
        firstItemRequest.setId(1L);
        firstItemRequest.setItems(new ArrayList<>());

        secondItemRequest = new ItemRequest();
        secondItemRequest.setCreated(LocalDateTime.now().minusDays(1));
        secondItemRequest.setRequestor(secondUser);
        secondItemRequest.setDescription("I want perforator");
        secondItemRequest.setItems(new ArrayList<>());
        secondItemRequest.setId(2L);
    }

    @SneakyThrows
    @Test
    void createRequest() {
        ItemRequestDto firstItemRequestDto = ItemRequestDto.builder()
                .description(firstItemRequest.getDescription())
                .build();

        Mockito.when(itemRequestService.createRequest(firstUser.getId(), firstItemRequestDto))
                .thenReturn(ItemRequestMapper.mapToItemRequestDto(firstItemRequest));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", String.valueOf(firstUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value(firstItemRequestDto.getDescription()));
    }

    @SneakyThrows
    @Test
    void getRequestsByRequestor() {
        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(firstItemRequest);

        Mockito.when(itemRequestService.getRequestsByRequestor(firstUser.getId()))
                .thenReturn(List.of(ItemRequestMapper.mapToItemRequestDto(firstItemRequest)));


        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", String.valueOf(firstUser.getId())))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()));
    }

    @SneakyThrows
    @Test
    void getRequestByRequestId() {
        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(firstItemRequest);

        Mockito.when(itemRequestService.getRequestByRequestId(firstUser.getId(), firstItemRequest.getId()))
                .thenReturn(ItemRequestMapper.mapToItemRequestDto(firstItemRequest));

        mockMvc.perform(get("/requests/{requestId}", firstItemRequest.getId())
                        .header("X-Sharer-User-Id", String.valueOf(firstUser.getId())))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));
    }

    @SneakyThrows
    @Test
    void getRequestsWithPagination() {
        ItemRequestDto firstItemRequestDto = ItemRequestMapper.mapToItemRequestDto(firstItemRequest);

        ItemRequest thirdItemRequest = new ItemRequest();
        thirdItemRequest.setCreated(LocalDateTime.now());
        thirdItemRequest.setRequestor(firstUser);
        thirdItemRequest.setDescription("I wanna air pump");
        thirdItemRequest.setId(3L);
        thirdItemRequest.setItems(new ArrayList<>());

        ItemRequestDto secondItemRequestDto = ItemRequestMapper.mapToItemRequestDto(thirdItemRequest);

        Mockito.when(itemRequestService.getRequestsWithPagination(secondUser.getId(), 0, 2))
                .thenReturn(List.of(firstItemRequest, thirdItemRequest).stream()
                        .map(ItemRequestMapper::mapToItemRequestDto)
                        .collect(Collectors.toList()));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", String.valueOf(secondUser.getId()))
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value(firstItemRequestDto.getDescription()))
                .andExpect(jsonPath("$[1].id").value(3L))
                .andExpect(jsonPath("$[1].description").value(secondItemRequestDto.getDescription()));
    }
}