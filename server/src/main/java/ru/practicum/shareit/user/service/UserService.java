package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto createUser(UserDto userDto);

    Collection<UserDto> getAllUsers();

    UserDto updateUser(UserDto userDto);

    UserDto getUser(long id);

    void deleteUser(long id);
}
