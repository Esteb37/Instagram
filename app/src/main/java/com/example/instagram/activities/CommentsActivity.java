package com.example.instagram.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagram.R;
import com.example.instagram.adapters.comments.CommentsAdapter;
import com.example.instagram.databinding.ActivityCommentsBinding;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.example.instagram.models.User;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
    Activity for displaying a post's comments
*/
public class CommentsActivity extends AppCompatActivity {

    public static final String TAG = "CommentsActivity";

    private ActivityCommentsBinding app;
    private Context mContext;

    private User mCurrentUser;
    private Post mCurrentPost;

    private CommentsAdapter mAdapter;

    /*
        Sets up the activity's methods

        @param Bundle savedInstanceState - The last saved instance of the
        activity

        @return void
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = CommentsActivity.this;
        
        mCurrentUser = User.getCurrentUser();

        //Get the post for which the comments are being displayed
        mCurrentPost = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        setupLayout();

        setupRecyclerView();

        setPostButtonListener();

        loadProfilePicture();

        loadComments();
    }

    /*
        Sets up the graphical elements of the activity

        @param none

        @return void
     */
    private void setupLayout(){
        setContentView(R.layout.activity_comments);

        //Implement viewBinding
        app = ActivityCommentsBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        //Setup a custom toolbar for the window
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_detail);
        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.comments);
    }

    /*
        Sets up the recyclerview that will display the comments

        @param none

        @return void
     */
    private void setupRecyclerView(){

        //Get the list of comments
        List<Comment> comments = new ArrayList<>();

        //Create the adapter with the list of comments
        mAdapter = new CommentsAdapter(mContext, comments);

        //Set the recyclerview's manager and adapter
        app.rvComments.setLayoutManager(new LinearLayoutManager(mContext));
        app.rvComments.setAdapter(mAdapter);
    }

    /*
        Queries the list of the post's comments and adds them to the
        recyclerview

        @param none

        @return void
     */
    private void loadComments(){

        //Get a query of comments that belong to this post
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class).whereContains("post",mCurrentPost.getObjectId());

        //Put them in descending order based on date
        query.addDescendingOrder("createdAt");

        //Get the comments and add them to the recyclerview
        query.findInBackground((comments, e) -> mAdapter.addAll(comments));
    }

    /*
        Loads the user's profile picture into the comment text input

        @param none

        @return void
     */
    private void loadProfilePicture(){
        ParseFile profilePicture = mCurrentUser.getProfilePicture();
        assert profilePicture != null;

        //Load the profile picture with rounded corners
        Glide.with(mContext)
                .load(profilePicture.getUrl())
                .transform(new RoundedCorners(100), new CenterCrop())
                .into(app.ivProfilePicture);
    }

    /*
        Sets up the listener for the "Post" button to post the comment

        @param none

        @return void
     */
    private void setPostButtonListener(){
        app.btnPost.setOnClickListener(v -> {

            //Create a new comment with the text content, the current user
            // and the current post
            Comment comment = (Comment) ParseObject.create("Comment");
            comment.setContent(Objects.requireNonNull(app.etComment.getText()).toString());
            comment.setUser(mCurrentUser);
            comment.setPost(mCurrentPost);

            //Save the comment
            comment.saveInBackground(e -> {

                //If the save was successful
                if(e==null){

                    //Add the comment to the post's comments and the recyclerview
                    mCurrentPost.addComment(comment);
                    mAdapter.add(0,comment);

                    //Close the textinput and keyboard
                    clearFocus();
                } else {
                    e.printStackTrace();
                }
            });
        });
    }

    /*
        Clears the text input text and focus and closes the keyboard

        @param none

        @return void
     */
    private void clearFocus() {
        //Clear the text input's text and focus
        app.etComment.setText("");
        app.etComment.clearFocus();

        //Close the keyboard
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(app.tilComment.getWindowToken(), 0);
    }
}