package com.example.instagram.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.activities.DetailsActivity;
import com.example.instagram.adapters.posts.PostsAdapter;
import com.example.instagram.databinding.FragmentHomeBinding;
import com.example.instagram.models.Post;
import com.parse.ParseQuery;


import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/*
    Fragment for displaying
 */
public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";

    //View binder
    private FragmentHomeBinding app;

    //Current context
    private Context mContext;

    //Adapter for the timeline recyclerview
    private PostsAdapter mAdapter;

    //List of posts fromt the timeline
    private List<Post> mPosts;

    //Current page the user is at in the timeline
    private int mPage = 0;

    // Required empty public constructor
    public HomeFragment() { }

    /*
       Loads the last saved instance of the fragment

       @param savedInstanceState - The last saved instance
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
        Inflates the fragment with the layout
     */
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        app = FragmentHomeBinding.inflate(inflater, container, false);
        return app.getRoot();
    }

    /*
        Sets up the fragment's methods.
     */
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = view.getContext();

        setupRefreshListener();

        setupRecyclerView();

        queryPosts(0);


    }

    /*
        Sets up the post timeline recyclerview
     */
    private void setupRecyclerView(){

        //Create a listener for a click on the post
        PostsAdapter.OnClickListener clickListener = position -> {

            //Open the post in the details activity
            Intent i = new Intent(mContext, DetailsActivity.class);
            i.putExtra("post", Parcels.wrap(mPosts.get(position)));
            startActivity(i);
        };

        //Create a listener for scrolling
        PostsAdapter.OnScrollListener scrollListener = position -> {

            //If the user has arriveda t the end, load more posts
            if (position >= mPosts.size() - 1) {
                queryPosts(++mPage);
                Log.d(TAG,"Loading mPage:"+mPage);
            }
        };

        //Setup the posts list
        mPosts = new ArrayList<>();

        //Create the adapter
        mAdapter = new PostsAdapter(mContext, mPosts, clickListener, scrollListener);

        //Set the adapter on the recycler view
        app.rvPosts.setAdapter(mAdapter);

        //Set the layout manager on the recycler view
        app.rvPosts.setLayoutManager(new LinearLayoutManager(mContext));
    }

    /*
        Sets up a listener for the "swipe down to refresh" action
     */
    private void setupRefreshListener(){
        app.swipeContainer.setOnRefreshListener(() -> {

            //Refresh the timeline
            mPage = 0;
            queryPosts(mPage);
        });
    }

    //Get the timeline's posts
    private void queryPosts(int mPage) {

        //Specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        //Include data referred by user key
        query.include("user");

        //Limit query to latest 10 items
        final int limit = 10;
        query.setLimit(limit);

        //Get the selected page fromt the timeline
        query.setSkip(limit*mPage);

        //Order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");

        //Start an asynchronous call for posts
        query.findInBackground((posts, e) -> {

            //Check for errors
            if (e==null) {
                if (mPage == 0) {
                    mAdapter.clear();
                }
                mAdapter.addAll(posts);

            } else {
                Log.e(TAG, "Issue with getting posts", e);
            }
            app.swipeContainer.setRefreshing(false);
        });
    }

}