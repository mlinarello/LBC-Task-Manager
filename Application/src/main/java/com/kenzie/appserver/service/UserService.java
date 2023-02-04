package com.kenzie.appserver.service;

import com.kenzie.appserver.controller.model.LoginRequest;
import com.kenzie.appserver.repositories.UserRepository;
import com.kenzie.appserver.repositories.model.UserRecord;
import com.kenzie.appserver.service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User createAccount(User user) {

        if (userAlreadyExists(user)) {
            throw new IllegalArgumentException();
        }

        UserRecord userRecord = new UserRecord();
        userRecord.setName(user.getName());
        userRecord.setUsername(user.getUsername());
        userRecord.setHashedPassword(user.getHashedPassword());

        userRepository.save(userRecord);

        return user;
    }

    public String hashPassword(String password) {
        return String.valueOf(password.hashCode());
    }
    public boolean userAlreadyExists(User user) {
        return userRepository.existsById(user.getUsername());
    }

    public String login(LoginRequest loginRequest) {
        if(userRepository.existsById(loginRequest.getUsername())){
            Optional<UserRecord> userRecord = userRepository.findById(loginRequest.getUsername());
            if(userRecord.get().getHashedPassword().equals(hashPassword(loginRequest.getPassword()))){
                return userRecord.get().getUsername().toString();
            }
        }
        return "nologin";
    }
}
