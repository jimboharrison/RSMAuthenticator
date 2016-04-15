package com.rsm.rsmauthenticator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.logging.Handler;

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
    String username;
    String userEmail;
    String recentAppName;
    String recentRequestTime;
    String isExpired;
    String Otp;
    Boolean IsAwaitingResponse;
    int RequestId;
    private Object activeRequests;
    String serverApiHost = "jh-devserver.cloudapp.net";
    TelephonyManager telephonyManager;
    OkHttpClient client;
    TextView tvRecentAppName, tvRecentRequestTime, tvIsExpired;
    LinearLayout recentRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        userLocalStore = new UserLocalStore(this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        TextView tvUserName = (TextView) findViewById(R.id.userName);
        TextView tvUserEmail = (TextView) findViewById(R.id.userEmail);

        recentRequest = (LinearLayout) findViewById(R.id.recentRequest);

        tvRecentAppName = (TextView) findViewById(R.id.recentAppName);
        tvRecentRequestTime = (TextView) findViewById(R.id.recentRequestTime);
        tvIsExpired = (TextView) findViewById(R.id.isExpired);

        client = new OkHttpClient();
        username = userLocalStore.getLoggedInUser().name;
        userEmail = userLocalStore.getLoggedInUser().email;

        tvUserEmail.setText(userEmail);
        tvUserName.setText(username);

        btLogout = (Button) findViewById(R.id.btLogout);
        btLogout.setOnClickListener(this);
        recentRequest.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(authenticateUser() == true){
            getActiveRequests();
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
            case R.id.recentRequest:
                if(IsAwaitingResponse != null){
                    if(IsAwaitingResponse){
                        Intent intent = new Intent(this, AccessResponseActivity.class);
                        intent.putExtra("otp", Otp);
                        intent.putExtra("appName", recentAppName);
                        intent.putExtra("requestTime", recentRequestTime);
                        intent.putExtra("requestId", Integer.toString(RequestId));
                        intent.putExtra("username", userLocalStore.getLoggedInUser().name);
                        startActivity(intent);
                    }else
                    {
                        Intent intent = new Intent(this, AccessRequestActivity.class);
                        intent.putExtra("otp", Otp);
                        intent.putExtra("appName", recentAppName);
                        intent.putExtra("requestTime", recentRequestTime);
                        intent.putExtra("username", userLocalStore.getLoggedInUser().name);
                        startActivity(intent);
                    }
                }

                break;
        }
    }

  public Object getActiveRequests() {
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

                        if (!responseData.equals("null")) {
                            Gson gson = new Gson();
                            JsonElement element = gson.fromJson(responseData, JsonElement.class);
                            JsonObject jsonObject = element.getAsJsonObject();
                            AccessRequest request = gson.fromJson(jsonObject, AccessRequest.class);
                            IsAwaitingResponse = request.IsAwaitingResponse;
                            Otp = request.Otp;
                            RequestId = request.RequestId;
                            recentAppName = request.AppName.toString();
                            recentRequestTime = request.RequestTime.toString();
                            if (request.IsExpired)
                                isExpired = "Has Expired";
                            else isExpired = "Active";

                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvRecentAppName.setText(recentAppName);
                                    tvRecentRequestTime.setText(recentRequestTime);
                                    tvIsExpired.setText(isExpired);
                                }
                            });
                        }
                    }
                });

        return activeRequests;
    }


}
