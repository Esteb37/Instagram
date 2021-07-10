package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;

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

    private Bitmap mProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupLayout();

        setupSignupButtonListener();

        setupProfilePictureListener();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            try {
                InputStream inputStream = SignupActivity.this.getContentResolver().openInputStream(data.getData());
                mProfilePicture = BitmapFactory.decodeStream(inputStream);
                Glide.with(SignupActivity.this)
                        .load(mProfilePicture)
                        .transform(new RoundedCorners(300))
                        .into(app.ivProfilePicture);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupLayout() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_signup);

        app = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        Glide.with(SignupActivity.this)
                .load(R.drawable.placeholder_profile)
                .transform(new RoundedCorners(300))
                .into(app.ivProfilePicture);

    }

    private void setupSignupButtonListener() {
        app.btnSignup.setOnClickListener(v -> {
            String username = app.etUsername.getText().toString();
            String password = app.etPassword.getText().toString();
            String confirm = app.etConfirm.getText().toString();
            String email = app.etEmail.getText().toString();
            if(confirm.equals(password)){
                signupUser(username,password,email);
            } else{
                Toast.makeText(SignupActivity.this,"Passwords do not match.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupProfilePictureListener(){
        app.ivProfilePicture.setOnClickListener(v ->{
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

            //noinspection deprecation
            startActivityForResult(chooserIntent, PICK_IMAGE);
        });

    }

    private void signupUser(String username, String password,String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mProfilePicture.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] image = stream.toByteArray();
        ParseFile profilePicture  = new ParseFile("profile_picture.jpeg", image);

        profilePicture.saveInBackground((SaveCallback) fe -> {
           if(fe==null){
               user.setProfilePicture(profilePicture);

               user.signUpInBackground(e -> {
                   if (e == null) {
                       loginUser(username,password);
                   } else {
                       Log.d(TAG,"Signup failed",e);
                       Toast.makeText(SignupActivity.this,"Signup failed.",Toast.LENGTH_SHORT).show();
                   }
               });
           } else {
               fe.printStackTrace();
           }
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, (user, e) -> {
            if(e==null){
                navigateToMain();
            } else{
                Log.d(TAG,"Issue with login",e);
                Toast.makeText(SignupActivity.this,getString(R.string.incorrect),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

}