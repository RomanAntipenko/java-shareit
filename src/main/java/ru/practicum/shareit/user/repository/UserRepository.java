package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
    Collection<User> getUsers();

    User createUser(User user);

    User findUserById(long id);

    void deleteUserById(long id);

    User patchUser(User user);
}
