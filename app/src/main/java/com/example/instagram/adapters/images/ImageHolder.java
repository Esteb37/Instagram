package com.example.instagram.adapters.images;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.databinding.ItemImageBinding;
import com.example.instagram.models.Post;
import com.parse.ParseFile;

public class ImageHolder extends RecyclerView.ViewHolder{

    Context mContext;
    Post mPost;
    ItemImageBinding app;
    public ImageHolder(View view, Context context, ItemImageBinding binder) {
        super(view);

        app = binder;
        mContext = context;

    }

    public void bind(Post post) {
        mPost = post;

        ParseFile image = post.getImage();
        if(image != null){
            Glide.with(mContext)
                    .load(image.getUrl())
                    .into(app.ivImage);
        }

    }
}
