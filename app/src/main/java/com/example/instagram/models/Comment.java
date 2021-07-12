package com.example.instagram.models;

import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

/*
    Class to handle a post's comments.
 */
@ParseClassName("Comment")
public class Comment extends ParseObject implements Parcelable {

    public static final String TAG = "CommentClass";

    //Database keys
    private static final String KEY_CONTENT = "content";
    private static final String KEY_USER = "user";
    private static final String KEY_POST = "post";

    //Content getter
    public String getContent(){
        return getString(KEY_CONTENT);
    }

    //Content setter
    public void setContent(String content){
        put(KEY_CONTENT,content);
    }

    //Post getter
    public Post getPost(){
        return (Post) getParseObject(KEY_POST);
    }

    //Post setter
    public void setPost(Post post){
        put(KEY_POST,post);
    }

    //User setter
    public void setUser(User user){
        put(KEY_USER,user);
    }

    //User getter
    public User getUser(){
        return User.fromParseUser(getParseUser(KEY_USER));
    }

    /*
        Creates a custom relative time tag for the timestamp

        @param createdAt - The datetime of this pots's creation.
     */
    public static String calculateTimeAgo(Date createdAt) {
        return Post.calculateTimeAgo(createdAt);
    }
}
