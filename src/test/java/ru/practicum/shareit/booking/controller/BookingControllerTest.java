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
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    BookingService bookingService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    User firstUser;
    User secondUser;
    Item firstItem;
    Item secondItem;
    Booking bookingAfterSave;

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

        firstItem = new Item();
        firstItem.setOwner(firstUser);
        firstItem.setAvailable(true);
        firstItem.setDescription("GamingPC");
        firstItem.setName("PC");
        firstItem.setId(1L);

        secondItem = new Item();
        secondItem.setOwner(secondUser);
        secondItem.setAvailable(true);
        secondItem.setDescription("Beer");
        secondItem.setName("Waizen beer");
        secondItem.setId(2L);

        bookingAfterSave = new Booking();
        bookingAfterSave.setBooker(firstUser);
        bookingAfterSave.setItem(secondItem);
        bookingAfterSave.setState(BookingState.WAITING);
        bookingAfterSave.setStart(LocalDateTime.now().plusDays(5));
        bookingAfterSave.setEnd(LocalDateTime.now().plusDays(8));
        bookingAfterSave.setId(1L);
    }

    @SneakyThrows
    @Test
    void createBooking() {
        BookingDto bookingDto = BookingDto.builder()
                .start(bookingAfterSave.getStart())
                .end(bookingAfterSave.getEnd())
                .itemId(bookingAfterSave.getItem().getId())
                .build();

        Mockito.when(bookingService.createBooking(firstUser.getId(), bookingDto))
                .thenReturn(bookingAfterSave);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(firstUser.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Waizen beer"));
    }

    @SneakyThrows
    @Test
    void approveOrCancelBooking() {
        bookingAfterSave.setState(BookingState.APPROVED);
        Mockito.when(bookingService.acceptOrDeclineBooking(firstUser.getId(), bookingAfterSave.getId(), true))
                .thenReturn(bookingAfterSave);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingAfterSave.getId())
                        .header("X-Sharer-User-Id", String.valueOf(firstUser.getId()))
                        .param("approved", String.valueOf(true)))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Waizen beer"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @SneakyThrows
    @Test
    void getBookingForOwnerOrBooker() {
        Mockito.when(bookingService.getBookingForOwnerOrBooker(firstUser.getId(), bookingAfterSave.getId()))
                .thenReturn(bookingAfterSave);

        mockMvc.perform(get("/bookings/{bookingId}", bookingAfterSave.getId())
                        .header("X-Sharer-User-Id", String.valueOf(firstUser.getId())))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Waizen beer"));
    }

    @SneakyThrows
    @Test
    void getAllBookingsForBooker() {
        Item thirdItem = new Item();
        thirdItem.setOwner(secondUser);
        thirdItem.setAvailable(true);
        thirdItem.setDescription("Hammer for metal");
        thirdItem.setName("Hammer");
        thirdItem.setId(3L);

        Booking bookingAfterSaveSecond = new Booking();
        bookingAfterSaveSecond.setBooker(firstUser);
        bookingAfterSaveSecond.setItem(thirdItem);
        bookingAfterSaveSecond.setState(BookingState.APPROVED);
        bookingAfterSaveSecond.setStart(LocalDateTime.now().plusDays(7));
        bookingAfterSaveSecond.setEnd(LocalDateTime.now().plusDays(10));
        bookingAfterSaveSecond.setId(2L);

        Mockito.when(bookingService.getAllBookingsForUser(firstUser.getId(), "ALL", false, 0, 2))
                .thenReturn(List.of(bookingAfterSave, bookingAfterSaveSecond));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(firstUser.getId()))
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("Waizen beer"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].item.name").value("Hammer"));
    }

    @SneakyThrows
    @Test
    void getAllBookingForOwner() {
        Item thirdItem = new Item();
        thirdItem.setOwner(firstUser);
        thirdItem.setAvailable(true);
        thirdItem.setDescription("Hammer for metal");
        thirdItem.setName("Hammer");
        thirdItem.setId(3L);

        Booking bookingAfterSaveSecond = new Booking();
        bookingAfterSaveSecond.setBooker(secondUser);
        bookingAfterSaveSecond.setItem(thirdItem);
        bookingAfterSaveSecond.setState(BookingState.APPROVED);
        bookingAfterSaveSecond.setStart(LocalDateTime.now().plusDays(7));
        bookingAfterSaveSecond.setEnd(LocalDateTime.now().plusDays(10));
        bookingAfterSaveSecond.setId(2L);

        Mockito.when(bookingService.getAllBookingsForUser(firstUser.getId(), "ALL", true, 0, 2))
                .thenReturn(List.of(bookingAfterSaveSecond));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", String.valueOf(firstUser.getId()))
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].item.name").value("Hammer"));
    }
}