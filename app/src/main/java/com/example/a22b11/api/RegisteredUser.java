package com.example.a22b11.api;

import java.time.Instant;

public class RegisteredUser {
    public Long id;
    public String password;
    public Instant creation;
    public String session;

    public RegisteredUser(Long id, String password, Instant creation, String session) {
        this.id = id;
        this.password = password;
        this.creation = creation;
        this.session = session;
    }
}
