package com.example.instagram.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagram.R;
import com.example.instagram.databinding.ItemPostBinding;
import com.example.instagram.models.Post;
import com.example.instagram.models.User;

import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.Locale;
import java.util.Objects;

/*
    Activity for displaying the details of a post
 */
public class DetailsActivity extends AppCompatActivity {

    public static final String TAG = "DetailsActivity";

    private ItemPostBinding app;
    private Context mContext;

    private Post mPost;
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

        mContext = DetailsActivity.this;

        //Get the post for which the details are being displayed
        mPost = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        mCurrentUser = User.getCurrentUser();

        setupLayout();

        loadPostDetails();

        loadUserDetails();

        setLikeButtonListener();

        setCommentButtonListener();
    }

    /*
        Sets up the graphical elements of the activity

        @param none

        @return void
     */
    private void setupLayout(){
        setContentView(R.layout.item_post);

        //Implement viewbinding
        app = ItemPostBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        //Set a custom toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_detail);
        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.post);
    }

    /*
        Loads the post details into the window

        @param none

        @return void
     */
    private void loadPostDetails(){

        //Set text details
        app.tvDescription.setText(mPost.getDescription());
        app.tvUsername.setText(mPost.getUser().getUsername());
        app.tvUsername2.setText(mPost.getUser().getUsername());
        app.tvTimestamp.setText(Post.calculateTimeAgo(mPost.getCreatedAt()));

        //Display the "like" button state depending on if the user has
        // liked the post
        app.btnLike.setImageResource(mPost.isLikedByUser(mCurrentUser) ? R.drawable.ufi_heart_active: R.drawable.ufi_heart);

        //Show the likes as "user and x others have liked the post"
        showLikes();

        //Load the image from the post
        ParseFile image = mPost.getImage();
        if(image != null){
            Glide.with(mContext)
                    .load(image.getUrl())
                    .into(app.ivContent);
        }

        //Load the user that published the posts' profile picture
        ParseFile profilePicture = mPost.getUser().getProfilePicture();
        if(profilePicture != null) {
            Glide.with(mContext)
                    .load(profilePicture.getUrl())
                    .transform(new RoundedCorners(100), new CenterCrop())
                    .into(app.ivProfilePicture);
        }
    }

    /*
        Loads the profile picture of the current user into the "add
        comment" button

        @param none

        @return void
     */
    private void loadUserDetails() {

        //Load the current user's profile picture
        ParseFile currentProfile = mCurrentUser.getProfilePicture();
        if(currentProfile != null){
            Glide.with(mContext)
                    .load(currentProfile.getUrl())
                    .transform(new RoundedCorners(100),new CenterCrop())
                    .into(app.ivProfilePicture2);
        }
    }

    /*
        Sets up the listener for the like button to add a like to the post

        @param none

        @return void
     */
    private void setLikeButtonListener(){
        app.btnLike.setOnClickListener(v -> {

            //If the user has liked the post, unlike and viceversa,
            // updating the button image
            if(mPost.isLikedByUser(mCurrentUser)) {
                app.btnLike.setImageResource(R.drawable.ufi_heart);
                mCurrentUser.unlikePost(mPost);
            } else {
                app.btnLike.setImageResource(R.drawable.ufi_heart_active);
                mCurrentUser.likePost(mPost);
            }

            //Show the updated like count
            showLikes();
        });
    }

    /*
        Sets up the listener for the comment button to add a comment to
        the post

        @param none

        @return void
     */
    private void setCommentButtonListener(){
        app.btnComment.setOnClickListener(v -> {

            //Navigate to the "Comments" activity
            Intent i = new Intent(mContext, CommentsActivity.class);
            i.putExtra("post",Parcels.wrap(mPost));
            startActivity(i);
        });
    }

    /*
        Shows the likes as "user and x others have liked the post"

        @param none

        @return void
     */
    private void showLikes(){

        //If at least one user has liked the post
        if(mPost.getLikes()>0){

            //Show the user that liked the post
            app.tvUserLike.setText(mPost.getUserLikes().get(0).getUsername());
            app.clLikedBy.setVisibility(View.VISIBLE);

            //If more than one user has liked the post
            if(mPost.getLikes()>1){

                //Show the "and x others" tag as well
                app.tvLikes.setText(String.format(Locale.US,"%d others.", mPost.getLikes() - 1));
                app.clLikedOthers.setVisibility(View.VISIBLE);
            } else {
                app.clLikedOthers.setVisibility(View.GONE);
            }
        } else{
            app.clLikedBy.setVisibility(View.GONE);
        }
    }
}