package com.example.instagram.adapters.posts;

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
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.Locale;

class PostHolder extends RecyclerView.ViewHolder {

    public static final String TAG = "PostHolder";

    private final ItemPostBinding app;
    private final Context mContext;

    private final User mCurrentUser;
    private Post mPost;

    public PostHolder(@NonNull View itemView, Context context, ItemPostBinding binder, PostsAdapter.OnClickListener clickListener) {
        super(itemView);
        mContext = context;
        app = binder;
        mCurrentUser = User.getCurrentUser();
        itemView.setOnClickListener(v -> clickListener.onItemClicked(getAdapterPosition()));
    }

    public void bind(Post post){
        mPost = post;

        loadPostDetails();

        loadUserProfile();

        setLikeButtonListener();

        setCommentButtonListener();

        setProfilePictureListener();

    }

    private void loadUserProfile(){
        ParseFile currentProfile = mCurrentUser.getProfilePicture();
        if(currentProfile != null){
            Glide.with(mContext)
                    .load(currentProfile.getUrl())
                    .transform(new RoundedCorners(100),new CenterCrop())
                    .into(app.ivProfilePicture2);
        }
    }

    private void loadPostDetails(){

        app.tvDescription.setText(mPost.getDescription());
        app.tvUsername.setText(mPost.getUser().getUsername());
        app.tvUsername2.setText(mPost.getUser().getUsername());
        app.tvTimestamp.setText(Post.calculateTimeAgo(mPost.getCreatedAt()));

        showLikes(mPost);

        app.btnLike.setImageResource(mPost.isLikedByUser(mCurrentUser) ? R.drawable.ufi_heart_active: R.drawable.ufi_heart);

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

    private void setProfilePictureListener(){
        app.ivProfilePicture.setOnClickListener(v -> {
            FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
            ProfileFragment fragment = new ProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("user", mPost.getUser());
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit();
        });
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
            mContext.startActivity(i);
        });
    }

    private void showLikes(Post post) {
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