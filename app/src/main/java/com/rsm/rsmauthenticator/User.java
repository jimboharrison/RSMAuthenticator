package com.rsm.rsmauthenticator;

/**
 * Created by James on 25/01/2016.
 */
public class User {
    String email, password, name;
    Integer userId;

    public User(String name,String email, String password, Integer userId){
        this.name = name;
        this.email = email;
        this.password = password;
        this.userId = userId;
    }
}
