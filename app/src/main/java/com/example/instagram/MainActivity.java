package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.instagram.databinding.ActivityMainBinding;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = ActivityMainBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        app.btnLogout.setOnClickListener(v->{
            ParseUser.logOut();
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
        });

    }
}