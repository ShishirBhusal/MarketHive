package com.example.iconnect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.example.iconnect.fragment.AddPostFragment;
import com.example.iconnect.fragment.FriendsFragment;
import com.example.iconnect.fragment.HomeFragment;
import com.example.iconnect.fragment.NotificationFragment;
import com.example.iconnect.fragment.ProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.iammert.library.readablebottombar.ReadableBottomBar;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ReadableBottomBar bottomBar = findViewById(R.id.readableBottomBar);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer,new HomeFragment());
        transaction.commit();



        bottomBar.setOnItemSelectListener(new ReadableBottomBar.ItemSelectListener() {
            @Override
            public void onItemSelected(int i) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch(i){
                    case 0:
                        transaction.replace(R.id.frameContainer,new HomeFragment());
                        break;
                    case 1:
                        transaction.replace(R.id.frameContainer,new FriendsFragment());
                        break;
                    case 2:
                        transaction.replace(R.id.frameContainer,new AddPostFragment());
                        break;
                    case 3:
                        transaction.replace(R.id.frameContainer,new NotificationFragment());
                        break;
                    case 4:
                        transaction.replace(R.id.frameContainer,new ProfileFragment());
                        break;
                }
                transaction.commit();
            }
        });
    }
    String uid = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("online").setValue(true);
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("token").setValue(s);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("online").setValue(false);
    }
    }
