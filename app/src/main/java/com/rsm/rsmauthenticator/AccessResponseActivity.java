package com.rsm.rsmauthenticator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AccessResponseActivity extends AppCompatActivity implements View.OnClickListener {

    Button btApproveRequest, btDeclineRequest, btHome;
    String serverApiHost = "jh-devserver.cloudapp.net";
    String otp, requestTime, username, appName, requestId;
    OkHttpClient client;
    UserLocalStore localStore;
    TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_response);

        Intent myIntent = getIntent(); // gets the previously created intent
        otp = myIntent.getStringExtra("otp"); // will return otp
        username = myIntent.getStringExtra("username");
        appName = myIntent.getStringExtra("appName");
        requestTime = myIntent.getStringExtra("requestTime");
        requestId = myIntent.getStringExtra("requestId");
        client = new OkHttpClient();
        localStore = new UserLocalStore(this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        SimpleDateFormat format = new SimpleDateFormat("MMM EEE dd HH:mm:ss zzz yyyy");

        try{
            Date parseDate = format.parse(requestTime);
        }catch (ParseException e) {
            e.printStackTrace();
        }

        TextView tvUsername = (TextView)findViewById(R.id.responseRequestUsername);
        TextView tvApp = (TextView)findViewById(R.id.responseRequesApplication);
        TextView tvDate = (TextView)findViewById(R.id.responseRequestDate);
        TextView tvOtp = (TextView)findViewById(R.id.responseRequesOtp);

        btApproveRequest = (Button) findViewById(R.id.btnApproveRequest);
        btDeclineRequest = (Button) findViewById(R.id.btnDeclineRequest);
        btHome = (Button) findViewById(R.id.btHome);

        tvUsername.setText(username);
        tvApp.setText(appName);
        tvDate.setText(requestTime);
        tvOtp.setText(otp);

        btApproveRequest.setOnClickListener(this);
        btDeclineRequest.setOnClickListener(this);
        btHome.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnApproveRequest:
                SendResponse(2);
                break;

            case R.id.btnDeclineRequest:
                SendResponse(1);
                break;

            case R.id.btHome:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }

    private void SendResponse(int response) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(serverApiHost)
                .addPathSegment("AppDashboard")
                .addPathSegment("api")
                .addPathSegment("AccessRequest")
                .build();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put("AccessReuqestId", Integer.parseInt(requestId));
            jsonObject.put("AccessRequestResponse", response);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = jsonObject.toString();

        RequestBody requestBody = RequestBody.create(JSON, json);

        //adding basic authentication from logged in user on mobile app
        User user = localStore.getLoggedInUser();
        String credential = Credentials.basic(user.email, user.password);

        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", credential)// adding authorization header to request
                .header("DeviceIMEI", telephonyManager.getDeviceId())//security step
                .post(requestBody)
                .build();

        //this then posts the response
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String hello = "failed call";
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final int responseCode = response.code();

                        if (responseCode == 401){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Unauthorized Access! Please try again or contact system Admin", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        if (responseCode != 400) {
                            backToMAinActivity();
                        }
                    }
                });
    }

    private void backToMAinActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
