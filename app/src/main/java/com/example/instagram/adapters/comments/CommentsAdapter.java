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

/*
    Adapter class for the comments recyclerview
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentHolder> {

    public static final String TAG = "CommentsAdapter";

    private final Context mContext;

    private final List<Comment> mComments;

    /*
        Constructor from a context and a list of comments

        @param Context context - The context in which the recyclerview is being
                                    displayed
        @param List<Comment> comments - The list of comments to display

        @return none
     */
    public CommentsAdapter(Context context, List<Comment> comments) {
        mContext = context;
        mComments = comments;
    }

    @NotNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        com.example.instagram.databinding.ItemCommentBinding app = ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        View view = app.getRoot();

        return new CommentHolder(view,mContext, app);
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

    /*
        Cleans the elements of the recyclerview

        @param none

        @return void
     */
    public void clear() {
        mComments.clear();
        notifyDataSetChanged();
    }

    /*
        Adds a list of comment items to the recyclerview and notifies the adapter

        @param List<Comment> list - THe list of comments to add

        @return void
     */
    public void addAll(List<Comment> list) {
        mComments.addAll(list);
        notifyDataSetChanged();
    }

    /*
        Adds a single comment to the recyclerview and notifies the adapter

        @param int index - The position in which the comment is inserted
        @param Comment comment - The comment to insert

        @return void

     */
    public void add(int index, Comment comment){
        mComments.add(index,comment);
        notifyItemInserted(index);
    }
}
