package com.example.instagram.adapters.comments;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagram.databinding.ItemCommentBinding;
import com.example.instagram.models.Comment;
import com.example.instagram.models.User;
import com.parse.ParseFile;
import com.parse.ParseQuery;

/*
    ViewHolder class for the comments recyclerview
 */
public class CommentHolder extends RecyclerView.ViewHolder{

    public static final String TAG = "CommentHolder";

    private final ItemCommentBinding app;
    private final Context mContext;

    /*
        Constructor from a Comment View, a Context and a ViewBinding binder

        @param View itemView - The comment view to be displayed
        @param Context context - The context on which this view is being displayed
        @param ItemCommentBinding binder - The ViewBinding binder

        @return none
     */
    public CommentHolder(View itemView, Context context, ItemCommentBinding binder) {
        super(itemView);
        app = binder;
        mContext = context;
    }

    /*
        Loads the comment's details into the comment view

        @param Comment comment - The comment to be displayed

        @return void
     */
    public void bind(Comment comment) {

        //Load the comment's text details
        app.tvContent.setText(comment.getContent());
        app.tvTimestamp.setText(Comment.calculateTimeAgo(comment.getCreatedAt()));

        //Get a query for users
        ParseQuery<User> query = ParseQuery.getQuery(User.class);

        //Find the publishing user's information in the query
        query.getInBackground(comment.getUser().getObjectId(),(user, e )->{

            //Load the user's username
            app.tvUsername.setText(user.getUsername());

            //Load the user's profile picture with rounded corners
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
