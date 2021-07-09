package com.example.instagram.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.parse.GetCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommentsActivity extends AppCompatActivity {

    public static final String TAG = "CommentsActivity";

    User mCurrentUser;
    Post mCurrentPost;

    List<Comment> mComments;
    CommentsAdapter mAdapter;

    ActivityCommentsBinding app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        mCurrentUser = User.getCurrentUser();

        mCurrentPost = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        setupLayout();

        setupRecyclerView();

        setPostListener();

        loadProfilePicture();

        loadComments();
    }

    private void loadComments(){

        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class).whereContains("post",mCurrentPost.getObjectId());

        query.addDescendingOrder("createdAt");

        query.findInBackground((comments, e) ->{
            mAdapter.addAll(comments);
        });
    }

    private void loadProfilePicture(){
        ParseFile profilePicture = mCurrentUser.getProfilePicture();
        assert profilePicture != null;
        Glide.with(CommentsActivity.this)
                .load(profilePicture.getUrl())
                .transform(new RoundedCorners(100), new CenterCrop())
                .into(app.ivProfilePicture);
    }

    private void setPostListener(){
        app.btnPost.setOnClickListener(v -> {
            Comment comment = (Comment) ParseObject.create("Comment");
            comment.setContent(Objects.requireNonNull(app.etComment.getText()).toString());
            comment.setUser(mCurrentUser);
            comment.setPost(mCurrentPost);

            comment.saveInBackground(e -> {
                if(e==null){
                    mCurrentPost.addComment(comment);

                    mComments.add(0,comment);
                    mAdapter.notifyItemInserted(0);
                    app.etComment.setText("");
                    app.etComment.clearFocus();
                    InputMethodManager imm = (InputMethodManager) CommentsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(app.tilComment.getWindowToken(), 0);
                } else {
                    e.printStackTrace();
                }
            });
        });
    }

    private void setupRecyclerView(){
        mComments = new ArrayList<>();
        mAdapter = new CommentsAdapter(CommentsActivity.this,mComments);

        app.rvComments.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));
        app.rvComments.setAdapter(mAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void setupLayout(){
        app = ActivityCommentsBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_detail);
        ((TextView) findViewById(R.id.tvTitle)).setText("Comments");
    }
}