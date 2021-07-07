package com.example.instagram.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.R;
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
    protected PostsAdapter adapter;
    protected List<Post> allPosts;
    RecyclerView rvPosts;

    int page = 0;

    Context context;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        app = FragmentHomeBinding.inflate(getLayoutInflater());
        context = view.getContext();

        app.swipeContainer.setOnRefreshListener(() -> {
            page = 0;
            queryPosts(page);
        });

        PostsAdapter.OnClickListener clickListener = position -> {
            Intent i = new Intent(context, DetailsActivity.class);
            i.putExtra("post", Parcels.wrap(allPosts.get(position)));
            startActivity(i);
        };

        PostsAdapter.OnScrollListener scrollListener = position -> {
            Log.i(TAG, String.valueOf(position));
            if (position >= allPosts.size() - 1) {
                queryPosts(++page);
            }
        };

        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(context, allPosts, clickListener, scrollListener);

        rvPosts = view.findViewById(R.id.rvPosts);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(manager);

        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);

        // query posts from Parstagram
        queryPosts(0);


    }

    private void queryPosts(int page) {

        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        // limit query to latest 20 items

        final int limit = 20;
        query.setLimit(limit);

        query.setSkip(limit*page);

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

            if(page==0){
                adapter.clear();
            }

            // ...the data has come back, add new items to your adapter...
            adapter.addAll(posts);

            app.swipeContainer.setRefreshing(false);

        });
    }

}