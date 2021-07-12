package com.example.instagram.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.instagram.R;
import com.example.instagram.databinding.FragmentPostBinding;
import com.example.instagram.models.Post;
import com.example.instagram.models.User;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/*
    Fragment to create a new post
 */
public class PostFragment extends Fragment {

    public static final String TAG = "PostFragment";

    public static final String FILE_NAME = "photo.jpg";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 37;

    //Viewbinder
    private FragmentPostBinding app;

    //Current context
    private Context mContext;

    //Taken photo file
    private File mPhotoFile;

    //Current user
    private User mCurrentUser;

    // Required empty public constructor
    public PostFragment() {}

    /*
        Loads the last saved instance of the fragment

        @param savedInstanceState - The last saved instance
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
        Inflates the fragment with the layout
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        app = FragmentPostBinding.inflate(inflater, container, false);
        return app.getRoot();
    }

    /*
        Sets up the fragment's methods.
     */
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mContext = getContext();

        mCurrentUser = User.getCurrentUser();

        //Launch camera when the "Take Picture" button is clicked
        app.btnPicture.setOnClickListener(v-> launchCamera());

        setSubmitListener();

    }

    /*
        Sets a listener for the "Submit" button
     */
    private void setSubmitListener(){
        app.btnSubmit.setOnClickListener(v -> {

            //Get the description
            String description = app.etDescription.getText().toString();

            //Save the post
            if(description.isEmpty()){
                Toast.makeText(mContext,"Description can't be empty", Toast.LENGTH_SHORT).show();
            } else{
                savePost(description);
            }
        });
    }

    /*
        Saves a post into the database and the user's list of posts

        @param description - The posts description
     */
    private void savePost(String description) {

        //Create a new post with the selected details and the current user
        Post post = new Post();
        post.setDescription(description);
        post.setUser(mCurrentUser);

        //Display the progress bar
        app.pbLoading.setVisibility(View.VISIBLE);

        if(mPhotoFile==null || app.ivPost.getDrawable() == null){
            Toast.makeText(mContext, "There is no image!", Toast.LENGTH_SHORT).show();
        } else {

            //Set the image into the created post object
            post.setImage(new ParseFile(mPhotoFile));

            //Save the post in the database
            post.saveInBackground(e -> {
                if (e != null) {
                    Log.d(TAG, "Error while saving", e);
                    Toast.makeText(mContext, "Unable to save post", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Post saved successfully.", Toast.LENGTH_SHORT).show();

                    //Add the post to the user's list of posts
                    mCurrentUser.addPost(post);

                    //Hide the progressbar
                    app.pbLoading.setVisibility(View.GONE);

                    //Return to the home fragment
                    final FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, new HomeFragment()).commit();
                }
            });
        }
    }

    /*
        Launches the camera for taking a picture.
     */
    @SuppressLint("QueryPermissionsNeeded")
    private void launchCamera() {
        // Create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create a File reference for future access
        mPhotoFile = getPhotoFileUri();

        // Wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(mContext, "com.codepath.fileprovider", mPhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        // Start the image capture intent to take photo
        if (intent.resolveActivity(mContext.getPackageManager()) != null)
            //noinspection deprecation
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /*
        Catches the result image from the camera activity
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                // Retrieve the photo taken by the canmera
                Bitmap takenImage = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());

                // Load the taken image into a preview
                app.ivPost.setImageBitmap(takenImage);
                app.ivPost.setVisibility(View.VISIBLE);

            } else { // Result was a failure
                Toast.makeText(mContext, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /*
        Returns the File for a photo stored on disk given the fileName
     */
    private File getPhotoFileUri(){
        // Get safe storage directory for photos
        File mediaStorageDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + PostFragment.FILE_NAME);
    }
}