package com.example.instagram.adapters.images;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.databinding.ItemImageBinding;
import com.example.instagram.models.Post;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageHolder> {

    private final Context mContext;
    private final List<Post> mPosts;
    ItemImageBinding app;

    public ImageAdapter(Context context, List<Post> posts) {
        mPosts = posts;
        mContext = context;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        app = ItemImageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        View view = app.getRoot();

        return new ImageHolder(view,mContext,app);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

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
