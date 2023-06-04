package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public Collection<User> getAllUsers() {
        return repository.findAll();
    }

    public User createUser(User user) {
        return repository.save(user);
    }

    public User updateUser(User user) {
        User oldUser = repository.findById(user.getId())
                .orElseThrow(() -> new UserIdNotFoundException(String.format("userId: \"%s\" не найден", user.getId())));
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        return repository.save(oldUser);
    }

    public User getUser(long id) {
        User userOptional = repository.findById(id)
                .orElseThrow(() -> new UserIdNotFoundException(String.format("userId: \"%s\" не найден", id)));
        return userOptional;
    }

    public void deleteUser(long id) {
        repository.deleteById(id);
    }
}
