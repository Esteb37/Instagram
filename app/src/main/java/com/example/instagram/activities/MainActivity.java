package com.example.instagram.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import com.example.instagram.R;
import com.example.instagram.databinding.ActivityMainBinding;



public class MainActivity extends AppCompatActivity {

   ActivityMainBinding app;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = ActivityMainBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        app.bottomNavigation.setOnNavigationItemReselectedListener(item -> {
            Fragment fragment = new Fragment();

            switch(item.getItemId()){
                case R.id.actionHome:
                    fragment = new HomeFragment();
                    break;
                case R.id.actionPost:
                    fragment = new PostFragment();
                    break;
                case R.id.actionProfile:
                    fragment = new PostFragment();
                    break;
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit();
        });
    }





}