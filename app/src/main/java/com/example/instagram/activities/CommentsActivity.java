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

public class CommentsActivity extends AppCompatActivity {

    public static final String TAG = "CommentsActivity";

    private ActivityCommentsBinding app;
    private Context mContext;

    private User mCurrentUser;
    private Post mCurrentPost;

    private CommentsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = CommentsActivity.this;
        
        mCurrentUser = User.getCurrentUser();

        mCurrentPost = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        setupLayout();

        setupRecyclerView();

        setPostButtonListener();

        loadProfilePicture();

        loadComments();
    }

    private void setupLayout(){
        setContentView(R.layout.activity_comments);

        app = ActivityCommentsBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_detail);
        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.comments);
    }

    private void setupRecyclerView(){
        List<Comment> comments = new ArrayList<>();
        mAdapter = new CommentsAdapter(mContext, comments);

        app.rvComments.setLayoutManager(new LinearLayoutManager(mContext));
        app.rvComments.setAdapter(mAdapter);
    }

    private void loadComments(){
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class).whereContains("post",mCurrentPost.getObjectId());

        query.addDescendingOrder("createdAt");

        query.findInBackground((comments, e) -> mAdapter.addAll(comments));
    }

    private void loadProfilePicture(){
        ParseFile profilePicture = mCurrentUser.getProfilePicture();
        assert profilePicture != null;
        Glide.with(mContext)
                .load(profilePicture.getUrl())
                .transform(new RoundedCorners(100), new CenterCrop())
                .into(app.ivProfilePicture);
    }

    private void setPostButtonListener(){
        app.btnPost.setOnClickListener(v -> {
            
            Comment comment = (Comment) ParseObject.create("Comment");
            comment.setContent(Objects.requireNonNull(app.etComment.getText()).toString());
            comment.setUser(mCurrentUser);
            comment.setPost(mCurrentPost);

            comment.saveInBackground(e -> {
                if(e==null){
                    mCurrentPost.addComment(comment);
                    mAdapter.add(0,comment);
                    clearFocus();
                } else {
                    e.printStackTrace();
                }
            });
        });
    }

    private void clearFocus() {
        app.etComment.setText("");
        app.etComment.clearFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(app.tilComment.getWindowToken(), 0);
    }
}