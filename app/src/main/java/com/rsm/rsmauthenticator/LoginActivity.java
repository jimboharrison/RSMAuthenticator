package com.rsm.rsmauthenticator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by James on 08/01/2016.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btLogin;
    EditText etEmail, etPassword;

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
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}
