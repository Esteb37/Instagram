package com.example.instagram.models;

import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@ParseClassName("Post")
public class Post extends ParseObject implements Parcelable {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_PROFILE_PICTURE = "profilePicture";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_COMMENTS = "comments";

    public static final String TAG = "Post";

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION,description);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image){
        put(KEY_IMAGE,image);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER,user);
    }

    public ParseFile getProfilePicture(){
        return getUser().getParseFile(KEY_PROFILE_PICTURE);
    }

    public int getLikes(){
        return getInt(KEY_LIKES);
    }

    public void setLikes(int likes){
        put(KEY_LIKES,likes);
        update();
    }

    public List<Comment> getComments(){
        return getList(KEY_COMMENTS);
    }

    public void setComments(List<Comment> comments){
        put(KEY_COMMENTS,comments);
        update();
    }

    public void addComment(Comment comment){
        List<Comment> comments = getComments();
        comments.add(comment);
        setComments(comments);
    }
    public ParseUser getUserLike() {
        return (ParseUser) Objects.requireNonNull(getList("userLikes")).get(0);
    }

    public void addLike(ParseUser user){
        List<ParseUser> userLikes = getList("userLikes");
        assert userLikes != null;
        userLikes.add(user);
        put("userLikes",userLikes);
        setLikes(getLikes()+1);
    }

    public void removeLike(ParseUser user){
        List<ParseUser> userLikes = getList("userLikes");
        assert userLikes != null;
        userLikes.remove(user);
        put("userLikes",userLikes);
        setLikes(getLikes()-1);
    }

    public void update(){
        this.saveInBackground(e -> {
            if(e == null){
                Log.d(TAG,"Post updated");
            } else {
                e.printStackTrace();
            }
        });
    }

    public boolean isLikedByUser(ParseUser user){
        return Objects.requireNonNull(user.getList("likes")).contains(this);
    }
    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }
}
