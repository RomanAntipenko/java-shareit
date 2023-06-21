package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public Collection<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(UserMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public UserDto createUser(User user) {
        return UserMapper.mapToDto(repository.save(user));
    }

    public UserDto updateUser(User user) {
        User oldUser = repository.findById(user.getId())
                .orElseThrow(() -> new UserIdNotFoundException(String.format("userId: \"%s\" не найден", user.getId())));
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        return UserMapper.mapToDto(repository.save(oldUser));
    }

    public UserDto getUser(long id) {
        User userOptional = repository.findById(id)
                .orElseThrow(() -> new UserIdNotFoundException(String.format("userId: \"%s\" не найден", id)));
        return UserMapper.mapToDto(userOptional);
    }

    public void deleteUser(long id) {
        repository.deleteById(id);
    }
}
