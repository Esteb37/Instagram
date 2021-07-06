package com.example.instagram.adapters;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.databinding.ItemPostBinding;
import com.example.instagram.models.Post;
import com.parse.ParseFile;

class PostHolder extends RecyclerView.ViewHolder {

    ItemPostBinding app;
    Context context;

    public PostHolder(@NonNull View itemView, Context context, ItemPostBinding app) {
        super(itemView);
        this.context = context;
        this.app = app;

    }

    public void bind(Post post) {

        app.tvDescription.setText(post.getDescription());
        app.tvUsername.setText(post.getUser().getUsername());
        ParseFile image = post.getImage();
        if(image != null){
            Glide.with(context).load(image.getUrl()).into(app.ivContent);
        }
    }
}