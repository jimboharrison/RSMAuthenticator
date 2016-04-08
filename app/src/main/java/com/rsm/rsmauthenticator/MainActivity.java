package com.rsm.rsmauthenticator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btLogout;
    UserLocalStore userLocalStore;
    String username, userEmail;
    private Object activeRequests;
    String serverApiHost = "52.169.154.122";
    TelephonyManager telephonyManager;
    OkHttpClient client;
    JSONArray jsonArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        userLocalStore = new UserLocalStore(this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        TextView tvUserName = (TextView) findViewById(R.id.userName);
        TextView tvUserEmail = (TextView) findViewById(R.id.userEmail);
        client = new OkHttpClient();
        username = userLocalStore.getLoggedInUser().name;
        userEmail = userLocalStore.getLoggedInUser().email;

        tvUserEmail.setText(userEmail);
        tvUserName.setText(username);

        btLogout = (Button) findViewById(R.id.btLogout);
        btLogout.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(authenticateUser() == true){
            //getActiveRequests();
        }else{
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private boolean authenticateUser() {
        return userLocalStore.IsUserLoggedIn();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btLogout:
                userLocalStore.clearUserData();
                userLocalStore.SetUserLoggedIn(false);
                startActivity(new Intent(this, LoginActivity.class));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Logged Out", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }


/*    public Object getActiveRequests() {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(serverApiHost)
                .addPathSegment("AppDashboard")
                .addPathSegment("api")
                .addPathSegment("AccessRequest/")
                .addQueryParameter("userId", String.valueOf(userLocalStore.getLoggedInUser().userId))
                .build();

        //adding basic authentication from logged in user on mobile app
        User user = userLocalStore.getLoggedInUser();
        String credential = Credentials.basic(user.email, user.password);

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", credential)
                .header("DeviceIMEI", telephonyManager.getDeviceId())
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String hello = "failed call";
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseData = response.body().string();
                        final int responseCode = response.code();

                        try {
                            jsonArray = new JSONArray(responseData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //need to parse the jsonarray and put into a list of objects that can be displayed
                        for(int i = 0 ; i< jsonArray.length(); i++){

                        }
                    }
                });

        return activeRequests;
    }*/
}
