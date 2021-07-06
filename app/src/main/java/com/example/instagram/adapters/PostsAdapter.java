package com.example.instagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.R;
import com.example.instagram.databinding.ActivityPostBinding;
import com.example.instagram.databinding.ItemPostBinding;
import com.example.instagram.models.Post;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostHolder> {

    private Context context;
    private List<Post> posts;
    ItemPostBinding app;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        app = ItemPostBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        View view = app.getRoot();

        return new PostHolder(view,context,app);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }


}