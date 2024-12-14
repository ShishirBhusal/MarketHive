package com.example.iconnect.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iconnect.Adapters.followerAdapter;
import com.example.iconnect.LogInActivity;
import com.example.iconnect.R;
import com.example.iconnect.models.User;
import com.example.iconnect.models.Follow;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {
    RecyclerView profile_friends_RV;
    ArrayList<Follow> friendsList;
    ActivityResultLauncher<String> getCoverPhoto, getProfile;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        profile_friends_RV = view.findViewById(R.id.friendsRV_P);
        ImageView change_cover = view.findViewById(R.id.change_cover);
        ImageView change_profile = view.findViewById(R.id.change_profile);
        ImageView coverPhoto = view.findViewById(R.id.cover_photo);
        ImageView profilePhoto = view.findViewById(R.id.profile);
        ImageView signout = view.findViewById(R.id.signout);
        TextView name = view.findViewById(R.id.name);
        TextView profession = view.findViewById(R.id.profession);
        TextView followers = view.findViewById(R.id.followers);
        TextView following = view.findViewById(R.id.following);


        database.getReference().child("Users").child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            Picasso.get()
                                    .load(user.getCover_photo())
                                    .placeholder(R.drawable.post_placeholder)
                                    .into(coverPhoto);
                            Picasso.get()
                                    .load(user.getProfile())
                                    .placeholder(R.drawable.profile_placeholder)
                                    .into(profilePhoto);
                            name.setText(user.getName());
                            profession.setText(user.getProfession());
                            followers.setText(user.getFollowers_count()+"");
                            following.setText(user.getFollowing()+"");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        friendsList = new ArrayList<>();

        followerAdapter adapter = new followerAdapter(friendsList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        profile_friends_RV.setLayoutManager(manager);
        profile_friends_RV.setNestedScrollingEnabled(false);
        profile_friends_RV.setAdapter(adapter);

        database.getReference().child("Users").child(auth.getUid()).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Follow follow = dataSnapshot.getValue(Follow.class);
                    friendsList.add(follow);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getCoverPhoto = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result!=null) {
                    coverPhoto.setImageURI(result);
                    final StorageReference refrence = storage.getReference().child("cover_photo").child(auth.getUid());
                    refrence.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Cover photo saved", Toast.LENGTH_SHORT).show();
                            refrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    database.getReference().child("Users").child(auth.getUid())
                                            .child("cover_photo").setValue(uri.toString());
                                }
                            });
                        }
                    });
                }
            }
        });


        getProfile = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    profilePhoto.setImageURI(result);
                    final StorageReference refrence = storage.getReference().child("profile").child(auth.getUid());
                    refrence.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "profile photo saved", Toast.LENGTH_SHORT).show();
                            refrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    database.getReference().child("Users").child(auth.getUid())
                                            .child("profile").setValue(uri.toString());
                                }
                            });
                        }
                    });
                }
            }
        });

        change_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCoverPhoto.launch("image/*");
            }
        });
        change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProfile.launch("image/*");
            }
        });
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(getActivity(), LogInActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

}