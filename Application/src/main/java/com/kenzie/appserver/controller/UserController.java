package com.kenzie.appserver.controller;

import com.kenzie.appserver.controller.model.LoginRequest;
import com.kenzie.appserver.controller.model.UserCreateRequest;
import com.kenzie.appserver.controller.model.UserResponse;
import com.kenzie.appserver.service.UserService;
import com.kenzie.appserver.service.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public ResponseEntity<UserResponse> createAccount(@RequestBody UserCreateRequest userCreateRequest) {
        User user = new User(userCreateRequest.getUsername(),
                            userCreateRequest.getName(),
                            userService.hashPassword(userCreateRequest.getPassword()));

        try {
            userService.createAccount(user);
        }
        catch (IllegalArgumentException iae) {
            UserResponse usernameTaken = new UserResponse();
            usernameTaken.setUsername("UsernameTaken");
            return ResponseEntity.ok(usernameTaken);
        }

        UserResponse userResponse = createUserResponse(user);

        return ResponseEntity.ok(userResponse);
    }

    private UserResponse createUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setHashedPassword(user.getHashedPassword());
        userResponse.setName(user.getName());
        userResponse.setUsername(user.getUsername());

        return userResponse;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            String id = userService.login(loginRequest);
            if (id.equals("nologin")) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }
}
