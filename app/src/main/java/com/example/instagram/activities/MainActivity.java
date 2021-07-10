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

/*
    Activity to navigate between the Timeline, Post and Profile fragments
 */
@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private ActivityMainBinding app;
    private Context mContext;

    private User mCurrentUser;

    /*
        Sets up the activity's methods

        @param Bundle savedInstanceState - The last saved instance of the
        activity

        @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = MainActivity.this;

        mCurrentUser = User.getCurrentUser();

        setupLayout();

        setupNavigationBar();

        setLogoutButtonListener();

    }

    /*
        Sets up the graphical elements of the activity

        @param none

        @return void
     */
    private void setupLayout(){

        //Setup viewbinding
        app = ActivityMainBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        //Set the color of the bottom navigation bar
        Objects.requireNonNull(app.bottomNavigation).setBackgroundColor(getResources().getColor(R.color.dark));

        //Set a customa ction bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_main);
    }

    /*
        Sets up the bottom navigation bar's behavior

        @param none

        @return void
     */
    private void setupNavigationBar(){

        final FragmentManager fragmentManager = getSupportFragmentManager();

        //Ensure that the id's of the navigation items are final for the switch
        final int actionHome = R.id.actionHome;
        final int actionProfile = R.id.actionProfile;
        final int actionPost = R.id.actionPost;

        //Set a listener for the navigation items
        app.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment;

            //Navigate to a different fragment depending on the item selected
            //and update the item's icons to highlight the one selected
            switch(item.getItemId()){

                //Home Item selected
                case actionHome:
                    fragment = new HomeFragment();
                    changeIcons(actionHome,R.drawable.instagram_home_filled_24);
                    break;

                //Post item selected
                case actionPost:
                    fragment = new PostFragment();
                    changeIcons(actionPost,R.drawable.instagram_new_post_filled_24);
                    break;

                //Profile item selected
                case actionProfile:

                    //Put the current user into the arguments of the fragment
                    fragment = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", mCurrentUser);
                    fragment.setArguments(bundle);
                    changeIcons(actionProfile,R.drawable.instagram_user_filled_24);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + item.getItemId());
            }

            //Open the selected fragment
            fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit();
            return true;
        });

        //Set the default window to be the Home
        app.bottomNavigation.setSelectedItemId(R.id.actionHome);
    }

    /*
        Sets the listener for the logout button and navigate to the login activity

        @param none

        @return void
     */
    private void setLogoutButtonListener(){
        findViewById(R.id.btnLogout).setOnClickListener(v -> {

            //Log the user out
            ParseUser.logOut();

            //Navigate to the login activity
            Intent i = new Intent(mContext,LoginActivity.class);
            startActivity(i);
        });
    }

    /*
        Highlights the selected icon and removes any other highlights

        @param int actionIcon - The id of the highlighted icon
        @param int drawable - The highlighted icon's filled drawable

        @return void
     */
    private void changeIcons(int actionIcon, int drawable) {

        //Remove all highlights
        app.bottomNavigation.getMenu().findItem(R.id.actionHome).setIcon(R.drawable.instagram_home_outline_24);
        app.bottomNavigation.getMenu().findItem(R.id.actionPost).setIcon(R.drawable.instagram_new_post_outline_24);
        app.bottomNavigation.getMenu().findItem(R.id.actionProfile).setIcon(R.drawable.instagram_user_outline_24);

        //Highlight the selected icon
        app.bottomNavigation.getMenu().findItem(actionIcon).setIcon(drawable);
    }

}