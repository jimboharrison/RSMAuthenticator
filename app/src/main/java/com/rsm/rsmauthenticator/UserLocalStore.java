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
        spEditor.putInt("UserId", user.userId);
        spEditor.commit();
    }

    public User getLoggedInUser(){
        String name = userLocalDB.getString("RsmName", "");
        String email = userLocalDB.getString("RsmEmail", "");
        String password = userLocalDB.getString("RsmPassword", "");
        Integer userId = userLocalDB.getInt("UserId", Integer.MIN_VALUE);

        return new User(name, email, password, userId);
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
        return userLocalDB.getBoolean("loggedIn", false) == true;
    }

    public String GetGCMRegId(String propertyRegId) {
        return userLocalDB.getString(propertyRegId, "");
    }

    public int GetAppVersion(String propertyAppVersion) {
        return userLocalDB.getInt(propertyAppVersion, Integer.MIN_VALUE);
    }

    public void StoreAppVersion(String propertyAppVersion, int appversion) {
        SharedPreferences.Editor spEditor = userLocalDB.edit();
        spEditor.putInt(propertyAppVersion, appversion);
        spEditor.commit();
    }

    public void StoreGCMRegId(String propertyRegId, String regId) {
        SharedPreferences.Editor spEditor = userLocalDB.edit();
        spEditor.putString(propertyRegId, regId);
        spEditor.commit();
    }
}
