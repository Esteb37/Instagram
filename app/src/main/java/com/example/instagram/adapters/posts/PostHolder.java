package com.example.instagram.adapters.posts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagram.R;
import com.example.instagram.activities.CommentsActivity;
import com.example.instagram.databinding.ItemPostBinding;
import com.example.instagram.fragments.ProfileFragment;
import com.example.instagram.models.Post;
import com.example.instagram.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

class PostHolder extends RecyclerView.ViewHolder {

    ItemPostBinding app;
    Context mContext;
    Post mPost;
    User mCurrentUser;

    public static final String TAG = "PostHolder";

    public PostHolder(@NonNull View itemView, Context context, ItemPostBinding binder, PostsAdapter.OnClickListener clickListener) {
        super(itemView);
        mContext = context;
        app = binder;

        mCurrentUser = User.fromParseUser(ParseUser.getCurrentUser());

        itemView.setOnClickListener(v -> clickListener.onItemClicked(getAdapterPosition()));

    }

    public void bind(Post post) throws ParseException {
        mPost = post;

        loadPostDetails();

        loadUserProfile();

        setLikeListener();

        setCommentListener();

        setProfileListener();

    }

    @SuppressLint("DefaultLocale")
    private void showLikes(Post post) {
        if(post.getLikes()>0){
            try{
                app.tvUserLike.setText(post.getUserLike().fetchIfNeeded().getUsername());
            }
            catch(Exception e){
                e.printStackTrace();
            }

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

    private void setProfileListener(){
        app.ivProfilePicture.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
            ProfileFragment fragment = new ProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", mPost.getUser());
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit();
        });
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
            showLikes(mPost);

        });

    }

    private void setCommentListener(){
        app.btnComment.setOnClickListener(v -> {
            Intent i = new Intent(mContext, CommentsActivity.class);
            i.putExtra("post",Parcels.wrap(mPost));
            mContext.startActivity(i);
        });
    }

    private void loadUserProfile(){
        ParseFile currentProfile = mCurrentUser.getParseFile("profilePicture");
        if(currentProfile != null){
            Glide.with(mContext)
                    .load(currentProfile.getUrl())
                    .transform(new RoundedCorners(100),new CenterCrop())
                    .into(app.ivProfilePicture2);
        }
    }

    private void loadPostDetails() throws ParseException {

        app.tvDescription.setText(mPost.getDescription());
        app.tvUsername.setText(mPost.getUser().getUsername());
        app.tvUsername2.setText(mPost.getUser().getUsername());
        app.tvTimestamp.setText(Post.calculateTimeAgo(mPost.getCreatedAt()));

        showLikes(mPost);

        ParseFile image = mPost.getImage();
        if(image != null){
            Glide.with(mContext)
                    .load(image.getUrl())
                    .into(app.ivContent);
        }

        ParseFile profilePicture = mPost.getProfilePicture();
        if(profilePicture != null) {
            Glide.with(mContext)
                    .load(profilePicture.getUrl())
                    .transform(new RoundedCorners(100), new CenterCrop())
                    .into(app.ivProfilePicture);
        }
    }
}