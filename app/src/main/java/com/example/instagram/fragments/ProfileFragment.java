package com.example.instagram.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagram.activities.DetailsActivity;
import com.example.instagram.adapters.images.ImageAdapter;
import com.example.instagram.databinding.FragmentHomeBinding;
import com.example.instagram.databinding.FragmentProfileBinding;
import com.example.instagram.models.Post;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    FragmentProfileBinding app;
    protected ImageAdapter mAdapter;
    protected List<Post> mPosts;
    ParseUser mUser;

    int mPage = 0;

    Context mContext;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        app = FragmentProfileBinding.inflate(inflater, container, false);
        mUser = getArguments().getParcelable("user");
        return app.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = view.getContext();


        /*ImageAdapter.OnClickListener clickListener = position -> {
            Intent i = new Intent(mContext, DetailsActivity.class);
            i.putExtra("post", Parcels.wrap(mPosts.get(position)));
            startActivity(i);
        };

        ImageAdapter.OnScrollListener scrollListener = position -> {
            if (position >= mPosts.size() - 1) {

                queryPosts(++mPage);
                Log.d(TAG,"Loading mPage:"+mPage);
            }
        };*/

        mPosts = new ArrayList<>();
        mAdapter = new ImageAdapter(mContext, mPosts);

        GridLayoutManager manager = new GridLayoutManager(mContext,3);


        // set the mAdapter on the recycler view
        app.rvPosts.setAdapter(mAdapter);

        // set the layout manager on the recycler view
        app.rvPosts.setLayoutManager(manager);

        // query posts from Parstagram
        queryPosts(0);

        ParseFile profilePicture = mUser.getParseFile("profilePicture");
        if(profilePicture != null){
            Glide.with(mContext)
                    .load(profilePicture.getUrl())
                    .transform(new RoundedCorners(300))
                    .into(app.ivProfilePicture);
        }

        app.tvBio.setText(mUser.getString("bio"));
        app.tvName.setText(mUser.getString("name"));
        app.tvFollowers.setText(String.valueOf(mUser.getInt("followers")));
        app.tvFollowing.setText(String.valueOf(mUser.getInt("following")));

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

                return;
            }

            if(mPage==0){
                mAdapter.clear();
            }

            // ...the data has come back, add new items to your mAdapter...
            mAdapter.addAll(posts);



        });
    }

}