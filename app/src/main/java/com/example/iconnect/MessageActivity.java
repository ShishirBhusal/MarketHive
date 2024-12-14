package com.example.iconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.iconnect.Adapters.messageAdapter;
import com.example.iconnect.models.User;
import com.example.iconnect.models.message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
EditText get_message;
ImageView back,send,profile,audio_call,video_call;
TextView name;
FirebaseAuth auth;
FirebaseDatabase database;
ArrayList<message> messageArrayList;
RecyclerView show_message_rv;
User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        messageArrayList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        get_message = findViewById(R.id.get_message);
        send = findViewById(R.id.send);
        back = findViewById(R.id.back);
        audio_call = findViewById(R.id.audio_call);
        video_call = findViewById(R.id.video_call);
        name = findViewById(R.id.name);
        profile = findViewById(R.id.profile);
        show_message_rv = findViewById(R.id.show_message_rv);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        video_call.setVisibility(View.GONE);
        String sendGroup = auth.getUid()+userId;
        String receiveGroup = userId+auth.getUid();


        database.getReference().child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                Picasso.get()
                        .load(user.getProfile())
                        .placeholder(R.drawable.profile_placeholder)
                        .into(profile);
                name.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageBody = get_message.getText().toString();
                get_message.setText("");

                message msg = new message();
                msg.setMessageBody(messageBody);
                msg.setMessageBy(auth.getUid());

                String randomKey = database.getReference().push().getKey();

                if (messageBody.equals("")){
                    Toast.makeText(MessageActivity.this, "You first have to write message!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    database.getReference().child("chats").child(sendGroup).child("messages").child(randomKey).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            database.getReference().child("chats").child(receiveGroup).child("messages").child(randomKey).setValue(msg);
                            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user1 = snapshot.getValue(User.class);
                                    sendMessage(user1.getName(),msg.getMessageBody(),user.getToken());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                    HashMap<String, Object> lastMsgObj_Sender = new HashMap<>();
                    lastMsgObj_Sender.put("lastMsg",msg.getMessageBody());
                    lastMsgObj_Sender.put("timestamp",new Date().getTime());
                    lastMsgObj_Sender.put("messageFor",userId);


                    HashMap<String, Object> lastMsgObj_Reciever = new HashMap<>();
                    lastMsgObj_Reciever.put("lastMsg",msg.getMessageBody());
                    lastMsgObj_Reciever.put("timestamp",new Date().getTime());
                    lastMsgObj_Reciever.put("messageFor",auth.getUid());
                    lastMsgObj_Reciever.put("checkOpen",false);

                    database.getReference().child("chats").child(sendGroup).updateChildren(lastMsgObj_Sender);
                    database.getReference().child("chats").child(receiveGroup).updateChildren(lastMsgObj_Reciever);

                }
            }

        });


        messageAdapter adapter = new messageAdapter(sendGroup,receiveGroup,messageArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessageActivity.this,LinearLayoutManager.VERTICAL,false);
        show_message_rv.setLayoutManager(linearLayoutManager);
        show_message_rv.setAdapter(adapter);

        database.getReference().child("chats").child(sendGroup).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        message message1 = snapshot1.getValue(message.class);
                        message1.setMesageId(snapshot1.getKey());
                        messageArrayList.add(message1);
                    }
                    adapter.notifyDataSetChanged();
                    show_message_rv.smoothScrollToPosition(show_message_rv.getAdapter().getItemCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        audio_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(MessageActivity.this,OutgoingCallActivity.class);
                intent1.putExtra("userId",userId);
                intent1.putExtra("receiver_token",user.getToken());

                intent1.putExtra("type","audio");
                startActivity(intent1);
            }
        });

        video_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(MessageActivity.this,OutgoingCallActivity.class);
                intent1.putExtra("userId",userId);
                intent1.putExtra("receiver_token",user.getToken());

                intent1.putExtra("type","video");
                startActivity(intent1);
            }
        });
    }
    private void sendMessage(String MessagedBy, String MessageBody, String token) {
        try{
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title", MessagedBy);
            data.put("body", MessageBody);

            JSONObject notificationData = new JSONObject();
            notificationData.put("notification", data);
            notificationData.put("to", token);

            JsonObjectRequest request = new JsonObjectRequest(url, notificationData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                 }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String,String> map = new HashMap<>();
                    String key = "Key=AAAAhCyQGAQ:APA91bHfaAPlVDNNX69Mi24rMm4r5MfTE-_D6Pl3R1oq0TrOAt5VuBqSip-Ouq8Urpak_wTmjkq1HBO7240b1Fgu8x-PPyTK9PiK16wJnOgju2AY9sT0HXbhbpwMrz1if9BNiTs9P8Rl";
                    map.put("Authorization",key);
                    map.put("Content-Type","application/json");
                    return map;
                }
            };
            queue.add(request);
        }

        catch (Exception e){
            Toast.makeText(MessageActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
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