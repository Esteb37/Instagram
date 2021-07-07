package com.example.instagram.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.instagram.R;
import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.databinding.ActivityMainBinding;
import com.example.instagram.models.Post;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    ActivityMainBinding app;
    protected PostsAdapter adapter;
    protected List<Post> allPosts;

    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = ActivityMainBinding.inflate(getLayoutInflater());
        View view = app.getRoot();
        setContentView(view);

        app.btnPost.setOnClickListener(v -> {
            Intent i = new Intent(this,PostActivity.class);
            startActivity(i);
        });

        app.swipeContainer.setOnRefreshListener(() -> {
            page = 0;
            queryPosts(page);
        });

        PostsAdapter.OnClickListener clickListener = position -> {
            Intent i = new Intent(MainActivity.this,DetailsActivity.class);
            i.putExtra("post", Parcels.wrap(allPosts.get(position)));
            startActivity(i);
        };

        PostsAdapter.OnScrollListener scrollListener = position ->{
            Log.i(TAG, String.valueOf(position));
            if(position>=allPosts.size()-1){
                queryPosts(++page);
            }
        };

        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(this, allPosts,clickListener,scrollListener);

        // set the adapter on the recycler view
        app.rvPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        app.rvPosts.setLayoutManager(new LinearLayoutManager(this));
        // query posts from Parstagram
        queryPosts(0);


        app.btnLogout.setOnClickListener(v->{
            ParseUser.logOut();
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
        });

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