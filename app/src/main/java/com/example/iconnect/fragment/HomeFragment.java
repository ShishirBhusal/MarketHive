package com.example.iconnect.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.iconnect.Adapters.postAdapter;
import com.example.iconnect.Adapters.storyAdapter;
import com.example.iconnect.CartActivity;
import com.example.iconnect.ChatsActivity;
import com.example.iconnect.CommentActivity;
import com.example.iconnect.EventActivity;
import com.example.iconnect.MainActivity;
import com.example.iconnect.R;
import com.example.iconnect.models.User;
import com.example.iconnect.models.Userstory;
import com.example.iconnect.models.Post;
import com.example.iconnect.models.Story;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {
ShimmerRecyclerView storyRV;
ShimmerRecyclerView postRv;
ArrayList<Story> storyList;
ArrayList<Post> postList;
FirebaseAuth auth;
FirebaseDatabase database;
FirebaseStorage storage;
RoundedImageView addStory,profile;
TextView whatOnMind;
ActivityResultLauncher<String> addStoryImg;
ProgressDialog dialog;

Button joinEvent;
ImageView chats,cart;

    public HomeFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        dialog = new ProgressDialog(getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        addStory = view.findViewById(R.id.postImg);
        profile = view.findViewById(R.id.profile);
        whatOnMind = view.findViewById(R.id.whatOnMind);
        storyRV = view.findViewById(R.id.storyRV);
        postRv = view.findViewById(R.id.postRV);
        chats = view.findViewById(R.id.chats);
        cart = view.findViewById(R.id.cart);
        joinEvent = view.findViewById(R.id.joinEvent);

        ConstraintLayout adminLayout = view.findViewById(R.id.addAnyDiscount);

        postRv.showShimmerAdapter();
        storyRV.showShimmerAdapter();

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Uploading story");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);


        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
            }
        });

        joinEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EventActivity.class);
                startActivity(intent);
            }
        });

        whatOnMind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frameContainer,new AddPostFragment());
                transaction.commit();
            }
        });

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
                            Picasso.get()
                                    .load(user.getProfile())
                                    .placeholder(R.drawable.profile_placeholder)
                                    .into(addStory);
                            boolean isAdmin = user.isAdmin();
                            if (!isAdmin) {
                                adminLayout.setVisibility(View.GONE); // Hide the layout if not admin
                            } else {
                                adminLayout.setVisibility(View.VISIBLE); // Show the layout if admin
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        addStoryImg = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result!=null) {
                    dialog.show();
                    addStory.setImageURI(result);
                    final StorageReference reference = storage.getReference().child("stories").child(auth.getUid()).child(new Date().getTime()+"");
                    reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Story story = new Story();
                                    story.setStoryAt(new Date().getTime());
                                    database.getReference().child("stories").child(auth.getUid()).child("storyBy").setValue(story.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Userstory userstory = new Userstory(uri.toString(),story.getStoryAt());
                                            database.getReference().child("stories").child(auth.getUid()).child("userstories").push().setValue(userstory).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    dialog.dismiss();
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
        });

        addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStoryImg.launch("image/*");
            }
        });

        storyList = new ArrayList<>();


        storyAdapter adapter = new storyAdapter(storyList);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,true);
        storyRV.setLayoutManager(manager);
        storyRV.setNestedScrollingEnabled(false);

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    storyList.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        Story story = new Story();
                        story.setStoryBy(dataSnapshot.getKey());
                        story.setStoryAt(dataSnapshot.child("storyBy").getValue(Long.class));

                        ArrayList<Userstory> stories = new ArrayList<>();
                        for (DataSnapshot dataSnapshot1: dataSnapshot.child("userstories").getChildren()){
                            Userstory userstory = dataSnapshot1.getValue(Userstory.class);
                            stories.add(userstory);
                        }
                        story.setUserstories(stories);
                        storyList.add(story);
                    }
                    storyRV.setAdapter(adapter);
                    storyRV.hideShimmerAdapter();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        postList = new ArrayList<>();
        postAdapter adapter2 = new postAdapter(postList);
        LinearLayoutManager manager2 = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);
        postRv.setLayoutManager(manager2);
        postRv.setNestedScrollingEnabled(false);

        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setPostId(dataSnapshot.getKey());
                    postList.add(post);
                }
                postRv.setAdapter(adapter2);
                postRv.hideShimmerAdapter();
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChatsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}