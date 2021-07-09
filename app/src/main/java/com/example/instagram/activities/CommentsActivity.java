package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.instagram.R;
import com.example.instagram.adapters.comments.CommentsAdapter;
import com.example.instagram.databinding.ActivityCommentsBinding;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.parse.ParseException;
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

        mComments = new ArrayList<>();
        mAdapter = new CommentsAdapter(CommentsActivity.this,mComments);

        app.btnPost.setOnClickListener(v -> {
            Comment comment = new Comment();
            comment.setContent(Objects.requireNonNull(app.etComment.getText()).toString());
            comment.setUser(mCurrentUser);
            comment.setPost(mCurrentPost);

            comment.saveInBackground(e -> {
                if(e==null){
                    Log.d(TAG,"Comment added");
                } else {
                    e.printStackTrace();
                }
            });
        });

        loadComments();
    }

    void loadComments(){
        mAdapter.addAll(mCurrentPost.getComments());
    }
}