package com.example.iconnect.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iconnect.R;
import com.example.iconnect.models.Follow;
import com.example.iconnect.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class followerAdapter extends RecyclerView.Adapter<followerAdapter.ViewHolder> {

    public followerAdapter(ArrayList<Follow> list) {
        this.list = list;
    }

    ArrayList<Follow> list;
    ImageView profile;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);

        }
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.friends_rv_p, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

         Follow follow = list.get(position);
        FirebaseDatabase.getInstance().getReference().child("Users").child(follow.getFollowedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profile = viewHolder.itemView.findViewById(R.id.profile);
                User user = snapshot.getValue(User.class);
                Picasso.get()
                        .load(user.getProfile())
                        .placeholder(R.drawable.profile_placeholder)
                        .into(profile);
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

