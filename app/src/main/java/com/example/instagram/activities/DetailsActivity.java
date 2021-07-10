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

public class DetailsActivity extends AppCompatActivity {

    public static final String TAG = "DetailsActivity";

    private ItemPostBinding app;
    private Context mContext;

    private Post mPost;
    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = DetailsActivity.this;

        mPost = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        mCurrentUser = User.getCurrentUser();

        setupLayout();

        loadPostDetails();

        loadUserDetails();

        setLikeButtonListener();

        setCommentButtonListener();
    }

    private void setupLayout(){
        setContentView(R.layout.item_post);

        app = ItemPostBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_detail);
        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.post);
    }

    private void loadPostDetails(){
        app.tvDescription.setText(mPost.getDescription());
        app.tvUsername.setText(mPost.getUser().getUsername());
        app.tvUsername2.setText(mPost.getUser().getUsername());
        app.tvTimestamp.setText(Post.calculateTimeAgo(mPost.getCreatedAt()));

        app.btnLike.setImageResource(mPost.isLikedByUser(mCurrentUser) ? R.drawable.ufi_heart_active: R.drawable.ufi_heart);

        showLikes(mPost);

        ParseFile image = mPost.getImage();
        if(image != null){
            Glide.with(mContext)
                    .load(image.getUrl())
                    .into(app.ivContent);
        }

        ParseFile profilePicture = mPost.getUser().getProfilePicture();
        if(profilePicture != null) {
            Glide.with(mContext)
                    .load(profilePicture.getUrl())
                    .transform(new RoundedCorners(100), new CenterCrop())
                    .into(app.ivProfilePicture);
        }
    }

    private void loadUserDetails() {
        ParseFile currentProfile = mCurrentUser.getProfilePicture();
        if(currentProfile != null){
            Glide.with(mContext)
                    .load(currentProfile.getUrl())
                    .transform(new RoundedCorners(100),new CenterCrop())
                    .into(app.ivProfilePicture2);
        }
    }

    private void setLikeButtonListener(){
        app.btnLike.setOnClickListener(v -> {
            if(mPost.isLikedByUser(mCurrentUser)) {
                app.btnLike.setImageResource(R.drawable.ufi_heart);
                mCurrentUser.unlikePost(mPost);
            } else {
                app.btnLike.setImageResource(R.drawable.ufi_heart_active);
                mCurrentUser.likePost(mPost);
            }
            showLikes(mPost);
        });
    }

    private void setCommentButtonListener(){
        app.btnComment.setOnClickListener(v -> {
            Intent i = new Intent(mContext, CommentsActivity.class);
            i.putExtra("post",Parcels.wrap(mPost));
            startActivity(i);
        });
    }

    private void showLikes(Post post){
        if(post.getLikes()>0){
            app.tvUserLike.setText(post.getUserLikes().get(0).getUsername());
            app.clLikedBy.setVisibility(View.VISIBLE);
            if(post.getLikes()>1){
                app.tvLikes.setText(String.format(Locale.US,"%d others.", post.getLikes() - 1));
                app.clLikedOthers.setVisibility(View.VISIBLE);
            } else {
                app.clLikedOthers.setVisibility(View.GONE);
            }
        } else{
            app.clLikedBy.setVisibility(View.GONE);
        }
    }
}