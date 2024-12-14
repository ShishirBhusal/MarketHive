package com.example.iconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.iconnect.Adapters.chatsAdapter;
import com.example.iconnect.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    ShimmerRecyclerView chatsRV;
    ArrayList<User> chatsList;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ImageView profile;
    DatabaseReference reference;
    ArrayList<String> contactedUserList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        chatsRV = findViewById(R.id.chatsRV);
        profile = findViewById(R.id.profile);

        chatsRV.showShimmerAdapter();

        chatsList = new ArrayList<>();
        contactedUserList = new ArrayList<>();

        database.getReference().child("Users").child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            Picasso.get()
                                    .load(user.getProfile())
                                    .placeholder(R.drawable.profile_placeholder)
                                    .into(profile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        chatsAdapter adapter = new chatsAdapter(chatsList);
        LinearLayoutManager manager = new LinearLayoutManager(ChatsActivity.this);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);

        chatsRV.setLayoutManager(manager);
        chatsRV.setAdapter(adapter);


        reference = database.getReference().child("chats");
        Query query = reference.orderByChild("timestamp").limitToLast(10000);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactedUserList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String messageFor = dataSnapshot.child("messageFor").getValue(String.class);
                    if (dataSnapshot.getKey().equals(auth.getUid()+messageFor)) {
                        contactedUserList.add(messageFor);
                    }
                }

                database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatsList.clear();

                            for (String uid: contactedUserList){

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    User user = dataSnapshot.getValue(User.class);
                                    user.setUserId(dataSnapshot.getKey());

                                    if (user.getUserId().equals(uid)){
                                        chatsList.add(user);
                                        break;
                                    }

                            }
                        }
                        chatsRV.setAdapter(adapter);
                        chatsRV.hideShimmerAdapter();
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatsActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


    }
    String uid = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("online").setValue(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("online").setValue(false);
    }
}