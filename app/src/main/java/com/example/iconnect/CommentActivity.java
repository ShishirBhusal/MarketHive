package com.example.iconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iconnect.Adapters.commentsAdapter;
import com.example.iconnect.models.Comment;
import com.example.iconnect.models.Notification;
import com.example.iconnect.models.User;
import com.example.iconnect.models.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
Intent intent;
String postId,postedBy;
FirebaseAuth auth;
FirebaseDatabase database;
ArrayList<Comment> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ImageView profile = findViewById(R.id.profile);
        ImageView postImg = findViewById(R.id.postImg);
        TextView postDescription = findViewById(R.id.aboutPost);
        TextView likes = findViewById(R.id.likes);
        TextView comments = findViewById(R.id.comments);
        ImageView upload_comment = findViewById(R.id.upload_comment);
        EditText write_comment = findViewById(R.id.write_comment);
        RecyclerView comments_rv = findViewById(R.id.commentRV);

        setSupportActionBar(findViewById(R.id.toolbar));
        CommentActivity.this.setTitle("Review Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        postId = intent.getStringExtra("postId");
        postedBy = intent.getStringExtra("postedBy");

        database.getReference().child("posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                Picasso.get()
                        .load(post.getPostImg())
                        .placeholder(R.drawable.post_placeholder)
                        .into(postImg);
                postDescription.setText(post.getPostDescription());
                likes.setText(post.getPostLikes()+"");
                comments.setText(post.getComments_count()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        database.getReference().child("Users").child(postedBy)
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
        upload_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comment comment = new Comment();
                comment.setCommentBody(write_comment.getText().toString());
                comment.setCommentedAt(new Date().getTime());
                comment.setCommentedBy(auth.getUid());

                database.getReference().child("posts").child(postId).child("comments").push().setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("posts").child(postId).child("comments_count").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int comments_count = 0;
                                if (snapshot.exists()){
                                    comments_count = snapshot.getValue(Integer.class);
                                }
                                database.getReference().child("posts").child(postId).child("comments_count").setValue(comments_count+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        write_comment.setText("");
                                        Toast.makeText(CommentActivity.this, "Comment added successfully.", Toast.LENGTH_SHORT).show();

                                        Notification notification = new Notification();
                                        notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                        notification.setNotificationAt(new Date().getTime());
                                        notification.setPostId(postId);
                                        notification.setPostedBy(postedBy);
                                        notification.setType("comment");

                                        FirebaseDatabase.getInstance().getReference().child("notification")
                                                .child(postedBy).push().setValue(notification);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
        });
        commentsAdapter adapter = new commentsAdapter(list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        comments_rv.setLayoutManager(linearLayoutManager);
        comments_rv.setAdapter(adapter);

        database.getReference().child("posts").child(postId).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    list.add(comment);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
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