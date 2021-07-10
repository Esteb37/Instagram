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
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;


public class PostFragment extends Fragment {

    FragmentPostBinding app;

    public static final String TAG = "PostFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 37;
    public static final String FILE_NAME = "photo.jpg";

    private File mPhotoFile;
    Context mContext;
    User mCurrentUser;

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        app = FragmentPostBinding.inflate(inflater, container, false);
        return app.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mContext = getContext();

        mCurrentUser = User.getCurrentUser();

        setSubmitListener();

        app.btnPicture.setOnClickListener(v-> launchCamera());
    }

    private void setSubmitListener(){
        app.btnSubmit.setOnClickListener(v -> {
            String description = app.etDescription.getText().toString();
            if(description.isEmpty()){
                Toast.makeText(mContext,"Description can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            savePost(description,mPhotoFile);
        });
    }
    private void savePost(String description, File mPhotoFile) {
        Post post = new Post();
        post.setDescription(description);
        post.setUser(mCurrentUser);
        app.pbLoading.setVisibility(View.VISIBLE);
        if(mPhotoFile==null || app.ivPost.getDrawable() == null){
            Toast.makeText(mContext, "There is no image!", Toast.LENGTH_SHORT).show();
            return;
        }
        post.setImage(new ParseFile(mPhotoFile));

        post.saveInBackground(e -> {
            if(e != null){
                Log.d(TAG,"Error while saving",e);
                Toast.makeText(mContext,"Unable to save post",Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(mContext, "Post saved successfully.", Toast.LENGTH_SHORT).show();

            mCurrentUser.addPost(post);

            app.pbLoading.setVisibility(View.GONE);

            final FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

            fragmentManager.beginTransaction().replace(R.id.flContainer,new HomeFragment()).commit();
        });
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        mPhotoFile = getPhotoFileUri(FILE_NAME);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(mContext, "com.codepath.fileprovider", mPhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        // Start the image capture intent to take photo
        if (intent.resolveActivity(mContext.getPackageManager()) != null)
            //noinspection deprecation
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(mPhotoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                app.ivPost.setImageBitmap(takenImage);
                app.ivPost.setVisibility(View.VISIBLE);
            } else { // Result was a failure
                Toast.makeText(mContext, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri (String fileName){
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }


}