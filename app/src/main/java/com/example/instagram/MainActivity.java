package com.example.instagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagram.databinding.ActivityMainBinding;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding app;
    public static final  String TAG = "MainActivity";

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 37;
    public String photoFileName = "photo.jpg";
    private File photoFile;

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

        queryPosts();

        app.btnSubmit.setOnClickListener(v -> {
            String description = app.etDescription.getText().toString();
            if(description.isEmpty()){
                Toast.makeText(MainActivity.this,"Description can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            ParseUser currentUser = ParseUser.getCurrentUser();
            savePost(description,currentUser,photoFile);
        });

        app.btnPicture.setOnClickListener(v->{
            launchCamera();
        });
    }

    private void savePost(String description, ParseUser currentUser, File photoFile) {
        Post post = new Post();
        post.setDescription(description);
        post.setUser(currentUser);

        if(photoFile==null || app.ivPicture.getDrawable() == null){
            Toast.makeText(MainActivity.this, "There is no image!", Toast.LENGTH_SHORT).show();
            return;
        }
        post.setImage(new ParseFile(photoFile));

        post.saveInBackground(e -> {
           if(e != null){
              Log.d(TAG,"Error while saving",e);
              Toast.makeText(MainActivity.this,"Unable to save post",Toast.LENGTH_SHORT).show();
              return;
           }
           Toast.makeText(MainActivity.this, "Post saved successfully.", Toast.LENGTH_SHORT).show();
           app.etDescription.setText("");
           app.ivPicture.setImageResource(0);
        });
    }

    private void queryPosts(){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.findInBackground((posts, e) -> {
            if(e!=null){
                Log.d(TAG,"Issue with getting posts",e);
                return;
            }

            for(Post post : posts){
                Log.i(TAG,"Post: "+post.getDescription()+", username: "+post.getUser().getUsername());
            }
        });
    }

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        // Start the image capture intent to take photo
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                app.ivPicture.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri (String fileName){
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}