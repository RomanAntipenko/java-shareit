package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.validations.FirstlyUserValidation;
import ru.practicum.shareit.user.validations.SecondaryUserValidation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Builder
@Data
public class UserDto {

    @Null(groups = SecondaryUserValidation.class)
    private Long id;

    @NotBlank(groups = FirstlyUserValidation.class)
    private String name;

    @Email(groups = FirstlyUserValidation.class, message = "Введен некорректный email адрес")
    @NotBlank(groups = FirstlyUserValidation.class)
    private String email;
}
