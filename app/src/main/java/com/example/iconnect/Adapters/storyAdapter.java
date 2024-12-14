package com.example.iconnect.Adapters;


import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.circularstatusview.CircularStatusView;
import com.example.iconnect.R;
import com.example.iconnect.models.User;
import com.example.iconnect.models.Userstory;
import com.example.iconnect.models.Story;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class storyAdapter extends RecyclerView.Adapter<storyAdapter.ViewHolder> {

    public storyAdapter(ArrayList<Story> list) {
        this.list = list;
    }

    ArrayList<Story> list;
    TextView username;
    ImageView story,profile;
    CircularStatusView circularStatusView;
    ProgressDialog dialog;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View view) {
            super(view);

        }
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.story_rv, viewGroup, false);
        username = (TextView) view.findViewById(R.id.username);
        story = view.findViewById(R.id.storyImg);
        profile = view.findViewById(R.id.profile);
        circularStatusView = view.findViewById(R.id.status_circle);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Story storyModel = list.get(position);

        if (storyModel.getUserstories().size() > 0) {
            Userstory laststory = storyModel.getUserstories().get(storyModel.getUserstories().size() - 1);
            Picasso.get()
                    .load(laststory.getImage())
                    .into(story);
            circularStatusView.setPortionsCount(storyModel.getUserstories().size());

            FirebaseDatabase.getInstance().getReference().child("Users").child(storyModel.getStoryBy()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    username = (TextView) viewHolder.itemView.findViewById(R.id.username);
                    profile = viewHolder.itemView.findViewById(R.id.profile);
                    story = viewHolder.itemView.findViewById(R.id.storyImg);

                    User user = snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile())
                            .placeholder(R.drawable.profile_placeholder)
                            .into(profile);
                    username.setText(user.getName());
                    story.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            ArrayList<MyStory> myStories = new ArrayList<>();

                            for (Userstory story : storyModel.getUserstories()) {
                                myStories.add(new MyStory(
                                        story.getImage()
                                ));
                            }

                            new StoryView.Builder(((AppCompatActivity) view.getContext()).getSupportFragmentManager())
                                    .setStoriesList(myStories) // Required
                                    .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                                    .setTitleText(user.getName()) // Default is Hidden
                                    .setSubtitleText("") // Default is Hidden
                                    .setTitleLogoUrl(user.getProfile()) // Default is Hidden
                                    .setStoryClickListeners(new StoryClickListeners() {
                                        @Override
                                        public void onDescriptionClickListener(int position) {
                                            //your action
                                        }

                                        @Override
                                        public void onTitleIconClickListener(int position) {
                                            //your action
                                        }
                                    }) // Optional Listeners
                                    .build() // Must be called before calling show method
                                    .show();
                        }
                    });
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

