package com.example.instagram.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import com.example.instagram.R;
import com.example.instagram.databinding.ActivityMainBinding;
import com.example.instagram.fragments.HomeFragment;
import com.example.instagram.fragments.PostFragment;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

   ActivityMainBinding app;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = ActivityMainBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        Objects.requireNonNull(app.bottomNavigation).setBackgroundColor(getResources().getColor(R.color.dark));


        final FragmentManager fragmentManager = getSupportFragmentManager();

        app.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = new Fragment();
            resetIcons();
            switch(item.getItemId()){
                case R.id.actionHome:
                    fragment = new HomeFragment();
                    app.bottomNavigation.getMenu().findItem(R.id.actionHome).setIcon(R.drawable.instagram_home_filled_24);
                    break;
                case R.id.actionPost:
                    fragment = new PostFragment();
                    app.bottomNavigation.getMenu().findItem(R.id.actionPost).setIcon(R.drawable.instagram_new_post_filled_24);
                    break;
                case R.id.actionProfile:
                    fragment = new PostFragment();
                    app.bottomNavigation.getMenu().findItem(R.id.actionProfile).setIcon(R.drawable.instagram_user_filled_24);
                    break;
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit();
            return true;
        });
        app.bottomNavigation.setSelectedItemId(R.id.actionHome);
    }


    private void resetIcons(){
        app.bottomNavigation.getMenu().findItem(R.id.actionHome).setIcon(R.drawable.instagram_home_outline_24);
        app.bottomNavigation.getMenu().findItem(R.id.actionPost).setIcon(R.drawable.instagram_new_post_outline_24);
        app.bottomNavigation.getMenu().findItem(R.id.actionProfile).setIcon(R.drawable.instagram_user_outline_24);
    }


}