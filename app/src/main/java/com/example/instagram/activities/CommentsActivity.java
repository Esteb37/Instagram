package com.example.instagram.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagram.R;
import com.example.instagram.adapters.comments.CommentsAdapter;
import com.example.instagram.databinding.ActivityCommentsBinding;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommentsActivity extends AppCompatActivity {

    public static final String TAG = "CommentsActivity";

    ParseUser mCurrentUser;
    Post mCurrentPost;

    List<Comment> mComments;
    CommentsAdapter mAdapter;

    ActivityCommentsBinding app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        mCurrentUser = ParseUser.getCurrentUser();

        mCurrentPost = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        app = ActivityCommentsBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_detail);
        ((TextView) findViewById(R.id.tvTitle)).setText("Comments");

        mComments = new ArrayList<>();
        mAdapter = new CommentsAdapter(CommentsActivity.this,mComments);

        app.rvComments.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));
        app.rvComments.setAdapter(mAdapter);

        app.btnPost.setOnClickListener(v -> {
            Comment comment = (Comment) ParseObject.create("Comment");
            comment.setContent(Objects.requireNonNull(app.etComment.getText()).toString());
            comment.setUser(mCurrentUser);
            comment.setPost(mCurrentPost);

            comment.saveInBackground(e -> {
                if(e==null){
                    mCurrentPost.addComment(comment);
                } else {
                    e.printStackTrace();
                }
            });
        });

        ParseFile profilePicture = mCurrentUser.getParseFile("profilePicture");
        Glide.with(CommentsActivity.this)
            .load(profilePicture.getUrl())
            .transform(new RoundedCorners(100), new CenterCrop())
            .into(app.ivProfilePicture);
        loadComments();
    }

    void loadComments(){
        List<Comment> comments = mCurrentPost.getComments();
        if(comments!=null) {
            mAdapter.addAll(comments);
        }
    }
}