package com.example.instagram.adapters.comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.databinding.ItemCommentBinding;
import com.example.instagram.models.Comment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentHolder> {

    private final Context mContext;
    private final List<Comment> mComments;
    ItemCommentBinding app;

    public CommentsAdapter(Context context, List<Comment> comments) {
        mContext = context;
        mComments = comments;
    }

    @NonNull
    @NotNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        app = ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        View view = app.getRoot();

        return new CommentHolder(view,mContext,app);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentHolder holder, int position) {
        Comment comment = mComments.get(position);
        holder.bind(comment);


    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mComments.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Comment> list) {
        mComments.addAll(list);
        notifyDataSetChanged();
    }
}
