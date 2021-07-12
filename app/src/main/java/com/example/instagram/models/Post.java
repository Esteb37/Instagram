package com.example.instagram.models;

import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
/*
    Class for handling Instagram Post objects
 */
@ParseClassName("Post")
public class Post extends ParseObject implements Parcelable {

    public static final String TAG = "PostClass";

    //Database keys
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_USER_LIKES = "userLikes";
    private static final String KEY_COMMENTS = "comments";
    private static final String KEY_LIKES = "likes";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_USER = "user";

    //Description getter
    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    //Description setter
    public void setDescription(String description){
        put(KEY_DESCRIPTION,description);
    }

    //Image getter
    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    //Image setter
    public void setImage(ParseFile image){
        put(KEY_IMAGE,image);
    }

    //User getter
    public User getUser(){
        return User.fromParseUser(getParseUser(KEY_USER));
    }

    //User setter
    public void setUser(User user){
        put(KEY_USER,user);
    }

    //Likes getter
    public int getLikes(){
        return getInt(KEY_LIKES);
    }

    //Likes setter
    public void setLikes(int likes){
        put(KEY_LIKES,likes);
        update();
    }

    //List of users that have liked getter
    public List<User> getUserLikes(){
        return getList(KEY_USER_LIKES);
    }

    //List of users that have liked setter
    public void setUserLikes(List<User> userLikes){
        put(KEY_USER_LIKES,userLikes);
        update();
    }

    /*
        Increases the like count and adds the user to the post's user likes

        @param user - The user that liked the post
     */
    public void addLike(User user){

        //Add the user to the post's user likes
        List<User> userLikes = getUserLikes();
        if(userLikes==null){
            userLikes = new ArrayList<>();
        }
        userLikes.add(user);
        setUserLikes(userLikes);

        //Increase the like count
        setLikes(getLikes()+1);
    }

    /*
        Decreases the like count and removes the user from the post's user likes

        @param user - The user that unliked the post
     */
    public void removeLike(User user){

        //Remove the user from the post's user likes
        List<User> userLikes = getUserLikes();
        assert userLikes != null;
        userLikes.remove(user);
        setUserLikes(userLikes);

        //Decrease the like count
        setLikes(getLikes()-1);
    }

    //Comments list getter
    public List<Comment> getComments(){
        return getList(KEY_COMMENTS);
    }

    //Comments list setter
    public void setComments(List<Comment> comments){
        put(KEY_COMMENTS,comments);
        update();
    }

    /*
        Adds a comment to the post's list of comments.

        @param comment - The comment to add
     */
    public void addComment(Comment comment){
        List<Comment> comments = getComments();
        if(comments==null){
            comments = new ArrayList<>();
        }
        comments.add(comment);
        setComments(comments);
    }

    //Updates the Post in the database
    public void update(){
        this.saveInBackground(e -> {
            if(e == null){
                Log.d(TAG,"Post updated");
            } else {
                e.printStackTrace();
            }
        });
    }

    /*
        Determines if this post has been liked by the user

        @param user - The user that may have liked the post
     */
    public boolean isLikedByUser(User user){
        List<String> likes = user.getLikes();
        return Objects.requireNonNull(likes).contains(getObjectId());
    }

    /*
        Creates a custom relative time tag for the timestamp

        @param createdAt - The datetime of this pots's creation.
     */
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
