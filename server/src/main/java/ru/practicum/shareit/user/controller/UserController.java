package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validations.FirstlyUserValidation;
import ru.practicum.shareit.user.validations.SecondaryUserValidation;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getUsers() {
        log.info("Вызван метод получения списка пользователей, в UserController");
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto createUser(@Validated({FirstlyUserValidation.class,
            SecondaryUserValidation.class}) @RequestBody UserDto userDto) {
        log.info("Вызван метод создания пользователя, в UserController");
        /*User user = UserMapper.mapToUser(userDto);*/
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@PathVariable("userId") long userId,
                             @Validated(SecondaryUserValidation.class) @RequestBody UserDto userDto) {
        log.info("Вызван метод обновления пользователя, в UserController");
        /*User user = UserMapper.mapToUser(userId, userDto);*/
        userDto.setId(userId);
        return userService.updateUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") long userId) {
        log.info("Вызван метод получения пользователя, в UserController");
        return userService.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") long userId) {
        log.info("Вызван метод удаления пользователя, в UserController");
        userService.deleteUser(userId);
    }
}
