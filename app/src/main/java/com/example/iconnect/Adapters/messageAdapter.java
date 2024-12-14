package com.example.iconnect.Adapters;


import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iconnect.R;
import com.example.iconnect.models.message;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class messageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
String senderGroup,receiverGroup;
ArrayList<message> list;

    public messageAdapter(String senderGroup, String receiverGroup, ArrayList<message> list) {
        this.senderGroup = senderGroup;
        this.receiverGroup = receiverGroup;
        this.list = list;
    }



    final int MESSAGE_SENT=0;
    final int MESSAGE_RECEIVE =  1;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
       if (viewType == MESSAGE_SENT){
           View view = LayoutInflater.from(viewGroup.getContext())
               .inflate(R.layout.message_send, viewGroup, false);
                return new senderViewHolder(view);

       }
       else{
           View view = LayoutInflater.from(viewGroup.getContext())
                   .inflate(R.layout.message_receive, viewGroup, false);
           return new receiverViewHolder(view);

       }
    }
    int reactions[] = new int[]{
            R.drawable.ic_fb_like,
            R.drawable.ic_fb_love,
            R.drawable.ic_fb_laugh,
            R.drawable.ic_fb_wow,
            R.drawable.ic_fb_sad,
            R.drawable.ic_fb_angry
    };

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        message message1 = list.get(position);
        ReactionsConfig config = new ReactionsConfigBuilder(viewHolder.itemView.getContext())
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(viewHolder.itemView.getContext(), config, (pos) -> {

            if (viewHolder.getClass()==senderViewHolder.class){
                senderViewHolder viewHolder1 = (senderViewHolder) viewHolder;
                viewHolder1.feeling.setImageResource(reactions[pos]);
                viewHolder1.feeling.setVisibility(View.VISIBLE);
            }

            else{
                receiverViewHolder viewHolder1 = (receiverViewHolder) viewHolder;
                viewHolder1.feeling.setImageResource(reactions[pos]);
                viewHolder1.feeling.setVisibility(View.VISIBLE);
            }

            message1.setFeeling(pos);
            FirebaseDatabase.getInstance().getReference().child("chats").child(senderGroup).child("messages").child(message1.getMesageId()).setValue(message1);
            FirebaseDatabase.getInstance().getReference().child("chats").child(receiverGroup).child("messages").child(message1.getMesageId()).setValue(message1);
            return true; // true is closing popup, false is requesting a new selection
        });

        if (viewHolder.getClass()==senderViewHolder.class){
            senderViewHolder viewHolder1 = (senderViewHolder) viewHolder;
            viewHolder1.message.setText(message1.getMessageBody());
            if (message1.getFeeling()>=0){
                viewHolder1.feeling.setImageResource(reactions[(int) message1.getFeeling()]);
                viewHolder1.feeling.setVisibility(View.VISIBLE);
            }else{
                viewHolder1.feeling.setVisibility(View.GONE);
            }

            viewHolder1.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });
        }
        else{
            receiverViewHolder viewHolder1 = (receiverViewHolder) viewHolder;
            viewHolder1.message.setText(message1.getMessageBody());

            if (message1.getFeeling()>=0){
                viewHolder1.feeling.setImageResource(reactions[(int) message1.getFeeling()]);
                viewHolder1.feeling.setVisibility(View.VISIBLE);
            }else{
                viewHolder1.feeling.setVisibility(View.GONE);
            }

            viewHolder1.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        message msg = list.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(msg.getMessageBy())){
            return MESSAGE_SENT;
        }
        else{
            return MESSAGE_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class senderViewHolder extends RecyclerView.ViewHolder{
    TextView message;
    ImageView feeling;

    public senderViewHolder(@NonNull View itemView) {
        super(itemView);
        message = itemView.findViewById(R.id.message_view);
        feeling = itemView.findViewById(R.id.feeling);
    }
}

class receiverViewHolder extends RecyclerView.ViewHolder{
    TextView message;
    ImageView feeling;

    public receiverViewHolder(@NonNull View itemView) {
        super(itemView);
        message = itemView.findViewById(R.id.message_view);
        feeling = itemView.findViewById(R.id.feeling);
    }
}

