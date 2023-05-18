package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
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
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getUsers() {
        return userService.getAllUsers().stream()
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserDto createUser(@Validated({FirstlyUserValidation.class,
            SecondaryUserValidation.class}) @RequestBody UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToDto(userService.createUser(user));
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@PathVariable("userId") long userId,
                             @Validated(SecondaryUserValidation.class) @RequestBody UserDto userDto) {
        User user = UserMapper.mapToUser(userId, userDto);
        return UserMapper.mapToDto(userService.updateUser(user));
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable("userId") long userId) {
        return UserMapper.mapToDto(userService.getUser(userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") long userId) {
        userService.deleteUser(userId);
    }
}
