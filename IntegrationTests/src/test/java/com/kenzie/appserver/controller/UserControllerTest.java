package com.kenzie.appserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kenzie.appserver.IntegrationTest;
import com.kenzie.appserver.controller.model.LoginRequest;
import com.kenzie.appserver.controller.model.UserCreateRequest;
import com.kenzie.appserver.service.UserService;
import com.kenzie.appserver.service.model.User;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.bouncycastle.util.encoders.UTF8;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@IntegrationTest
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    UserService userService;

    private final MockNeat mockNeat = MockNeat.threadLocal();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void createUser_withUniqueAndValidRequest_userCreatedInDynamoAndCorrectResponseReturned() throws Exception {
        //GIVEN
        String name = mockNeat.names().valStr();
        String username = mockNeat.users().valStr();
        String password = mockNeat.passwords().valStr();

        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName(name);
        userCreateRequest.setUsername(username);
        userCreateRequest.setPassword(password);

        //WHEN
        mvc.perform(post("/users/registration")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userCreateRequest)))
        //THEN
                .andExpect(jsonPath("username")
                        .value(is(username)))
                .andExpect(jsonPath("name")
                        .value(is(name)))
                .andExpect(jsonPath("hashedPassword")
                        .value(is(String.valueOf(password.hashCode()))))
                .andExpect(status().isOk());

    }

    @Test
    public void createUser_usernameAlreadyExists_returnsTriggerUsername() throws Exception {
        //GIVEN - a Login saved in the database
        String name = mockNeat.names().valStr();
        String username = mockNeat.users().valStr();
        String password = mockNeat.passwords().valStr();

        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName(name);
        userCreateRequest.setUsername(username);
        userCreateRequest.setPassword(password);

        mvc.perform(post("/users/registration")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userCreateRequest)));

        //WHEN - attempt to register User with same username
        UserCreateRequest secondUserCreateRequest = new UserCreateRequest();
        secondUserCreateRequest.setName("anewname");
        secondUserCreateRequest.setUsername(username);
        secondUserCreateRequest.setPassword("adifferentpassword");

        mvc.perform(post("/users/registration")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(secondUserCreateRequest)))

                //THEN - okrequest, UsernameTaken returned
                .andExpect(status().isOk())
                .andExpect(jsonPath("username")
                        .value(is("UsernameTaken")));
    }

    @Test
    public void login_validRequestAccountExistsGoodPassword_returnsOkWithUsernameInBody() throws Exception {
        String name = mockNeat.names().valStr();
        String username = mockNeat.users().valStr();
        String password = mockNeat.passwords().valStr();

        User user = new User(username, name, String.valueOf(password.hashCode()));

        User persistedUser = userService.createAccount(user);

        LoginRequest loginRequest = new LoginRequest(username, password);

        mvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest)))

                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().string(username));
    }
    @Test
    public void login_accountDoesntExist_returnsBadRequest() throws Exception {
        String username = mockNeat.users().valStr();
        String password = mockNeat.passwords().valStr();

        LoginRequest loginRequest = new LoginRequest(username, password);

        mvc.perform(post("/users/login")
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest)))

                .andExpect(status().isBadRequest());
    }

    @Test
    public void login_accountExistsBadPassword_returnsBadRequest() throws Exception {
        String name = mockNeat.names().valStr();
        String username = mockNeat.users().valStr();
        String password = mockNeat.passwords().valStr();

        String incorrectPassword = "jisdhbdhfbkjfdhb fkjhbvsfd";

        User user = new User(username, name, String.valueOf(password.hashCode()));

        User persistedUser = userService.createAccount(user);

        LoginRequest loginRequest = new LoginRequest(username, incorrectPassword);

        mvc.perform(post("/users/login")
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest)))

                .andExpect(status().isBadRequest());
    }
}
