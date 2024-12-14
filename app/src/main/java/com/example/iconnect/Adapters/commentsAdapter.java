package com.example.iconnect.Adapters;


import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iconnect.R;
import com.example.iconnect.models.Comment;
import com.example.iconnect.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class commentsAdapter extends RecyclerView.Adapter<commentsAdapter.ViewHolder> {

    public commentsAdapter(ArrayList<Comment> list) {
        this.list = list;
    }

    ArrayList<Comment> list;
    TextView commentBody;
    ImageView profile;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);

        }
    }



    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.comments_rv, viewGroup, false);
        commentBody =view.findViewById(R.id.commentBody);
        profile = view.findViewById(R.id.profile);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Comment comment = list.get(position);
        commentBody.setText(comment.getCommentBody());
        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getCommentedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentBody =viewHolder.itemView.findViewById(R.id.commentBody);
                User user = snapshot.getValue(User.class);
                Picasso.get()
                        .load(user.getProfile())
                        .placeholder(R.drawable.profile_placeholder)
                        .into(profile);
                commentBody.setText(Html.fromHtml("<b>" + user.getName()+ "</b> "  + comment.getCommentBody() ));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

