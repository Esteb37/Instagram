package com.example.instagram.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagram.databinding.ItemPostBinding;
import com.example.instagram.fragments.HomeFragment;
import com.example.instagram.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

class PostHolder extends RecyclerView.ViewHolder {

    ItemPostBinding app;
    Context context;

    public PostHolder(@NonNull View itemView, Context context, ItemPostBinding app, PostsAdapter.OnClickListener clickListener) {
        super(itemView);
        this.context = context;
        this.app = app;

        itemView.setOnClickListener(v -> clickListener.onItemClicked(getAdapterPosition()));

    }

    public void bind(Post post) {
        app.tvDescription.setText(post.getDescription());
        app.tvUsername.setText(post.getUser().getUsername());
        app.tvUsername2.setText(post.getUser().getUsername());
        app.tvTimestamp.setText(Post.calculateTimeAgo(post.getCreatedAt()));
        ParseFile image = post.getImage();
        if(image != null){
            Glide.with(context)
                    .load(image.getUrl())
                    .into(app.ivContent);
        }

        ParseFile profilePicture = post.getProfilePicture();
        if(profilePicture != null) {
            Glide.with(context)
                    .load(profilePicture.getUrl())
                    .transform(new RoundedCorners(100), new CenterCrop())
                    .into(app.ivProfilePicture);
        }
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseFile currentProfile = currentUser.getParseFile("profilePicture");
        if(currentProfile != null){
            Glide.with(context)
                    .load(currentProfile.getUrl())
                    .transform(new RoundedCorners(100),new CenterCrop())
                    .into(app.ivProfilePicture2);
        }
    }
}