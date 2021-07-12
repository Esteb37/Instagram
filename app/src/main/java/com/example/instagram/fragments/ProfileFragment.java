package com.example.instagram.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagram.adapters.images.ImageAdapter;
import com.example.instagram.databinding.FragmentProfileBinding;
import com.example.instagram.models.Post;
import com.example.instagram.models.User;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/*
    Fragment for displaying a user's profile
 */
public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    //View binder
    private FragmentProfileBinding app;

    //Current context
    private Context mContext;

    //User for which the profile is being displayed
    private User mUser;

    //User's posts
    private List<Post> mPosts;

    //Adapter for the user's posts recyclerview
    private ImageAdapter mAdapter;

    // Required empty public constructor
    public ProfileFragment() {}

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
        app = FragmentProfileBinding.inflate(inflater, container, false);
        return app.getRoot();
    }

    /*
        Sets up the fragment's methods.
     */
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = view.getContext();

        assert getArguments() != null;
        mUser = getArguments().getParcelable("user");

        setupRecyclerView();

        loadProfileDetails();

        queryPosts();
    }

    /*
        Sets up the User's posts recycler view
     */
    private void setupRecyclerView(){
        mPosts = new ArrayList<>();
        mAdapter = new ImageAdapter(mContext, mPosts);

        // Set the adapter on the recycler view
        app.rvPosts.setAdapter(mAdapter);

        // Set a grid layout manager on the recycler view
        app.rvPosts.setLayoutManager(new GridLayoutManager(mContext,3));
    }

    /*
        Loads the user's details into the fragment.
     */
    private void loadProfileDetails(){

        //Set text details
        app.tvBio.setText(mUser.getBio());
        app.tvName.setText(mUser.getName());
        app.tvFollowers.setText(String.valueOf(mUser.getFollowers()));
        app.tvFollowing.setText(String.valueOf(mUser.getFollowing()));

        //Set the profile picture
        ParseFile profilePicture = mUser.getProfilePicture();
        if(profilePicture != null){
            Glide.with(mContext)
                    .load(profilePicture.getUrl())
                    .transform(new RoundedCorners(300))
                    .into(app.ivProfilePicture);
        }
    }

    /*
        Gets the posts created by the user.
     */
    private void queryPosts() {

        //Get a query of the posts that are tagged as created by the user
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class)
                .whereContains("user", mUser.getObjectId());

        //Get the posts and add them to the recyclerview
        query.findInBackground((posts, e) ->{
            mAdapter.addAll(posts);
            app.tvPosts.setText(String.valueOf(mPosts.size()));
        });

    }
}