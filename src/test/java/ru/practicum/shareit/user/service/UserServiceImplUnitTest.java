package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.exceptions.UserIdNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepository userRepository;
    @Captor
    ArgumentCaptor<User> argumentCaptor;

    User firstUser;
    User secondUser;
    User thirdUser;

    @BeforeEach
    void init() {
        firstUser = new User();
        firstUser.setName("maks");
        firstUser.setEmail("maks220@mail.ru");
        secondUser = new User();
        secondUser.setName("sanya");
        secondUser.setEmail("gera789@mail.ru");
        thirdUser = new User();
        thirdUser.setName("roma");
        thirdUser.setEmail("romafifa@mail.ru");
    }

    @Test
    void getAllUsers() {
        List<User> expectedUsers = List.of(firstUser, secondUser, thirdUser);

        Mockito.when(userRepository.findAll())
                .thenReturn(expectedUsers);

        Assertions.assertEquals(expectedUsers, userService.getAllUsers());
    }

    @Test
    void createUser() {
        User firstUserAfterSave = new User();
        firstUserAfterSave.setName(firstUser.getName());
        firstUserAfterSave.setEmail(firstUser.getEmail());
        firstUserAfterSave.setId(1L);

        Mockito.when(userRepository.save(firstUser))
                .thenReturn(firstUserAfterSave);

        Assertions.assertEquals(firstUserAfterSave, userService.createUser(firstUser));
        Mockito.verify(userRepository).save(firstUser);
        Mockito.verify(userRepository, Mockito.times(1)).save(firstUser);
    }

    @Test
    void updateUserAndItsOk() {
        User secondUserToUpdate = new User();
        secondUserToUpdate.setName("notsanya");
        secondUserToUpdate.setId(1L);

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(secondUser));

        User actual = userService.updateUser(secondUserToUpdate);

        Mockito.verify(userRepository).save(argumentCaptor.capture());
        Mockito.verify(userRepository, Mockito.times(1)).save(argumentCaptor.capture());
        User expected = argumentCaptor.getValue();

        Assertions.assertEquals(secondUserToUpdate.getName(), expected.getName());
        Assertions.assertEquals(secondUser.getEmail(), expected.getEmail());
    }

    @Test
    void updateUserAndItsNotFound() {
        User secondUserToUpdate = new User();
        secondUserToUpdate.setName("notsanya");
        secondUserToUpdate.setId(1L);

        Mockito.when(userRepository.findById(1L))
                .thenThrow(UserIdNotFoundException.class);

        Assertions.assertThrows(UserIdNotFoundException.class, () -> userService.updateUser(secondUserToUpdate));

        Mockito.verify(userRepository, Mockito.times(0)).save(secondUserToUpdate);
    }

    @Test
    void getUserAndItsOk() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(firstUser));

        Assertions.assertEquals(firstUser, userService.getUser(1L));
    }

    @Test
    void getUserAndItsNotFound() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenThrow(UserIdNotFoundException.class);

        Assertions.assertThrows(UserIdNotFoundException.class, () -> userService.getUser(Mockito.anyLong()));
    }

    @Test
    void deleteUser() {
        userService.deleteUser(Mockito.anyLong());
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }
}