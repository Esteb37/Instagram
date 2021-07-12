package com.example.instagram.models;

import android.os.Parcelable;
import android.util.Log;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

/*
    Custom user class for handling custom ParseUser methods.
 */
public class User extends ParseUser implements Parcelable {

    public static final String TAG = "User";

    //Database keys
    private static final String KEY_PROFILE_PICTURE = "profilePicture";
    private static final String KEY_FOLLOWING = "following";
    private static final String KEY_FOLLOWERS = "followers";
    private static final String KEY_POSTS = "posts";
    private static final String KEY_LIKES = "likes";
    private static final String KEY_NAME = "name";
    private static final String KEY_BIO = "bio";

    /*
        Casts a ParseUser into a User object

        @param ParseUser - The user to cast

        @return User - The custom user object
     */
    public static User fromParseUser(ParseUser user){
        return (User) user;
    }

    //Current user getter
    public static User getCurrentUser(){
        return fromParseUser(ParseUser.getCurrentUser());
    }

    //Following getter
    public int getFollowing() {
        return getInt(KEY_FOLLOWING);
    }

    //Followers getter
    public int getFollowers() {
        return getInt(KEY_FOLLOWERS);
    }

    //Bio getter
    public String getBio() {
        return getString(KEY_BIO);
    }

    //Name getter
    public String getName() {
        return getString(KEY_NAME);
    }

    //Profile picture getter
    public ParseFile getProfilePicture() {
        return getParseFile(KEY_PROFILE_PICTURE);
    }

    //Profile picture setter
    public void setProfilePicture(ParseFile picture){
        put(KEY_PROFILE_PICTURE, picture);
    }

    //User's posts getter
    public List<Post> getPosts() {
        return getList(KEY_POSTS);
    }

    //User's posts setter
    public void setPosts(List<Post> posts){
        put(KEY_POSTS,posts);
        update();
    }

    /*
        Adds a post to the user's list of posts

        @param post - The post to add
     */
    public void addPost(Post post){
        List<Post> posts = getPosts();
        posts.add(post);
        setPosts(posts);
    }

    //Liked posts getter
    public List<String> getLikes() {
        return getList(KEY_LIKES);
    }

    //Liked posts setter
    public void setLikes(List<String> likes){
        put(KEY_LIKES,likes);
        update();
    }

    /*
        Adds a post to the user's liked posts list

        @param post - The post to add
     */
    public void addLike(Post post){
        List<String> likes = getLikes();
        likes.add(post.getObjectId());
        setLikes(likes);
    }

    /*
        Removes a post from the user's liked post list

        @param post - The post to remove
     */
    public void removeLike(Post post){
        List<String> likes = getLikes();
        likes.remove(post.getObjectId());
        setLikes(likes);
    }

    /*
        Likes a post

        @param post - The post to like
     */
    public void likePost(Post post){
        addLike(post);
        post.addLike(this);
        post.update();
    }

    /*
        Removes like from post

        @param post - The post to unlike
     */
    public void unlikePost(Post post){
        removeLike(post);
        post.removeLike(this);
        post.update();
    }

    /*
        Updates the user in the database
     */
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
