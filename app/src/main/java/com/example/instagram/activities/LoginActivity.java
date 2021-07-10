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

/*
    Activity for loggin in as a user
 */
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    private ActivityLoginBinding app;
    private Context mContext;

    /*
        Sets up the activity's methods

        @param Bundle savedInstanceState - The last saved instance of the
        activity

        @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = LoginActivity.this;

        //If the user has already signed in, navigate to the main activity
        if(User.getCurrentUser()!=null){
            navigateToMain();
        }

        setupLayout();

        setLoginButtonListener();

        setSignupButtonListener();

    }

    /*
        Sets up the graphical elements of the activity

        @param none

        @return void
     */
    private void setupLayout(){
        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_login);

        //Setup viewbinding
        app = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

    }

    /*
        Sets the listener for the login button to log the user in with username
        and password

        @param none

        @return void
     */
    private void setLoginButtonListener(){
        app.btnLogin.setOnClickListener(v -> {
            String username = app.etUsername.getText().toString();
            String password = app.etPassword.getText().toString();

            //Try to log the user in with the username and password
            loginUser(username,password);
        });
    }

    /*
        Sets the listener for the signup button to navigate to the signup activity

        @param none

        @return void
     */
    private void setSignupButtonListener(){
        app.btnSignup.setOnClickListener(v->{

            //Navigate to the signup activity
            Intent i = new Intent(this,SignupActivity.class);
            startActivity(i);
        });
    }

    /*
        Tries to log the user in with the selected username and password

        @param String username - The user's username
        @param String password - The user's password

        @return void
     */
    private void loginUser(String username, String password) {

        //Try to log the user in
        ParseUser.logInInBackground(username, password, (user, e) -> {

            //If the login is successful
            if(e==null){
                navigateToMain();
            } else{
                Log.d(TAG,"Issue with login",e);

                //Notify the user that the login was unsuccessful
                Toast.makeText(mContext,getString(R.string.incorrect),Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
        Navigates to the main window

        @param none

        @return void
     */
    private void navigateToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}