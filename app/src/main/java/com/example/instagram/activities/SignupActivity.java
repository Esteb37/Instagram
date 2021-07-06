package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.databinding.ActivitySignupBinding;
import com.parse.ParseUser;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding app;
    public static final String TAG = "ActivitySignup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        app = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        app.btnSignup.setOnClickListener(v -> {

            String username = app.etUsername.getText().toString();
            String password = app.etPassword.getText().toString();
            String confirm = app.etConfirm.getText().toString();
            String email = app.etEmail.getText().toString();
            if(confirm.equals(password))
                signupUser(username,password,email);
            else{
                Toast.makeText(SignupActivity.this,"Passwords do not match.",Toast.LENGTH_SHORT).show();
            }



        });
    }

    private void signupUser(String username, String password,String email) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.signUpInBackground(e -> {
            if (e != null) {
                Log.d(TAG,"Signup failed",e);
                Toast.makeText(SignupActivity.this,"Signup failed.",Toast.LENGTH_SHORT).show();
            } else {
                loginUser(username,password);
            }
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if(e!=null){
                Log.d(TAG,"Issue with login",e);
                Toast.makeText(SignupActivity.this,getString(R.string.incorrect),Toast.LENGTH_SHORT).show();
            }
            else{
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }


}