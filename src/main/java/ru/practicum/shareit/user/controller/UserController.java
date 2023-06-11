package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validations.FirstlyUserValidation;
import ru.practicum.shareit.user.validations.SecondaryUserValidation;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getUsers() {
        log.info("Вызван метод получения списка пользователей, в UserController");
        return userService.getAllUsers().stream()
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserDto createUser(@Validated({FirstlyUserValidation.class,
            SecondaryUserValidation.class}) @RequestBody UserDto userDto) {
        log.info("Вызван метод создания пользователя, в UserController");
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToDto(userService.createUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@PathVariable("userId") long userId,
                             @Validated(SecondaryUserValidation.class) @RequestBody UserDto userDto) {
        log.info("Вызван метод обновления пользователя, в UserController");
        User user = UserMapper.mapToUser(userId, userDto);
        return UserMapper.mapToDto(userService.updateUser(user));
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") long userId) {
        log.info("Вызван метод получения пользователя, в UserController");
        return UserMapper.mapToDto(userService.getUser(userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") long userId) {
        log.info("Вызван метод удаления пользователя, в UserController");
        userService.deleteUser(userId);
    }
}
