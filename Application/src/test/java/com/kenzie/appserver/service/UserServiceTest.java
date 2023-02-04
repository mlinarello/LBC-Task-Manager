package com.kenzie.appserver.service;

import com.kenzie.appserver.controller.model.LoginRequest;
import com.kenzie.appserver.repositories.UserRepository;
import com.kenzie.appserver.repositories.model.UserRecord;
import com.kenzie.appserver.service.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void userAlreadyExists_userDoesExist_returnsTrue() {
        String username = "TESTUSERNAME123";
        String name = "TESTNAME123";
        String password = "password";
        String hashedPassword = String.valueOf(password.hashCode());

        User user = new User(username, name, hashedPassword);

        UserRecord record = new UserRecord();
        record.setUsername(username);
        record.setName(name);
        record.setHashedPassword(hashedPassword);

        userService.createAccount(user);

        verify(userRepository).save(record);

        when(userRepository.existsById(username)).thenReturn(true);

        boolean userExists = userService.userAlreadyExists(user);

        Assertions.assertTrue(userExists);
    }

    @Test
    void userAlreadyExists_userDoesNotExist_returnsFalse() {
        String username = "TESTUSERNAME123";
        String name = "TESTNAME123";
        String password = "password";
        String hashedPassword = String.valueOf(password.hashCode());

        User user = new User(username, name, hashedPassword);

        boolean userExists = userService.userAlreadyExists(user);

        Assertions.assertFalse(userExists);
    }

    @Test
    void createAccount_accountDoesNotExist_accountCreated() {
        String username = "TESTUSERNAME123";
        String name = "TESTNAME123";
        String password = "password";
        String hashedPassword = String.valueOf(password.hashCode());

        User user = new User(username, name, hashedPassword);

        UserRecord record = new UserRecord();
        record.setUsername(username);
        record.setName(name);
        record.setHashedPassword(hashedPassword);

        userService.createAccount(user);

        verify(userRepository).save(record);
    }

    @Test
    void createAccount_accountDoesExist_throwsExceptionAccountNotCreated() {
        String username = "TESTUSERNAME123";
        String name = "TESTNAME123";
        String password = "password";
        String hashedPassword = String.valueOf(password.hashCode());

        User user = new User(username, name, hashedPassword);

        UserRecord record = new UserRecord();
        record.setUsername(username);
        record.setName(name);
        record.setHashedPassword(hashedPassword);

        when(userRepository.existsById(username)).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userService.createAccount(user));
    }

    @Test
    void login_withGoodLogin_returnsUsername() {
        String username = "TESTUSERNAME123";
        String name = "TESTNAME123";
        String password = "password";
        String hashedPassword = String.valueOf(password.hashCode());

        UserRecord record = new UserRecord();
        record.setUsername(username);
        record.setName(name);
        record.setHashedPassword(hashedPassword);

        LoginRequest loginRequest = new LoginRequest(username, password);

        when(userRepository.existsById(username)).thenReturn(true);

        when(userRepository.findById(username)).thenReturn(Optional.of(record));

        String response = userService.login(loginRequest);

        Assertions.assertEquals(username, response);
    }

    @Test
    void login_usernameDoesNotExist_returnNoLoginString() {
        String username = "TESTUSERNAME123";
        String password = "password";

        LoginRequest loginRequest = new LoginRequest(username, password);

        when(userRepository.existsById(username)).thenReturn(false);

        String response = userService.login(loginRequest);

        Assertions.assertEquals("nologin", response);
    }

    @Test
    void login_incorrectPassword_returnNoLoginString() {
        String username = "TESTUSERNAME123";
        String name = "TESTNAME123";
        String password = "password";
        String hashedPassword = String.valueOf(password.hashCode());

        UserRecord record = new UserRecord();
        record.setUsername(username);
        record.setName(name);
        record.setHashedPassword(hashedPassword);

        LoginRequest loginRequest = new LoginRequest(username, "badPassword");

        when(userRepository.existsById(username)).thenReturn(true);

        when(userRepository.findById(username)).thenReturn(Optional.of(record));

        String response = userService.login(loginRequest);

        Assertions.assertEquals("nologin", response);
    }
}
