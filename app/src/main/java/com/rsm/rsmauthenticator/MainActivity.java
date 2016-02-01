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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btLogout;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        userLocalStore = new UserLocalStore(this);

        btLogout = (Button) findViewById(R.id.btLogout);
        btLogout.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(authenticateUser() == true){

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








}
