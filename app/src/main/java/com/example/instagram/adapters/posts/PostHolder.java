package com.example.instagram.adapters.posts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagram.R;
import com.example.instagram.databinding.ItemPostBinding;
import com.example.instagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

class PostHolder extends RecyclerView.ViewHolder {

    ItemPostBinding app;
    Context mContext;
    Post mPost;
    ParseUser mCurrentUser;

    public static final String TAG = "PostHolder";

    public PostHolder(@NonNull View itemView, Context context, ItemPostBinding binder, PostsAdapter.OnClickListener clickListener) {
        super(itemView);
        mContext = context;
        app = binder;

        itemView.setOnClickListener(v -> clickListener.onItemClicked(getAdapterPosition()));

    }

    public void bind(Post post) throws ParseException {
        mPost = post;
        app.tvDescription.setText(post.getDescription());
        app.tvUsername.setText(post.getUser().getUsername());
        app.tvUsername2.setText(post.getUser().getUsername());
        app.tvTimestamp.setText(Post.calculateTimeAgo(post.getCreatedAt()));

        showLikes(post);

        ParseFile image = post.getImage();
        if(image != null){
            Glide.with(mContext)
                    .load(image.getUrl())
                    .into(app.ivContent);
        }

        ParseFile profilePicture = post.getProfilePicture();
        if(profilePicture != null) {
            Glide.with(mContext)
                    .load(profilePicture.getUrl())
                    .transform(new RoundedCorners(100), new CenterCrop())
                    .into(app.ivProfilePicture);
        }
        mCurrentUser = ParseUser.getCurrentUser();

        ParseFile currentProfile = mCurrentUser.getParseFile("profilePicture");
        if(currentProfile != null){
            Glide.with(mContext)
                    .load(currentProfile.getUrl())
                    .transform(new RoundedCorners(100),new CenterCrop())
                    .into(app.ivProfilePicture2);
        }






        app.btnLike.setOnClickListener(v -> {
            if(post.isLikedByUser(mCurrentUser)) {
                app.btnLike.setImageResource(R.drawable.ufi_heart);
                unlikePost(post);
            } else {
                app.btnLike.setImageResource(R.drawable.ufi_heart_active);

                likePost(post);
            }
            try {
                showLikes(post);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        });
    }

    @SuppressLint("DefaultLocale")
    private void showLikes(Post post) throws ParseException {
        if(post.getLikes()>0){
            app.tvUserLike.setText(post.getUserLike().fetchIfNeeded().getUsername());
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


    public void update(){
        mCurrentUser.saveInBackground(e -> {
            if(e == null){
                Log.d(TAG,"User updated");
            } else {
                e.printStackTrace();
            }
        });
    }

    public void likePost(Post post){
        List<Post> likedPosts = mCurrentUser.getList("likes");
        assert likedPosts != null;
        likedPosts.add(post);
        mCurrentUser.put("likes",likedPosts);
        post.addLike(mCurrentUser);
        post.update();
        update();

    }

    public void unlikePost(Post post){
        List<Post> likedPosts = mCurrentUser.getList("likes");
        assert likedPosts != null;
        likedPosts.remove(post);
        mCurrentUser.put("likes",likedPosts);
        post.removeLike(mCurrentUser);
        post.update();
        update();

    }

}