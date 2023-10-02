package com.cleutonsampaio.yamlab;

public class User {
    public String email;
    public boolean isAdmin;

    @Override
    public String toString() {
        return "User{" + "email='" + email + '\'' + ", isAdmin=" + isAdmin + '}';
    }
}
