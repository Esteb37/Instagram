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
    private User mUser;
    private int mPage = 0;
    private Context mContext;


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

        setupRecyclerView();

        loadProfileDetails();

        queryPosts(0);
    }

    private void setupRecyclerView(){

        List<Post> mPosts = new ArrayList<>();
        ImageAdapter mAdapter = new ImageAdapter(mContext, mPosts);

        GridLayoutManager manager = new GridLayoutManager(mContext,3);


        // set the mAdapter on the recycler view
        app.rvPosts.setAdapter(mAdapter);

        // set the layout manager on the recycler view
        app.rvPosts.setLayoutManager(manager);
    }

    private void queryPosts(int mPage) {

        List<Post> postPointers = mUser.getList("posts");
        List<Post> posts = new ArrayList<>();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        /*for(pointer : postPointers) {
            query.getInBackground(pointer.get (post, e) -> {
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



            });*/

    }

    private void loadProfileDetails(){
        ParseFile profilePicture = mUser.getProfilePicture();
        if(profilePicture != null){
            Glide.with(mContext)
                    .load(profilePicture.getUrl())
                    .transform(new RoundedCorners(300))
                    .into(app.ivProfilePicture);
        }

        app.tvBio.setText(mUser.getBio());
        app.tvName.setText(mUser.getName());
        app.tvFollowers.setText(String.valueOf(mUser.getFollowers()));
        app.tvFollowing.setText(String.valueOf(mUser.getFollowing()));
    }

}