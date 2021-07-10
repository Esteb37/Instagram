package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.databinding.ActivityLoginBinding;
import com.example.instagram.models.User;
import com.parse.ParseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    private ActivityLoginBinding app;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = LoginActivity.this;

        if(User.getCurrentUser()!=null){
            navigateToMain();
        }

        setupLayout();

        setLoginButtonListener();

        setSignupButtonListener();

    }

    private void setupLayout(){
        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_login);

        app = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

    }

    private void setLoginButtonListener(){
        app.btnLogin.setOnClickListener(v -> {
            String username = app.etUsername.getText().toString();
            String password = app.etPassword.getText().toString();
            loginUser(username,password);
        });
    }

    private void setSignupButtonListener(){
        app.btnSignup.setOnClickListener(v->{
            Intent i = new Intent(this,SignupActivity.class);
            startActivity(i);
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if(e!=null){
                Log.d(TAG,"Issue with login",e);
                Toast.makeText(mContext,getString(R.string.incorrect),Toast.LENGTH_SHORT).show();
            } else{
                navigateToMain();
            }
        });
    }

    private void navigateToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}