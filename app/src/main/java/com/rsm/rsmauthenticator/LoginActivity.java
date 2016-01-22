package com.rsm.rsmauthenticator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.loopj.android.http.*;
import org.json.*;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by James on 08/01/2016.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btLogin;
    EditText etEmail, etPassword;
    TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btLogin = (Button) findViewById(R.id.btLogin);

        btLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //if login button is clicked
            case R.id.btLogin:
                RequestParams params = new RequestParams();
                params.add("email", etEmail.toString());
                params.add("password", etPassword.toString());
                params.add("password", telephonyManager.getDeviceId());
                HttpClient.get("Account", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
                        //http://loopj.com/android-async-http/
                        goToMainActivity();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                        // Pull out the first event on the public timeline
                        JSONObject firstEvent = null;
                        try {
                            firstEvent = (JSONObject) timeline.get(0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String tweetText = null;
                        try {
                            tweetText = firstEvent.getString("text");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Do something with the response
                        System.out.println(tweetText);
                    }


                });

                break;
        }
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
