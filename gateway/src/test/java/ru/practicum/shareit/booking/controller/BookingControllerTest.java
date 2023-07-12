package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookingClient bookingClient;
    @Autowired
    MockMvc mockMvc;
    UserDto firstUserDto;
    UserDto secondUserDto;
    ItemDto firstItemDto;
    BookingDto firstBookingDto;

    @BeforeEach
    void init() {
        LocalDateTime localDateTime = LocalDateTime.now();

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

        firstBookingDto = BookingDto.builder()
                .start(localDateTime.plusDays(1))
                .end(localDateTime.plusDays(8))
                .item(BookingDtoForItem.builder()
                        .id(1L)
                        .name(firstItemDto.getName())
                        .build())
                .build();
    }

    @SneakyThrows
    @Test
    void getBookingsForBookers() {
        BookingDto newBooking = BookingDto.builder()
                .id(1L)
                .start(firstBookingDto.getStart())
                .end(firstBookingDto.getEnd())
                .item(BookingDtoForItem.builder()
                        .id(firstItemDto.getId())
                        .name(firstItemDto.getName())
                        .build())
                .status(BookingState.APPROVED)
                .build();
        List<BookingDto> expected = List.of(newBooking);
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(expected));

        Mockito
                .when(bookingClient.getBookings(1L, BookingState.ALL, 0, 10))
                .thenReturn(responseEntity);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @SneakyThrows
    @Test
    void bookItem() {
        BookingDto newBooking = BookingDto.builder()
                .id(1L)
                .start(firstBookingDto.getStart())
                .end(firstBookingDto.getEnd())
                .item(BookingDtoForItem.builder()
                        .id(firstItemDto.getId())
                        .name(firstItemDto.getName())
                        .build())
                .build();
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(newBooking));

        Mockito
                .when(bookingClient.bookItem(1L, firstBookingDto))
                .thenReturn(responseEntity);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstBookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("PC"));
    }

    @SneakyThrows
    @Test
    void getBooking() {
        BookingDto newBooking = BookingDto.builder()
                .id(1L)
                .start(firstBookingDto.getStart())
                .end(firstBookingDto.getEnd())
                .item(BookingDtoForItem.builder()
                        .id(firstItemDto.getId())
                        .name(firstItemDto.getName())
                        .build())
                .status(BookingState.APPROVED)
                .build();
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(newBooking));

        Mockito
                .when(bookingClient.getBooking(1L, 1L))
                .thenReturn(responseEntity);

        mockMvc.perform(get("/bookings/{bookingId}", newBooking.getId())
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("PC"));
    }

    @SneakyThrows
    @Test
    void approveBooking() {
        BookingDto newBooking = BookingDto.builder()
                .id(1L)
                .start(firstBookingDto.getStart())
                .end(firstBookingDto.getEnd())
                .item(BookingDtoForItem.builder()
                        .id(firstItemDto.getId())
                        .name(firstItemDto.getName())
                        .build())
                .status(BookingState.APPROVED)
                .build();
        BookingDto oldBooking = BookingDto.builder()
                .id(1L)
                .start(firstBookingDto.getStart())
                .end(firstBookingDto.getEnd())
                .item(BookingDtoForItem.builder()
                        .id(firstItemDto.getId())
                        .name(firstItemDto.getName())
                        .build())
                .status(BookingState.WAITING)
                .build();
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(newBooking));

        Mockito
                .when(bookingClient.acceptOrDeclineBooking(1L, 1L, true))
                .thenReturn(responseEntity);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", String.valueOf(1L))
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oldBooking)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @SneakyThrows
    @Test
    void getBookingsForUser() {
        BookingDto newBooking = BookingDto.builder()
                .id(1L)
                .start(firstBookingDto.getStart())
                .end(firstBookingDto.getEnd())
                .item(BookingDtoForItem.builder()
                        .id(firstItemDto.getId())
                        .name(firstItemDto.getName())
                        .build())
                .status(BookingState.APPROVED)
                .build();
        List<BookingDto> expected = List.of(newBooking);
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(objectMapper.writeValueAsString(expected));

        Mockito
                .when(bookingClient.getAllBookingsForUser(2L, BookingState.ALL, 0, 10))
                .thenReturn(responseEntity);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", String.valueOf(2L))
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}