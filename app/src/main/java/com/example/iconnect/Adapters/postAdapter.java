package com.example.iconnect.Adapters;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iconnect.CommentActivity;
import com.example.iconnect.R;
import com.example.iconnect.models.Notification;
import com.example.iconnect.models.User;
import com.example.iconnect.models.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class postAdapter extends RecyclerView.Adapter<postAdapter.ViewHolder> {

    public postAdapter(ArrayList<Post> list) {
        this.list = list;
    }

    ArrayList<Post> list;
    TextView username,profession,about_post,likes,comments,shares;
    ImageView postImg,profile;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);

        }
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.post_rv, viewGroup, false);
        about_post = view.findViewById(R.id.aboutImg);
        postImg = view.findViewById(R.id.postImg);
        comments = view.findViewById(R.id.comments);
        likes = view.findViewById(R.id.likes);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Post model = list.get(position);
        Picasso.get()
                .load(model.getPostImg())
                .placeholder(R.drawable.post_placeholder)
                .into(postImg);
        about_post.setText(model.getPostDescription());
        likes.setText(model.getPostLikes()+"");
        comments.setText(model.getComments_count()+"");

        FirebaseDatabase.getInstance().getReference().child("posts").child(model.getPostId())
                .child("likes").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        likes = viewHolder.itemView.findViewById(R.id.likes);
                        if (snapshot.exists()){
                            likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_active,0,0,0);
                        }
                        else {
                            likes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FirebaseDatabase.getInstance().getReference().child("posts").child(model.getPostId())
                                            .child("likes").child(FirebaseAuth.getInstance().getUid()).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference().child("posts")
                                                            .child(model.getPostId()).child("postLikes").setValue(model.getPostLikes()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_active,0,0,0);

                                                                    Notification notification = new Notification();
                                                                    notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                    notification.setNotificationAt(new Date().getTime());
                                                                    notification.setPostId(model.getPostId());
                                                                    notification.setPostedBy(model.getPostedBy());
                                                                    notification.setType("like");

                                                                    FirebaseDatabase.getInstance().getReference().child("notification")
                                                                            .child(model.getPostedBy()).push().setValue(notification);
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getPostedBy()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                username = (TextView) viewHolder.itemView.findViewById(R.id.name);
                profession = viewHolder.itemView.findViewById(R.id.profession);
                profile = viewHolder.itemView.findViewById(R.id.profile);


                User user = snapshot.getValue(User.class);
                Picasso.get()
                        .load(user.getProfile())
                        .placeholder(R.drawable.profile_placeholder)
                        .into(profile);
                username.setText(user.getName());
                profession.setText(user.getProfession());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CommentActivity.class);
                intent.putExtra("postId",model.getPostId());
                intent.putExtra("postedBy",model.getPostedBy());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

