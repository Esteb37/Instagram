package com.example.instagram.activities;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.instagram.R;
import com.example.instagram.databinding.ActivityMainBinding;
import com.example.instagram.fragments.HomeFragment;
import com.example.instagram.fragments.PostFragment;
import com.example.instagram.fragments.ProfileFragment;
import com.example.instagram.models.User;
import com.parse.ParseUser;

import java.util.Objects;


@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private ActivityMainBinding app;
    private Context mContext;

    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = MainActivity.this;

        mCurrentUser = User.getCurrentUser();

        setupLayout();

        setupNavigationBar();

        setLogoutButtonListener();

    }

    private void setupLayout(){
        app = ActivityMainBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        Objects.requireNonNull(app.bottomNavigation).setBackgroundColor(getResources().getColor(R.color.dark));

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_main);
    }

    private void setupNavigationBar(){

        final FragmentManager fragmentManager = getSupportFragmentManager();

        final int actionHome = R.id.actionHome;
        final int actionProfile = R.id.actionProfile;
        final int actionPost = R.id.actionPost;

        app.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment;
            switch(item.getItemId()){
                case actionHome:
                    fragment = new HomeFragment();
                    changeIcons(actionHome,R.drawable.instagram_home_filled_24);
                    break;
                case actionPost:
                    fragment = new PostFragment();
                    changeIcons(actionPost,R.drawable.instagram_new_post_filled_24);
                    break;
                case actionProfile:
                    fragment = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", mCurrentUser);
                    fragment.setArguments(bundle);
                    changeIcons(actionProfile,R.drawable.instagram_user_filled_24);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + item.getItemId());
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit();
            return true;
        });
        app.bottomNavigation.setSelectedItemId(R.id.actionHome);
    }

    private void setLogoutButtonListener(){
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            ParseUser.logOut();
            Intent i = new Intent(mContext,LoginActivity.class);
            startActivity(i);
        });
    }

    private void changeIcons(int actionIcon, int drawable) {
        app.bottomNavigation.getMenu().findItem(actionIcon).setIcon(drawable);
        app.bottomNavigation.getMenu().findItem(R.id.actionHome).setIcon(R.drawable.instagram_home_outline_24);
        app.bottomNavigation.getMenu().findItem(R.id.actionPost).setIcon(R.drawable.instagram_new_post_outline_24);
        app.bottomNavigation.getMenu().findItem(R.id.actionProfile).setIcon(R.drawable.instagram_user_outline_24);
    }

}