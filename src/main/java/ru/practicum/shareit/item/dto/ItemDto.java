package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.validations.FirstlyItemValidation;
import ru.practicum.shareit.item.validations.SecondaryItemValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ItemDto {
    @Null(groups = SecondaryItemValidation.class)
    private Long id;
    @NotBlank(groups = FirstlyItemValidation.class)
    private String name;
    @NotBlank(groups = FirstlyItemValidation.class)
    private String description;
    @NotNull(groups = FirstlyItemValidation.class)
    private Boolean available;
    private BookingShort lastBooking;
    private BookingShort nextBooking;
    private List<CommentDto> comments;
}
