package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.databinding.ActivityDetailsBinding;
import com.example.instagram.models.Post;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {

    ActivityDetailsBinding app;
    Post mPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        app = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        mPost = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        app.tvDescription.setText(mPost.getDescription());
        app.tvUsername.setText(mPost.getUser().getUsername());
        String timeAgo = Post.calculateTimeAgo(mPost.getCreatedAt());
        app.tvTimestamp.setText(timeAgo);
        ParseFile image = mPost.getImage();
        if(image != null){
            Glide.with(this).load(image.getUrl()).into(app.ivContent);
        }

    }
}