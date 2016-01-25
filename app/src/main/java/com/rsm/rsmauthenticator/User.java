package com.rsm.rsmauthenticator;

/**
 * Created by James on 25/01/2016.
 */
public class User {
    String email, password, name;

    public User(String name,String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
