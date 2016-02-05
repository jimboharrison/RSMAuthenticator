package com.rsm.rsmauthenticator;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by James on 05/02/2016.
 */
public class GCMClientManager {
    //Constants
    public static final String TAG = "GCMClientManager";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    //Member variables
    private GoogleCloudMessaging gcm;
    private String regId;
    private String projectNumber;
    private Activity activity;
    UserLocalStore userLocalStore;

    public GCMClientManager(Activity activity, String projectNumber, UserLocalStore userLocalStore){
        this.activity = activity;
        this.projectNumber = projectNumber;
        this.userLocalStore = userLocalStore;
        this.gcm = GoogleCloudMessaging.getInstance(activity);
    }

    //this method returns application version code from package manager
    private static int getAppVersion(Context context){
        try{
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            //shouldn't ever happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    //this method will register the client if needed or fetch regid from localstore
    public String registerIfNeeded(){
        if(checkPlayServices()){
            regId = getRegistrationId(getContext());
            if(regId.isEmpty()){
                registerInBackground();
            }else{//got id from cache
                Log.i(TAG, regId);
            }
        }else{//no play service apk on device
            Log.i(TAG, "No valid Google Play Services APK found on device");
        }

        return regId;
    }

    //This method will register the application/device with the GCM servers
    //It will also store the registration and app version code in sharedpreferences
    private void registerInBackground(){

        try{
            if(gcm == null){
                gcm = GoogleCloudMessaging.getInstance(getContext());
            }

            InstanceID instanceID = InstanceID.getInstance(getContext());

            regId = instanceID.getToken(projectNumber, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG,regId);

            //This now persists the regId on client - no need to register again :)
            //Can use my userlocalstore class
            storeRegistrationId(getContext(), regId);
        }catch (IOException ex){
            //Exception caught if error found

        }
    }

    //this method gets the current regID for the client on GCM api
    //if result is empty then the app needs to register
    private String getRegistrationId(Context context){
        String registrationId = userLocalStore.GetGCMRegId(PROPERTY_REG_ID);
        if(registrationId.isEmpty()){
            Log.i(TAG,"Registration ID not found in SP");
            return "";
        }

        //Check if app was updated as the regid may not work with new version
        int registeredVersion = userLocalStore.GetAppVersion(PROPERTY_APP_VERSION);
        int currentVersion = getAppVersion(context);
        if(registeredVersion != currentVersion){
            Log.i(TAG, "App Version Changed.");
            return "";
        }

        return registrationId;
    }

    //this method store the regid and app version in sp
    private void storeRegistrationId(Context context , String regId){
        int appversion = getAppVersion(context);
        Log.i(TAG, "Saving regid on app version : " + appversion);
        userLocalStore.StoreAppVersion(PROPERTY_APP_VERSION, appversion);
        userLocalStore.StoreGCMRegId(PROPERTY_REG_ID, regId);
    }

    //this method checks the device to make sure it has the google play services apk. if not display a dialog
    private boolean checkPlayServices() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int resultCode = api.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (api.isUserResolvableError(resultCode)) {
                api.getErrorDialog(getActivity(),resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    private Context getContext() {
        return activity;
    }
    private Activity getActivity() {
        return activity;
    }

    public static abstract class RegistrationCompletedHandler {
        public abstract void onSuccess(String registrationId, boolean isNewRegistration);
        public void onFailure(String ex) {
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
            Log.e(TAG, ex);
        }
    }
}
