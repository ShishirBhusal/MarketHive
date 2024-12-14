package com.example.iconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iconnect.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.URL;

public class IncomingCallActivity extends AppCompatActivity {
TextView name,profession,typeOfCall;
ImageView profile,decline,accept;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        name = findViewById(R.id.name);
        profession = findViewById(R.id.profession);
        profile = findViewById(R.id.profile);
        typeOfCall = findViewById(R.id.type);
        decline = findViewById(R.id.decline);
        accept = findViewById(R.id.accept);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("senderId");
        String type = intent.getStringExtra("type");
        Log.d("IncomingCallActivity", "userId in incoming call activity: " + userId);

        DatabaseReference Call_ref = FirebaseDatabase.getInstance().getReference().child("chats").child(userId+FirebaseAuth.getInstance().getUid()).child("response");
        Log.d("IncomingCallActivity", "userId+FirebaseAuth.getInstance().getUid() = " + userId+FirebaseAuth.getInstance().getUid());

        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("IncomingCallActivity", "DataSnapshot: " + snapshot.getValue());

                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    Picasso.get()
                            .load(user.getProfile())
                            .placeholder(R.drawable.profile_placeholder)
                            .into(profile);
                    name.setText(user.getName());
                    profession.setText(user.getProfession());
                } else {
                    Log.e("IncomingCallActivity", "User object is null");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if (type.equals("Video")){
            typeOfCall.setText("Video call");
        }else{
            typeOfCall.setText("Audio call");
        }
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call_ref.setValue("declined");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Call_ref.removeValue();
                    }
                },3000);
                finish();
            }
        });

        String key = userId+FirebaseAuth.getInstance().getUid();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call_ref.setValue("accepted");
                joinMeeting(key);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Call_ref.removeValue();
                    }
                },3000);
            }

        });


    }
    private void joinMeeting(String key) {
        try {
            JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
            builder.setServerURL(new URL("https://meet.jit.si"));
            builder.setRoom(key);
            if (typeOfCall.equals("audio")){
                builder.setVideoMuted(true);
            }
            builder.build();

            JitsiMeetActivity.launch(IncomingCallActivity.this,builder.build());
            finish();
        }catch (Exception e){
            Toast.makeText(IncomingCallActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}