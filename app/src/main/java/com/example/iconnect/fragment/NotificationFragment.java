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
import com.example.iconnect.Adapters.notificationAdapter;
import com.example.iconnect.R;
import com.example.iconnect.models.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {
    ShimmerRecyclerView notificationRV;
    ArrayList<Notification> notificationList;
    FirebaseDatabase database;

    public NotificationFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notification, container, false);
        notificationList = new ArrayList<>();
        notificationRV = view.findViewById(R.id.notificationRV);

        notificationRV.showShimmerAdapter();

        notificationAdapter adapter = new notificationAdapter(notificationList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);

        notificationRV.setLayoutManager(manager);
        notificationRV.setNestedScrollingEnabled(false);

        database.getReference().child("notification").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    notification.setNotificationId(dataSnapshot.getKey());
                    notificationList.add(notification);
                }
                notificationRV.setAdapter(adapter);
                notificationRV.hideShimmerAdapter();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}