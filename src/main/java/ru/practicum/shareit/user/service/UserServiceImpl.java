package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

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
        Optional<User> oldUser = repository.findById(user.getId());
        if (oldUser.isEmpty()) {
            log.debug("UserId not found in updateUser method");
            throw new UserIdNotFoundException(String.format("userId: \"%s\" не найден", user.getId()));
        }
        if (user.getEmail() != null) {
            oldUser.get().setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            oldUser.get().setName(user.getName());
        }
        return repository.save(oldUser.get());
    }

    public User getUser(long id) {
        Optional<User> userOptional = repository.findById(id);
        if (userOptional.isEmpty()) {
            log.debug("UserId not found in getUser method");
            throw new UserIdNotFoundException(String.format("userId: \"%s\" не найден", id));
        }
        return userOptional.get();
    }

    public void deleteUser(long id) {
        repository.deleteById(id);
    }
}
