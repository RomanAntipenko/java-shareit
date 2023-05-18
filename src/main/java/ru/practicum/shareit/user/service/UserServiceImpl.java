package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public Collection<User> getAllUsers() {
        return repository.getUsers();
    }

    public User createUser(User user) {
        return repository.createUser(user);
    }

    public User patchUser(User user) {
        return repository.patchUser(user);
    }

    public User getUser(long id) {
        return repository.findUserById(id);
    }

    public void deleteUser(long id) {
        repository.deleteUserById(id);
    }
}
