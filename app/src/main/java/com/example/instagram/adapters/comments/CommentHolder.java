package com.example.instagram.adapters.comments;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagram.databinding.ItemCommentBinding;
import com.example.instagram.models.Comment;
import com.parse.ParseFile;


public class CommentHolder extends RecyclerView.ViewHolder{
    private final Context mContext;
    private final ItemCommentBinding app;


    public CommentHolder(View itemView, Context context, ItemCommentBinding binder) {
        super(itemView);
        mContext = context;
        app = binder;

    }


    public void bind(Comment comment) {

        app.tvContent.setText(comment.getContent());
        app.tvTimestamp.setText(Comment.calculateTimeAgo(comment.getCreatedAt()));
        app.tvUsername.setText(comment.getUser().getUsername());


        ParseFile profilePicture = comment.getProfilePicture();
        if(profilePicture != null) {
            Glide.with(mContext)
                    .load(profilePicture.getUrl())
                    .transform(new RoundedCorners(100), new CenterCrop())
                    .into(app.ivProfilePicture);
        }

    }


}
