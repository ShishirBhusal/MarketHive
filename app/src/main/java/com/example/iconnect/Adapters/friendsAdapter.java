package com.example.iconnect.Adapters;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iconnect.MessageActivity;
import com.example.iconnect.R;
import com.example.iconnect.models.Notification;
import com.example.iconnect.models.User;
import com.example.iconnect.models.Follow;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class friendsAdapter extends RecyclerView.Adapter<friendsAdapter.ViewHolder> {

    public friendsAdapter(ArrayList<User> list) {
        this.list = list;
    }

    ArrayList<User> list;
    TextView username,profession;
    ImageView profile,chats;
    Button followBTN;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);

        }
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.friends_rv, viewGroup, false);
        username = (TextView) view.findViewById(R.id.name);
        profile = view.findViewById(R.id.profile);
        profession = view.findViewById(R.id.profession);
        chats = view.findViewById(R.id.chats);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        followBTN = viewHolder.itemView.findViewById(R.id.followBtn);


        User user = list.get(position);
        Picasso.get()
                .load(user.getProfile())
                .placeholder(R.drawable.profile_placeholder)
                .into(profile);
        username.setText(user.getName());
        profession.setText(user.getProfession());


        String sendGroup = FirebaseAuth.getInstance().getUid()+user.getUserId();
        String receiveGroup = user.getUserId()+FirebaseAuth.getInstance().getUid();


        FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUserId()).child("followers").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats = viewHolder.itemView.findViewById(R.id.chats);

                followBTN = viewHolder.itemView.findViewById(R.id.followBtn);
                if (snapshot.exists()){
                    followBTN.setBackground(viewHolder.itemView.getContext().getResources().getDrawable(R.drawable.color_cover));
                    followBTN.setTextColor(viewHolder.itemView.getContext().getResources().getColor(R.color.gray));
                    followBTN.setText("Following");
                    followBTN.setEnabled(false);
                    chats.setVisibility(View.VISIBLE);
                }
                else{
                    followBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Follow follow = new Follow();
                            follow.setFollowedBy(FirebaseAuth.getInstance().getUid());
                            follow.setFollowedAt(new Date().getTime());
                            FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUserId()).child("followers")
                                    .child(FirebaseAuth.getInstance().getUid()).setValue(follow).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUserId())
                                                    .child("followers_count").setValue(user.getFollowers_count()+1)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                            Toast.makeText(view.getContext(), "started following" + user.getName(), Toast.LENGTH_SHORT).show();
                                                            followBTN = viewHolder.itemView.findViewById(R.id.followBtn);
                                                            followBTN.setBackground(viewHolder.itemView.getContext().getResources().getDrawable(R.drawable.color_cover));
                                                            followBTN.setTextColor(viewHolder.itemView.getContext().getResources().getColor(R.color.gray));
                                                            followBTN.setText("Following");
                                                            followBTN.setEnabled(false);
                                                            chats.setVisibility(View.VISIBLE);

                                                            FirebaseDatabase.getInstance().getReference().child("chats").child(sendGroup);
                                                            FirebaseDatabase.getInstance().getReference().child("chats").child(receiveGroup);


                                                            HashMap<String, Object> lastMsgObj_Sender = new HashMap<>();
                                                            lastMsgObj_Sender.put("timestamp",new Date().getTime());
                                                            lastMsgObj_Sender.put("messageFor",user.getUserId());
                                                            lastMsgObj_Sender.put("lastMsg","Tap to chat");

                                                            HashMap<String, Object> lastMsgObj_Reciever = new HashMap<>();
                                                            lastMsgObj_Reciever.put("timestamp",new Date().getTime());
                                                            lastMsgObj_Reciever.put("messageFor",FirebaseAuth.getInstance().getUid());
                                                            lastMsgObj_Reciever.put("lastMsg","Tap to chat");

                                                            FirebaseDatabase.getInstance().getReference().child("chats").child(sendGroup).updateChildren(lastMsgObj_Sender);
                                                            FirebaseDatabase.getInstance().getReference().child("chats").child(receiveGroup).updateChildren(lastMsgObj_Reciever);


                                                            Notification notification = new Notification();
                                                            notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                            notification.setNotificationAt(new Date().getTime());
                                                            notification.setType("follow");

                                                                FirebaseDatabase.getInstance().getReference().child("notification")
                                                                        .child(user.getUserId()).push().setValue(notification);

                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            if(snapshot.exists()){
                                                                                User user1 = snapshot.getValue(User.class);
                                                                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                                                                        .child("following").setValue(user1.getFollowing()+1);
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });
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

        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MessageActivity.class);
                intent.putExtra("userId",user.getUserId());
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

