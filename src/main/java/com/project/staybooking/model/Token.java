package com.project.staybooking.model;

// we don’t store token information in database
public class Token {
    private final String token;

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
