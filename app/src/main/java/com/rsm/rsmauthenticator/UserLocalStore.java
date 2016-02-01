package com.rsm.rsmauthenticator;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by James on 25/01/2016.
 */
public class UserLocalStore {
    public static final String SP_NAME = "RsmUserDetails";
    SharedPreferences userLocalDB;

    public UserLocalStore(Context context){
        userLocalDB = context.getSharedPreferences(SP_NAME, 0);
    }

    public void StoreUserData(User user){
        SharedPreferences.Editor spEditor = userLocalDB.edit();
        spEditor.putString("RsmName", user.name);
        spEditor.putString("RsmEmail", user.email);
        spEditor.putString("RsmPassword", user.password);
        spEditor.commit();
    }

    public User getLoggedInUser(){
        String name = userLocalDB.getString("RsmName", "");
        String email = userLocalDB.getString("RsmEmail", "");
        String password = userLocalDB.getString("RsmPassword", "");

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

    public boolean IsUserLoggedIn(){
        if(userLocalDB.getBoolean("loggedIn", false) == true)
            return true;
        else return false;
    }

}
