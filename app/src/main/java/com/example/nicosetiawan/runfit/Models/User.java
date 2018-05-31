package com.example.nicosetiawan.runfit.Models;

public class User {

    private String email;
    private String fullname;
    private String user_id;

    public User(String email, String fullname, String user_id) {
        this.email = email;
        this.fullname = fullname;
        this.user_id = user_id;
    }

    public User(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", fullname='" + fullname + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}
