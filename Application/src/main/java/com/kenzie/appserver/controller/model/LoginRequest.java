package com.kenzie.appserver.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.checkerframework.checker.units.qual.N;

import javax.validation.constraints.NotEmpty;

public class LoginRequest {
    //Password will be hashed in js, this object will only hold hashed passwords outside of testing
    @NotEmpty
    @JsonProperty(value = "username")
    private String username;


    @NotEmpty
    @JsonProperty(value = "password")
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public LoginRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
