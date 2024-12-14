package com.example.iconnect.Adapters;


import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iconnect.CommentActivity;
import com.example.iconnect.R;
import com.example.iconnect.models.Notification;
import com.example.iconnect.models.User;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class notificationAdapter extends RecyclerView.Adapter<notificationAdapter.ViewHolder> {

    public notificationAdapter(ArrayList<Notification> list) {
        this.list = list;
    }

    ArrayList<Notification> list;
    TextView notification_time,message;
    ImageView profile;
    ConstraintLayout openNotification;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

        }
    }



    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.notification_rv, viewGroup, false);
        notification_time = (TextView) view.findViewById(R.id.time);
        profile = view.findViewById(R.id.profile);
        message = view.findViewById(R.id.name);
        openNotification = view.findViewById(R.id.openNotification);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Notification model = list.get(position);

        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notification_time = (TextView) viewHolder.itemView.findViewById(R.id.time);
                        profile = viewHolder.itemView.findViewById(R.id.profile);
                        message = viewHolder.itemView.findViewById(R.id.name);
                        openNotification = viewHolder.itemView.findViewById(R.id.openNotification);
                        if (snapshot.exists()) {
                            message = viewHolder.itemView.findViewById(R.id.name);
                            notification_time = viewHolder.itemView.findViewById(R.id.time);
                            User user = snapshot.getValue(User.class);

                            Picasso.get()
                                    .load(user.getProfile())
                                    .placeholder(R.drawable.profile_placeholder)
                                    .into(profile);
                            String time = TimeAgo.using(model.getNotificationAt());
                            notification_time.setText(time);

                            if (model.getType().equals("like")){
                                message.setText(Html.fromHtml("<b>" + user.getName()+ "</b> "  + "liked your post"));
                            }else if (model.getType().equals("comment")){
                                message.setText(Html.fromHtml("<b>" + user.getName()+ "</b> "  + "commented on your post"));
                            }else{
                                message.setText(Html.fromHtml("<b>" + user.getName()+ "</b> "  + "started following you"));
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!model.getType().equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("notification").child(model.getPostedBy()).child(model.getNotificationId()).child("checkOpen").setValue(true);
                    openNotification.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.white));
                    Intent intent = new Intent(view.getContext(), CommentActivity.class);
                    intent.putExtra("postId", model.getPostId());
                    intent.putExtra("postedBy", model.getPostedBy());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                }
            }
        });
        Boolean checkOpen = model.isCheckOpen();
        if (checkOpen){
            openNotification.setBackgroundColor(viewHolder.itemView.getContext().getResources().getColor(R.color.white));

        }else{}
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }
}

