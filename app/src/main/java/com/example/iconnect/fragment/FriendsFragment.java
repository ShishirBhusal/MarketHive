package com.example.iconnect.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.iconnect.Adapters.friendsAdapter;
import com.example.iconnect.R;
import com.example.iconnect.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FriendsFragment extends Fragment {
    ShimmerRecyclerView friendsRV;
    ArrayList<User> friendsList;
    FirebaseAuth auth;
    FirebaseDatabase database;
   
    public FriendsFragment() {
        // Required empty public constructor
    }

 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_friends, container, false);
        friendsRV = view.findViewById(R.id.friendsRV);

        friendsRV.showShimmerAdapter();

        friendsList = new ArrayList<>();
        friendsAdapter adapter = new friendsAdapter(friendsList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);





        friendsRV.setLayoutManager(manager);
         friendsRV.setAdapter(adapter);

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    user.setUserId(dataSnapshot.getKey());
                    if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
                        friendsList.add(user);
                    }
                }
                friendsRV.setAdapter(adapter);
                friendsRV.hideShimmerAdapter();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}