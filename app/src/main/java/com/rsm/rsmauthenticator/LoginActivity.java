package com.rsm.rsmauthenticator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Credentials;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Authenticator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by James on 08/01/2016.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btLogin;
    EditText etEmail, etPassword;
    TelephonyManager telephonyManager;
    OkHttpClient client;
    UserLocalStore userLocalStore;
    JSONObject jsonResponse;
    String name, email, pass;
    Integer userId;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static String TAG = "LaunchActivity";

    protected String SENDER_ID = "540327761504";
    private GoogleCloudMessaging gcm =null;
    private String regid = null;
    private Context context= null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /* TEST*/
        if (checkPlayServices())
        {
            Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(myToolbar);

            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            client = new OkHttpClient();
            etEmail = (EditText) findViewById(R.id.etEmail);
            etPassword = (EditText) findViewById(R.id.etPassword);
            btLogin = (Button) findViewById(R.id.btLogin);

            btLogin.setOnClickListener(this);
            userLocalStore = new UserLocalStore(this);
        }
        /* ENDTEST*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //if login button is clicked
            case R.id.btLogin:
                try {
                    HttpUrl url = new HttpUrl.Builder()
                            .scheme("http")
                            .host("138.91.61.37")
                            .addPathSegment("AppDashboard")
                            .addPathSegment("api")
                            .addPathSegment("Account/")
                            .addQueryParameter("email", etEmail.getText().toString())
                            .addQueryParameter("password", etPassword.getText().toString())
                            .addQueryParameter("imei", telephonyManager.getDeviceId().toString())
                            .build();

                    doGetRequest(url);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    void doGetRequest(HttpUrl url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Error
                        String hello = "failed call";
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //Success
                        final String responseData = response.body().string();
                        final int responseCode = response.code();

                        if (responseCode == 400) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    etEmail.setText("");
                                    etPassword.setText("");
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                                    dialogBuilder.setMessage(responseData);
                                    dialogBuilder.setPositiveButton("Ok", null);
                                    dialogBuilder.show();
                                }
                            });
                        } else {
                            try {
                                jsonResponse = new JSONObject(responseData);
                                name = jsonResponse.getString("Name");
                                email = jsonResponse.getString("Email");
                                pass = jsonResponse.getString("Password");
                                userId = jsonResponse.getInt("Id");
                                CheckGCMRegistration(userId);

                                User user = new User(name, email, pass);
                                logUserIn(user);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void CheckGCMRegistration(Integer userId) {
        context = getApplicationContext();

        gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegistrationId(context);

        if (regid.isEmpty())
        {
            registerInBackground();

            saveGCMRegId(userId, regid);

            //now send to my server api to save registration id on current device
        }
        else
        {
            Log.d(TAG, "No valid Google Play Services APK found.");
        }

    }

    private void saveGCMRegId(Integer userId, String regid) {
        try {
            HttpUrl url = new HttpUrl.Builder()
                    .scheme("http")
                    .host("138.91.61.37")
                    .addPathSegment("AppDashboard")
                    .addPathSegment("api")
                    .addPathSegment("GCM/")
                    .addQueryParameter("userId", userId.toString())
                    .addQueryParameter("regId", regid)
                    .build();

            doPutRequest(url);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

     void doPutRequest(HttpUrl url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();
         //need to add some sort of authentication
        client.newCall(request)
                .enqueue(new Callback(){

                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //once complete I want to do nothing
                    }
                });
    }

    private void logUserIn(User user) {
        userLocalStore.StoreUserData(user);
        userLocalStore.SetUserLoggedIn(true);
        startActivity(new Intent(this, MainActivity.class));
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int resultCode = api.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {

            if (api.isUserResolvableError(resultCode)) {
                api.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d(TAG, "This device is not supported - Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context)
    {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.d(TAG, "Registration ID not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.d(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context)
    {
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground(){

        String msg = "";
        try {
            if(gcm == null){
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            regid = gcm.register(SENDER_ID);
        }
        catch (IOException e) {
            msg = "Error :" + e.getMessage();
        }
    }
}
