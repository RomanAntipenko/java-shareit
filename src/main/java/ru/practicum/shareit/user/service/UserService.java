package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    UserDto createUser(User user);

    Collection<UserDto> getAllUsers();

    UserDto updateUser(User user);

    UserDto getUser(long id);

    void deleteUser(long id);
}
