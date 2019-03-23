package com.example.projectrestore;

public class UserInformation {
    public String username;
    public String name;
    public String age;

    public UserInformation() {

    }

    public UserInformation(String username, String name, String age) {
        this.username = username;
        this.name = name;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }
}
