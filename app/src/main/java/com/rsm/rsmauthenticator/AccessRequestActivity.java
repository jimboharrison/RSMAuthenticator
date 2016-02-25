package com.rsm.rsmauthenticator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccessRequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_request);

        Intent myIntent = getIntent(); // gets the previously created intent
        String otp = myIntent.getStringExtra("otp"); // will return otp
        String requestTime = myIntent.getStringExtra("requestTime");
        String username = myIntent.getStringExtra("username");
        String appName = myIntent.getStringExtra("appName");



        SimpleDateFormat format = new SimpleDateFormat(
                "MMM EEE dd HH:mm:ss zzz yyyy");

        try {
            Date parseDate = format.parse(requestTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TextView tvUsername = (TextView)findViewById(R.id.requestusername);
        TextView tvApp = (TextView)findViewById(R.id.requestapplication);
        TextView tvDate = (TextView)findViewById(R.id.requestdate);
        TextView tvOtp = (TextView) findViewById(R.id.requestotp);

        tvUsername.setText(username);
        tvApp.setText(appName);
        tvDate.setText(requestTime);
        tvOtp.setText(otp);
    }

}
