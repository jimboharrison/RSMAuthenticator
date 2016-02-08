package com.rsm.rsmauthenticator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    String PROJECT_NUMBER = "540327761504";
    String serverApiHost = "40.87.151.116";
    GCMClientManager pushClientManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //if login button is clicked
            case R.id.btLogin:
                try {
                    HttpUrl url = new HttpUrl.Builder()
                            .scheme("http")
                            .host(serverApiHost)
                            .addPathSegment("AppDashboard")
                            .addPathSegment("api")
                            .addPathSegment("Account/")
                            .addQueryParameter("email", etEmail.getText().toString())
                            .addQueryParameter("password", etPassword.getText().toString())
                            .addQueryParameter("imei", telephonyManager.getDeviceId())
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
                                    Alertdialog(responseData);
                                }
                            });
                        } else {
                            try {
                                jsonResponse = new JSONObject(responseData);
                                name = jsonResponse.getString("Name");
                                email = jsonResponse.getString("Email");
                                pass = jsonResponse.getString("Password");
                                userId = jsonResponse.getInt("Id");
                                User user = new User(name, email, pass, userId);

                                boolean regok = RegisterGCMClient(userId);

                                logUserIn(user);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void Alertdialog(String responseData) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage(responseData);
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private boolean RegisterGCMClient(final Integer userId) {
        pushClientManager = new GCMClientManager(this,PROJECT_NUMBER, userLocalStore);
        String regId = pushClientManager.registerIfNeeded();
        try {
            boolean ok = SendGCMRegIdToServer(regId, userId);
            if(ok)
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean SendGCMRegIdToServer(String registrationId, Integer userId) throws IOException {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(serverApiHost)
                .addPathSegment("AppDashboard")
                .addPathSegment("api")
                .addPathSegment("GCM")
                .build();

        MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("UserId", userId);
            jsonObject.put("RegId", registrationId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = jsonObject.toString();

        RequestBody requestBody = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        //this should post the date to my server
        Response response = client.newCall(request).execute();
        if(response.code() == 400)
            return false;

        return true;
    }

    private void logUserIn(User user) {
        userLocalStore.StoreUserData(user);
        userLocalStore.SetUserLoggedIn(true);
        startActivity(new Intent(this, MainActivity.class));
    }
}









