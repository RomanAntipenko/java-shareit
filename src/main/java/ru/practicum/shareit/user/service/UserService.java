package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    User createUser(User user);

    Collection<User> getAllUsers();

    User updateUser(User user);

    User getUser(long id);

    void deleteUser(long id);
}
