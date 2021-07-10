package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagram.R;
import com.example.instagram.databinding.ActivitySignupBinding;
import com.example.instagram.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    public static final String TAG = "ActivitySignup";
    private static final int PICK_IMAGE = 1;

    private ActivitySignupBinding app;
    Context mContext;
    
    private Bitmap mProfilePicture;

    /*
        Sets up the activity's methods
        
        @param Bundle savedInstanceState - The last saved instance of the 
        activity
        
        @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = SignupActivity.this;
        
        setupLayout();

        setupSignupButtonListener();

        setupProfilePictureListener();

    }

    /*
        Sets up the graphical elements of the activity
        
        @param none 
        
        @return void
     */
    private void setupLayout() {
        
        //Remove the actionbar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_signup);

        //Setup viewbinding
        app = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        //Load the profile picture placeholder with rounded corners
        Glide.with(mContext)
                .load(R.drawable.placeholder_profile)
                .transform(new RoundedCorners(300))
                .into(app.ivProfilePicture);

    }

    /*
        Sets a listener for the signup button to try to create a new user

        @param none

        @return void
     */
    private void setupSignupButtonListener() {
        app.btnSignup.setOnClickListener(v -> {

            //Get the inputed texts
            String username = app.etUsername.getText().toString();
            String password = app.etPassword.getText().toString();
            String confirm = app.etConfirm.getText().toString();
            String email = app.etEmail.getText().toString();

            //Verify that the passwords match
            if(confirm.equals(password)){

                //Try to sign the user up
                signupUser(username,password,email);
            } else{
                Toast.makeText(mContext,"Passwords do not match.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
        Sets a listener for when the user clicks on the profile picture to change it

        @param none

        @return void
     */
    private void setupProfilePictureListener(){
        app.ivProfilePicture.setOnClickListener(v ->{

            //Prompt the user to pick a file from their device
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            //Prompt the user to pick the folder with the desired image
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");

            //Prompt the user to select the image
            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

            //Load the pick image process
            //noinspection deprecation
            startActivityForResult(chooserIntent, PICK_IMAGE);
        });

    }

    /*
        Launches after the user has selected an image and replaces the placeholder
        with it

        @param int requestCode - The code to verify the process corresponds to the
                                   picking of the image
        @param int resultCode - Code to verify that the activity was completed corrrectly
        @param Intent data - The image thata retrieved from the activity

        @return none
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If the activity corresponds to selecting an image
        if (requestCode == PICK_IMAGE) {
            try {

                //Get the selected profile picture from the datastream
                InputStream inputStream = mContext.getContentResolver().openInputStream(data.getData());
                mProfilePicture = BitmapFactory.decodeStream(inputStream);

                //Load the profile picture into the placeholder
                Glide.with(mContext)
                        .load(mProfilePicture)
                        .transform(new RoundedCorners(300))
                        .into(app.ivProfilePicture);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /*
        Creates a new user from the selected information and registers it into
        the database

        @param String username - The selected username
        @param String password - The selected password
        @param String email - The selected email

        @return void
     */
    private void signupUser(String username, String password,String email) {

        //Create a new user with the information
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        //Transform the selected profile picture bitmap into a ParseFile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mProfilePicture.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] image = stream.toByteArray();
        ParseFile profilePicture  = new ParseFile("profile_picture.jpeg", image);

        //Save the profile picture into the database
        profilePicture.saveInBackground((SaveCallback) fe -> {

            //If the image was saved correctly
            if(fe==null){

               //Set the image as the user's profile picture
               user.setProfilePicture(profilePicture);

               //Attempt to sign the user up
               user.signUpInBackground(e -> {
                   if (e == null) {

                       //Log in as the newly created user
                       loginUser(username,password);
                   } else {
                       Log.d(TAG,"Signup failed",e);
                       Toast.makeText(mContext,"Signup failed.",Toast.LENGTH_SHORT).show();
                   }
               });
           } else {
               fe.printStackTrace();
           }
        });
    }

    /*
        Logs the user in with username and password

        @param String username - The user's username
        @param String password - The user's password

        @return void

     */
    private void loginUser(String username, String password) {

        //Log the user in
        ParseUser.logInInBackground(username, password, (user, e) -> {

            //If the login was successful
            if(e==null){

                //Go to the main activity
                navigateToMain();
            } else{
                Log.d(TAG,"Issue with login",e);
                Toast.makeText(mContext,getString(R.string.incorrect),Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
        Navigates to the main activity

        @param none

        @return void
     */
    private void navigateToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

}