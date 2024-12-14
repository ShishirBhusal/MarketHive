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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iconnect.MessageActivity;
import com.example.iconnect.R;
import com.example.iconnect.models.Follow;
import com.example.iconnect.models.Notification;
import com.example.iconnect.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class chatsAdapter extends RecyclerView.Adapter<chatsAdapter.ViewHolder> {

    public chatsAdapter(ArrayList<User> list) {
        this.list = list;
    }

    ArrayList<User> list;
    TextView username,lastMsg,MsgTime;
    ImageView profile;
    View isOnline;
    ConstraintLayout openMessage;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);

        }
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.chats_rv, viewGroup, false);
        username = (TextView) view.findViewById(R.id.name);
        profile = view.findViewById(R.id.profile);
        lastMsg = view.findViewById(R.id.lastMsg);
        openMessage = view.findViewById(R.id.openMessage);
        MsgTime = view.findViewById(R.id.MsgTime);
        isOnline = view.findViewById(R.id.isOnline);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        User user = list.get(position);
        String sendGroup = FirebaseAuth.getInstance().getUid()+user.getUserId();

        FirebaseDatabase.getInstance().getReference().child("chats").child(sendGroup).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lastMsg = viewHolder.itemView.findViewById(R.id.lastMsg);
                MsgTime = viewHolder.itemView.findViewById(R.id.MsgTime);
                username = (TextView) viewHolder.itemView.findViewById(R.id.name);


                if (snapshot.exists()) {
                    String LastMessage = snapshot.child("lastMsg").getValue(String.class);
                    long messageTime = snapshot.child("timestamp").getValue(Long.class);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    lastMsg.setText(LastMessage);
                    MsgTime.setText(dateFormat.format(new Date(messageTime)));
                }
                else{
                    lastMsg.setText("Tap to chat");
                    MsgTime.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Picasso.get()
                .load(user.getProfile())
                .placeholder(R.drawable.profile_placeholder)
                .into(profile);
        username.setText(user.getName());
        if (user.isOnline()){
            isOnline.setVisibility(View.VISIBLE);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chats").child(FirebaseAuth.getInstance().getUid()+user.getUserId());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("checkOpen")) {
                    lastMsg = viewHolder.itemView.findViewById(R.id.lastMsg);
                    MsgTime = viewHolder.itemView.findViewById(R.id.MsgTime);
                    boolean checkOpen = snapshot.child("checkOpen").getValue(boolean.class);
                    if (checkOpen) {
                        username.setTextColor(viewHolder.itemView.getContext().getResources().getColor(R.color.gray));
                        lastMsg.setTextColor(viewHolder.itemView.getContext().getResources().getColor(R.color.gray));
                        MsgTime.setTextColor(viewHolder.itemView.getContext().getResources().getColor(R.color.gray));
                    } else {
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        openMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("checkOpen").setValue(true);
                username.setTextColor(viewHolder.itemView.getContext().getResources().getColor(R.color.gray));
                lastMsg.setTextColor(viewHolder.itemView.getContext().getResources().getColor(R.color.gray));
                MsgTime.setTextColor(viewHolder.itemView.getContext().getResources().getColor(R.color.gray));
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

