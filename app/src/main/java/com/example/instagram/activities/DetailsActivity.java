package com.example.instagram.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Objects;

public class DetailsActivity extends AppCompatActivity {

    ItemPostBinding app;
    Post mPost;
    User mCurrentUser;
    public static final String TAG = "DetailsActivity";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_post);

        setupLayout();

        mPost = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        mCurrentUser = User.getCurrentUser();

        loadDetails();

        loadUserDetails();

        setLikeListener();

        setCommentListener();

    }

    @SuppressLint("DefaultLocale")
    private void showLikes(Post post) throws ParseException {
        if(post.getLikes()>0){
            app.tvUserLike.setText(post.getUserLike().fetchIfNeeded().getUsername());
            app.clLikedBy.setVisibility(View.VISIBLE);
            if(post.getLikes()>1){
                app.tvLikes.setText(String.format("%d others.", post.getLikes() - 1));
                app.clLikedOthers.setVisibility(View.VISIBLE);
            } else {
                app.clLikedOthers.setVisibility(View.GONE);
            }
        }
        else{
            app.clLikedBy.setVisibility(View.GONE);
        }
    }


    @SuppressLint("SetTextI18n")
    private void setupLayout(){
        app = ItemPostBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_detail);
        ((TextView) findViewById(R.id.tvTitle)).setText("Post");
    }

    private void setLikeListener(){
        app.btnLike.setOnClickListener(v -> {
            if(mPost.isLikedByUser(mCurrentUser)) {
                app.btnLike.setImageResource(R.drawable.ufi_heart);
                mCurrentUser.unlikePost(mPost);
            } else {
                app.btnLike.setImageResource(R.drawable.ufi_heart_active);
                mCurrentUser.likePost(mPost);
            }
            try {
                showLikes(mPost);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        });
    }

    private void setCommentListener(){
        app.btnComment.setOnClickListener(v -> {
            Intent i = new Intent(DetailsActivity.this, CommentsActivity.class);
            i.putExtra("post",Parcels.wrap(mPost));
            startActivity(i);
        });
    }

    private void loadDetails(){
        app.tvDescription.setText(mPost.getDescription());
        app.tvUsername.setText(mPost.getUser().getUsername());
        app.tvUsername2.setText(mPost.getUser().getUsername());
        app.tvTimestamp.setText(Post.calculateTimeAgo(mPost.getCreatedAt()));

        try {
            showLikes(mPost);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseFile image = mPost.getImage();
        if(image != null){
            Glide.with(DetailsActivity.this)
                    .load(image.getUrl())
                    .into(app.ivContent);
        }

        ParseFile profilePicture = mPost.getProfilePicture();
        if(profilePicture != null) {
            Glide.with(DetailsActivity.this)
                    .load(profilePicture.getUrl())
                    .transform(new RoundedCorners(100), new CenterCrop())
                    .into(app.ivProfilePicture);
        }
    }

    private void loadUserDetails() {
        ParseFile currentProfile = mCurrentUser.getProfilePicture();
        if(currentProfile != null){
            Glide.with(DetailsActivity.this)
                    .load(currentProfile.getUrl())
                    .transform(new RoundedCorners(100),new CenterCrop())
                    .into(app.ivProfilePicture2);
        }
    }
}