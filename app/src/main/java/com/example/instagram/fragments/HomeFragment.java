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
import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.databinding.FragmentHomeBinding;
import com.example.instagram.models.Post;
import com.parse.ParseQuery;


import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";

    FragmentHomeBinding app;
    protected PostsAdapter mAdapter;
    protected List<Post> mPosts;

    int mPage = 0;

    Context mContext;

    public HomeFragment() {
        // Required empty public constructor
    }


    /*public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        app = FragmentHomeBinding.inflate(inflater, container, false);
        return app.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = view.getContext();

        app.swipeContainer.setOnRefreshListener(() -> {
            mPage = 0;
            queryPosts(mPage);
        });

        PostsAdapter.OnClickListener clickListener = position -> {
            Intent i = new Intent(mContext, DetailsActivity.class);
            i.putExtra("post", Parcels.wrap(mPosts.get(position)));
            startActivity(i);
        };

        PostsAdapter.OnScrollListener scrollListener = position -> {
            if (position >= mPosts.size() - 1) {

                queryPosts(++mPage);
                Log.d(TAG,"Loading mPage:"+mPage);
            }
        };

        mPosts = new ArrayList<>();
        mAdapter = new PostsAdapter(mContext, mPosts, clickListener, scrollListener);

        LinearLayoutManager manager = new LinearLayoutManager(mContext);


        // set the mAdapter on the recycler view
        app.rvPosts.setAdapter(mAdapter);

        // set the layout manager on the recycler view
        app.rvPosts.setLayoutManager(manager);

        // query posts from Parstagram
        queryPosts(0);


    }


    private void queryPosts(int mPage) {

        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // limit query to latest 20 items

        final int limit = 2;
        query.setLimit(limit);

        query.setSkip(limit*mPage);

        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground((posts, e) -> {
            // check for errors
            if (e != null) {
                Log.e(TAG, "Issue with getting posts", e);
                app.swipeContainer.setRefreshing(false);
                return;
            }

            if(mPage==0){
                mAdapter.clear();
            }

            // ...the data has come back, add new items to your mAdapter...
            mAdapter.addAll(posts);

            app.swipeContainer.setRefreshing(false);

        });
    }

}