package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class UserDto {

    /*@Null(groups = SecondaryUserValidation.class)*/
    private Long id;

    /*@NotBlank(groups = FirstlyUserValidation.class)*/
    private String name;

    /*@Email(groups = FirstlyUserValidation.class, message = "Введен некорректный email адрес")
    @NotBlank(groups = FirstlyUserValidation.class)*/
    private String email;
}
