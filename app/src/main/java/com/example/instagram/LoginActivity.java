package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.instagram.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    ActivityLoginBinding app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(ParseUser.getCurrentUser()!=null){
            goMainActivity();
        }

        app = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        app.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = app.etUsername.getText().toString();
                String password = app.etPassword.getText().toString();
                loginUser(username,password);
            }
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e!=null){
                    Log.d(TAG,"Issue with login",e);
                    Toast.makeText(LoginActivity.this,getString(R.string.incorrect),Toast.LENGTH_SHORT);
                }
                else{
                    goMainActivity();
                }
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }
}