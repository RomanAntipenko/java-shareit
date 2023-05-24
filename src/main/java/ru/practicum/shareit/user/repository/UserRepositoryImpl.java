package ru.practicum.shareit.user.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    @Getter
    private final Map<Long, User> userMap;

    @Getter
    private final AtomicLong atomicId = new AtomicLong(0);

    public Collection<User> getUsers() {
        return new ArrayList<>(userMap.values());
    }

    public User createUser(User user) {
        if (userMap.values().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            log.debug("Incorrect field 'email' in createUser method");
            throw new EmailAlreadyExistsException(String.format("Такой email: \"%s\" уже существует", user.getEmail()));
        }
        atomicId.getAndIncrement();
        user.setId(atomicId.longValue());
        userMap.put(atomicId.longValue(), user);
        return user;
    }

    public User findUserById(long id) {
        if (!userMap.containsKey(id)) {
            log.debug("UserId not found in createUser method");
            throw new UserIdNotFoundException(String.format("userId: \"%s\" не найден", id));
        }
        return userMap.get(id);
    }

    public void deleteUserById(long id) {
        if (!userMap.containsKey(id)) {
            log.debug("UserId not found in deleteUserById method");
            throw new UserIdNotFoundException(String.format("userId: \"%s\" не найден", id));
        }
        userMap.remove(id);
    }

    public User updateUser(User user) {
        User userBeforePatch = userMap.get(user.getId());
        if (user.getEmail() != null) {
            if (userMap.values().stream().anyMatch(user1 -> !user1.getId().equals(user.getId())
                    && user1.getEmail().equals(user.getEmail()))) {
                log.debug("Incorrect field 'email' in patchUser method");
                throw new EmailAlreadyExistsException(String.format("Такой email: \"%s\" уже существует", user.getEmail()));
            }
            userBeforePatch.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userBeforePatch.setName(user.getName());
        }
        return userBeforePatch;
    }
}
