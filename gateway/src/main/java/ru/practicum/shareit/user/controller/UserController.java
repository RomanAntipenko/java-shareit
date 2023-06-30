package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.validations.FirstlyUserValidation;
import ru.practicum.shareit.user.validations.SecondaryUserValidation;


@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Вызван метод получения списка пользователей");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated({FirstlyUserValidation.class,
            SecondaryUserValidation.class}) @RequestBody UserDto userDto) {
        log.info("Вызван метод создания пользователя {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> patchUser(@PathVariable("userId") long userId,
                                            @Validated(SecondaryUserValidation.class) @RequestBody UserDto userDto) {
        log.info("Вызван метод обновления пользователя c, userId ={} и {}", userId, userDto);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable("userId") long userId) {
        log.info("Вызван метод получения пользователя по userId ={}", userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable("userId") long userId) {
        log.info("Вызван метод удаления пользователя по userId ={}", userId);
        return userClient.deleteUser(userId);
    }
}
