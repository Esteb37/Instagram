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


public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";

    private FragmentHomeBinding app;

    private Context mContext;

    private PostsAdapter mAdapter;
    private List<Post> mPosts;

    private int mPage = 0;


    public HomeFragment() {
        // Required empty public constructor
    }

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

        setupRefreshListener();

        setupRecyclerView();

        queryPosts(0);


    }

    private void setupRecyclerView(){

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

        // set the mAdapter on the recycler view
        app.rvPosts.setAdapter(mAdapter);

        // set the layout manager on the recycler view
        app.rvPosts.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void setupRefreshListener(){
        app.swipeContainer.setOnRefreshListener(() -> {
            mPage = 0;
            queryPosts(mPage);
        });
    }

    private void queryPosts(int mPage) {

        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include("user");
        // limit query to latest 10 items

        final int limit = 10;
        query.setLimit(limit);

        query.setSkip(limit*mPage);

        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground((posts, e) -> {
            // check for errors
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