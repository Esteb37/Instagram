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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding app;
    public static final String TAG = "ActivitySignup";
    public static final int PICK_IMAGE = 1;
    private Bitmap mProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_signup);

        setupLayout();

        setSignupListener();

        setImageListener();

    }

    private void setupLayout() {
        app = ActivitySignupBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        Glide.with(SignupActivity.this)
                .load(R.drawable.placeholder_profile)
                .transform(new RoundedCorners(300))
                .into(app.ivProfilePicture);

    }

    private void setSignupListener() {
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

    private void setImageListener(){
        app.ivProfilePicture.setOnClickListener(v ->{
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

            startActivityForResult(chooserIntent, PICK_IMAGE);
        });

    }

    private void signupUser(String username, String password,String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        mProfilePicture.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] image = stream.toByteArray();

        // Create the ParseFile
        ParseFile profilePicture  = new ParseFile("profile_picture.jpeg", image);

        profilePicture.saveInBackground((SaveCallback) fe -> {
           if(fe==null){
               user.setProfilePicture(profilePicture);

               user.signUpInBackground(e -> {
                   if (e != null) {
                       Log.d(TAG,"Signup failed",e);
                       Toast.makeText(SignupActivity.this,"Signup failed.",Toast.LENGTH_SHORT).show();
                   } else {
                       loginUser(username,password);
                   }
               });
           } else {
               fe.printStackTrace();
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

}