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


public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    private FragmentProfileBinding app;
    private Context mContext;

    private User mUser;
    private List<Post> mPosts;
    private ImageAdapter mAdapter;

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
        assert getArguments() != null;
        mUser = getArguments().getParcelable("user");
        return app.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = view.getContext();

        setupRecyclerView();

        loadProfileDetails();

        queryPosts();
    }

    private void setupRecyclerView(){
        mPosts = new ArrayList<>();
        mAdapter = new ImageAdapter(mContext, mPosts);

        GridLayoutManager manager = new GridLayoutManager(mContext,3);

        // set the mAdapter on the recycler view
        app.rvPosts.setAdapter(mAdapter);

        // set the layout manager on the recycler view
        app.rvPosts.setLayoutManager(manager);
    }

    private void loadProfileDetails(){
        app.tvBio.setText(mUser.getBio());
        app.tvName.setText(mUser.getName());
        app.tvFollowers.setText(String.valueOf(mUser.getFollowers()));
        app.tvFollowing.setText(String.valueOf(mUser.getFollowing()));

        ParseFile profilePicture = mUser.getProfilePicture();
        if(profilePicture != null){
            Glide.with(mContext)
                    .load(profilePicture.getUrl())
                    .transform(new RoundedCorners(300))
                    .into(app.ivProfilePicture);
        }
    }

    private void queryPosts() {

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class).whereContains("user", mUser.getObjectId());

        query.findInBackground((posts, e) ->{
            mAdapter.addAll(posts);
            app.tvPosts.setText(String.valueOf(mPosts.size()));
        });

    }
}