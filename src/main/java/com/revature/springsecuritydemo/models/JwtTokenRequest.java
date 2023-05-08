package com.revature.springsecuritydemo.models;

public class JwtTokenRequest {
    private String username;
    private String password;

    public JwtTokenRequest() {
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
