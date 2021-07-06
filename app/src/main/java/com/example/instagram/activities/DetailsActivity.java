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
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        app = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        post = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        app.tvDescription.setText(post.getDescription());
        app.tvUsername.setText(post.getUser().getUsername());
        String timeAgo = Post.calculateTimeAgo(post.getCreatedAt());
        app.tvTimestamp.setText(timeAgo);
        ParseFile image = post.getImage();
        if(image != null){
            Glide.with(this).load(image.getUrl()).into(app.ivContent);
        }

    }
}