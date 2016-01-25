package com.rsm.rsmauthenticator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        client = new OkHttpClient();
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btLogin = (Button) findViewById(R.id.btLogin);

        btLogin.setOnClickListener(this);
        userLocalStore = new UserLocalStore(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
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

    void doGetRequest(HttpUrl url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //Error
                        String hello = "hello";
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //Success
                        String responseData = response.body().string();
                        try {
                            JSONObject json = new JSONObject(responseData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
