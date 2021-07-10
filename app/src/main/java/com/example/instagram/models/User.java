package com.example.instagram.models;

import android.graphics.Bitmap;
import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;


public class User extends ParseUser implements Parcelable {

    private static final String KEY_LIKES = "likes";
    private static final String KEY_FOLLOWING = "following";
    private static final String KEY_FOLLOWERS = "followers";
    private static final String KEY_BIO = "bio";
    private static final String KEY_NAME = "name";
    private static final String KEY_POSTS = "posts";
    private static final String KEY_PROFILE_PICTURE = "profilePicture";
    private static final String TAG = "User";

    public static User fromParseUser(ParseUser user){
        return (User) user;
    }

    public static User getCurrentUser(){
        return fromParseUser(ParseUser.getCurrentUser());
    }

    public List<String> getLikes() {
        return getList(KEY_LIKES);
    }

    public void setLikes(List<String> likes){
        put(KEY_LIKES,likes);
        update();
    }

    public void addLike(Post post){
        List<String> likes = getLikes();

        likes.add(post.getObjectId());
        setLikes(likes);
    }

    public void removeLike(Post post){
        List<String> likes = getLikes();
        likes.remove(post.getObjectId());
        setLikes(likes);
    }

    public void likePost(Post post){
        addLike(post);
        post.addLike(this);
        post.update();
    }

    public void unlikePost(Post post){
        removeLike(post);
        post.removeLike(this);
        post.update();
    }

    public int getFollowing() {
        return getInt(KEY_FOLLOWING);
    }

    public int getFollowers() {
        return getInt(KEY_FOLLOWERS);
    }

    public String getBio() {
        return getString(KEY_BIO);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public List<Post> getPosts() {
        return getList(KEY_POSTS);
    }

    public void setPosts(List<Post> posts){
        put(KEY_POSTS,posts);
        update();
    }

    public void addPost(Post post){
        List<Post> posts = getPosts();
        posts.add(post);
        setPosts(posts);
    }

    public ParseFile getProfilePicture() {
        return getParseFile(KEY_PROFILE_PICTURE);
    }

    public void setProfilePicture(ParseFile picture){
        put(KEY_PROFILE_PICTURE, picture);
    }

    private void update() {
        this.saveInBackground(e -> {
            if(e == null){
                Log.d(TAG,"User updated");
            } else {
                e.printStackTrace();
            }
        });
    }

}
