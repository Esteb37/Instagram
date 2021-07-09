package com.example.instagram.adapters.posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.databinding.ItemPostBinding;
import com.example.instagram.models.Post;
import com.parse.ParseException;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostHolder> {

    private final Context mContext;
    private final List<Post> mPosts;
    ItemPostBinding app;


    //Listener for recyclerview item click
    public interface OnClickListener {

        //Return clicked position
        void onItemClicked(int position);
    }

    OnClickListener mClickListener;

    public interface OnScrollListener {

        //Return clicked position
        void onScroll(int position);
    }

    OnScrollListener mScrollListener;

    public PostsAdapter(Context context, List<Post> posts, OnClickListener clickListener, OnScrollListener scrollListener) {
        mContext = context;
        mPosts = posts;
        mClickListener = clickListener;
        mScrollListener = scrollListener;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        app = ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        View view = app.getRoot();

        return new PostHolder(view,mContext,app,mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        Post post = mPosts.get(position);
        try {
            holder.bind(post);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (mScrollListener != null)  mScrollListener.onScroll(position);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }


}