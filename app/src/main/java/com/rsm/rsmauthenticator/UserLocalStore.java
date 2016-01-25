package com.rsm.rsmauthenticator;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by James on 25/01/2016.
 */
public class UserLocalStore {
    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDB;

    public UserLocalStore(Context context){
        userLocalDB = context.getSharedPreferences(SP_NAME, 0);
    }

    public void StoreUserData(User user){
        SharedPreferences.Editor spEditor = userLocalDB.edit();
        spEditor.putString("name", user.name);
        spEditor.putString("email", user.email);
        spEditor.putString("password", user.password);
        spEditor.commit();
    }

    public User getLoggedInUser(){
        String name = userLocalDB.getString("name", "");
        String email = userLocalDB.getString("email", "");
        String password = userLocalDB.getString("password", "");

        return new User(name, email, password);
    }

    public void SetUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor = userLocalDB.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalDB.edit();
        spEditor.clear();
        spEditor.commit();
    }

}
