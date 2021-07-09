package com.example.instagram.adapters.comments;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagram.databinding.ItemCommentBinding;
import com.example.instagram.models.Comment;
import com.example.instagram.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class CommentHolder extends RecyclerView.ViewHolder{
    private final Context mContext;
    private final ItemCommentBinding app;
    public static final String TAG = "CommentHolder";


    public CommentHolder(View itemView, Context context, ItemCommentBinding binder) {
        super(itemView);
        app = binder;
        mContext = context;


    }

    public void bind(Comment comment) {


        app.tvContent.setText(comment.getContent());
        app.tvTimestamp.setText(Comment.calculateTimeAgo(comment.getCreatedAt()));



        ParseQuery<User> query = ParseQuery.getQuery(User.class);

        query.getInBackground(comment.getUser().getObjectId(),(user, e )->{
            app.tvUsername.setText(user.getUsername());
            ParseFile profilePicture = user.getProfilePicture();
            if (profilePicture != null) {
                Glide.with(mContext)
                        .load(profilePicture.getUrl())
                        .transform(new RoundedCorners(100), new CenterCrop())
                        .into(app.ivProfilePicture);

            }
        });


    }
}
