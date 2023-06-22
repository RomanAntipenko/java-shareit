package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {

    private final UserService userService;
    private final UserRepository userRepository;
    User firstUser;
    UserDto firstUserDto;
    User secondUser;
    User thirdUser;

    @BeforeEach
    void init() {
        firstUser = new User();
        firstUser.setName("maks");
        firstUser.setEmail("maks220@mail.ru");
        firstUserDto = UserDto.builder()
                .email(firstUser.getEmail())
                .name(firstUser.getName())
                .build();
        secondUser = new User();
        secondUser.setName("sanya");
        secondUser.setEmail("gera789@mail.ru");
        thirdUser = new User();
        thirdUser.setName("roma");
        thirdUser.setEmail("romafifa@mail.ru");
    }

    @Test
    void getAllUsers() {
        User firstSavedUser = userRepository.save(firstUser);
        User secondSavedUser = userRepository.save(secondUser);
        User thirdSavedUser = userRepository.save(thirdUser);

        Assertions.assertEquals(List.of(firstSavedUser, secondSavedUser, thirdSavedUser).stream()
                        .map(UserMapper::mapToDto)
                        .collect(Collectors.toList()),
                userService.getAllUsers());
    }

    @Test
    void createUser() {
        User firstSavedUser = userRepository.save(firstUser);
        Assertions.assertEquals(UserMapper.mapToDto(firstSavedUser), userService.createUser(firstUser));
    }

    @Test
    void updateUser() {
        User userUpdate = new User();
        userUpdate.setId(1L);
        userUpdate.setName("Ivan");
        User firstSavedUser = userRepository.save(firstUser);
        firstSavedUser.setName("Ivan");
        Assertions.assertEquals(UserMapper.mapToDto(firstSavedUser), userService.updateUser(userUpdate));
    }

    @Test
    void getUser() {
        User firstSavedUser = userRepository.save(firstUser);
        Assertions.assertEquals(userService.getUser(firstSavedUser.getId()), UserMapper.mapToDto(firstSavedUser));
    }

    @Test
    void deleteUser() {
        User firstSavedUser = userRepository.save(firstUser);
        userService.deleteUser(firstSavedUser.getId());
        Assertions.assertFalse(userRepository.existsById(firstSavedUser.getId()));
    }
}