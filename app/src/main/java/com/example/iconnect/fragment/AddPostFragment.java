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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iconnect.MainActivity;
import com.example.iconnect.R;
import com.example.iconnect.models.User;
import com.example.iconnect.models.Post;
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

import java.util.Date;

public class AddPostFragment extends Fragment {
    ActivityResultLauncher<String> uploadImg;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Uri uri;
    ProgressDialog dialog;
    public AddPostFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        dialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_post, container, false);
        EditText post_decription = view.findViewById(R.id.aboutImg);
        ImageView postImg = view.findViewById(R.id.postImg);
        Button postBtn = view.findViewById(R.id.postBtn);
        ImageView selectImgBtn = view.findViewById(R.id.selectImage);
        TextView name = view.findViewById(R.id.name);
        TextView profession = view.findViewById(R.id.profession);
        ImageView profilePhoto = view.findViewById(R.id.profile);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Uploading post");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        database.getReference().child("Users").child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            Picasso.get()
                                    .load(user.getProfile())
                                    .placeholder(R.drawable.profile_placeholder)
                                    .into(profilePhoto);
                            name.setText(user.getName());
                            profession.setText(user.getProfession());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        post_decription.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String description = post_decription.getText().toString();
                            if (!description.isEmpty()){
                                postBtn.setBackground(getContext().getResources().getDrawable(R.drawable.btn_background));
                                postBtn.setTextColor(getContext().getResources().getColor(R.color.white));
                                postBtn.setEnabled(true);
                            }
                            else{
                                postBtn.setBackground(getContext().getResources().getDrawable(R.drawable.color_cover));
                                postBtn.setTextColor(getContext().getResources().getColor(R.color.gray));
                                postBtn.setEnabled(false);
                            }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                }
        );

        uploadImg = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
               if (result!=null) {
                   uri = result;
                   postImg.setImageURI(uri);
                   postBtn.setBackground(getContext().getResources().getDrawable(R.drawable.btn_background));
                   postBtn.setTextColor(getContext().getResources().getColor(R.color.white));
                   postBtn.setEnabled(true);
                   postImg.setVisibility(view.VISIBLE);
               }
            }
        });


        selectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImg.launch("image/*");

            }
        });
        
        
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                final StorageReference reference = storage.getReference().child("post").child(auth.getUid()).child(new Date().getTime()+"");
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Post post = new Post();
                                post.setPostImg(uri.toString());
                                post.setPostedBy(auth.getUid());
                                post.setPostDescription(post_decription.getText().toString());
                                post.setPostedAt(new Date().getTime());
                                
                                database.getReference().child("posts").push().setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.dismiss();
                                        Toast.makeText(view.getContext(), "Posted successfully", Toast.LENGTH_SHORT).show();
                                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                        transaction.replace(R.id.frameContainer,new HomeFragment());
                                        transaction.commit();
                                    }
                                });
                                
                            }
                        });
                    }
                });
            }
        });
        return view;
    }
}