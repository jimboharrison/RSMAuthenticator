package com.rsm.rsmauthenticator;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AccessRequestActivity extends AppCompatActivity implements View.OnClickListener {

    Button btViewActiveRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_request);

        Intent myIntent = getIntent(); // gets the previously created intent
        String otp = myIntent.getStringExtra("otp"); // will return otp
        String requestTime = myIntent.getStringExtra("requestTime");
        String username = myIntent.getStringExtra("username");
        String appName = myIntent.getStringExtra("appName");
        String notificationId = myIntent.getStringExtra("notificationId");
        String ns = Context.NOTIFICATION_SERVICE;

        if (notificationId != null && !notificationId.isEmpty()) {
            NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
            nMgr.cancel(Integer.parseInt(notificationId));
        }

        SimpleDateFormat format = new SimpleDateFormat("MMM EEE dd HH:mm:ss zzz yyyy");

        btViewActiveRequests = (Button) findViewById(R.id.btViewActiveRequests);
        btViewActiveRequests.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btViewActiveRequests:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}
