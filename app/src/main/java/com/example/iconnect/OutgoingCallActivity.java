package com.example.iconnect;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.iconnect.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;


public class OutgoingCallActivity extends AppCompatActivity {
    TextView name, profession, typeOfCall;
    ImageView profile, decline;
    String userId, type;
    String userName;
    private String token;
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_call);
        name = findViewById(R.id.name);
        profession = findViewById(R.id.profession);
        profile = findViewById(R.id.profile);
        typeOfCall = findViewById(R.id.type);
        decline = findViewById(R.id.decline);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        type = intent.getStringExtra("type");


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get the new FCM token
                    String token = task.getResult();
                    Log.d("FCM", "FCM Token: " + token);
                });




    // Submit the task to fetch access token
        executorService.execute(() -> {
            try {
                AccessToken accessToken = new AccessToken();
                token = accessToken.getAccessToken(); // Replace with your token-fetching logic
                Log.e(TAG, "AccessToken is: " + token);

                // Call sendInvitation() here after the token is fetched
                runOnUiThread(() -> {
                    if (type.equals("video")) {
                        typeOfCall.setText("Video call");
                        sendInvitation("video", FirebaseAuth.getInstance().getUid(), token);
                        checkResponse();
                    } else {
                        typeOfCall.setText("Audio call");
                        sendInvitation("audio", FirebaseAuth.getInstance().getUid(), token);
                        checkResponse();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error fetching AccessToken: " + e.getLocalizedMessage());
                runOnUiThread(() -> Toast.makeText(this, "Failed to fetch access token", Toast.LENGTH_SHORT).show());
            }
        });

// Shut down the ExecutorService after execution
        executorService.shutdown();


        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get()
                        .load(user.getProfile())
                        .placeholder(R.drawable.profile_placeholder)
                        .into(profile);
                name.setText(user.getName());
                userName = user.getName();
                profession.setText(user.getProfession());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void checkResponse() {
        FirebaseDatabase.getInstance().getReference().child("chats").child(FirebaseAuth.getInstance().getUid() + userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild("response")) {
                    String response = snapshot.child("response").getValue(String.class);
                    if (response.equals("accepted")) {
                        Toast.makeText(OutgoingCallActivity.this, "call accepted", Toast.LENGTH_SHORT).show();
                        joinMeeting(FirebaseAuth.getInstance().getUid() + userId);
                    } else if (response.equals("declined")) {
                        Toast.makeText(OutgoingCallActivity.this, "call declined", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            private void joinMeeting(String Key) {
                try {

                    String moderatorToken = JwtTokenGenerator.generateModeratorToken(userId, userName,Key);

                    JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                    builder.setServerURL(new URL("https://8x8.vc"));
                    builder.setRoom(Key);
                    builder.setToken(moderatorToken);
                    if (type.equals("audio")) {
                        builder.setVideoMuted(true);
                    }
                    builder.build();

                    JitsiMeetActivity.launch(OutgoingCallActivity.this, builder.build());
                    finish();
                } catch (Exception e) {
                    Toast.makeText(OutgoingCallActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: " + e.getLocalizedMessage());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void sendInvitation(String Type, String uid, String token) {
        try {
            if (token == null || token.isEmpty()) {
                Toast.makeText(this, "Invalid FCM token", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = getIntent();
            String receiver_token = intent.getStringExtra("receiver_token");


            // FCM v1 URL
            String url = "https://fcm.googleapis.com/v1/projects/iconnect-1b66d/messages:send";

            // Create the notification payload
            JSONObject message = new JSONObject();
            JSONObject notification = new JSONObject();
            notification.put("title", Type.equals("video") ? "Video" : "Audio");
            notification.put("body", uid);

            JSONObject messageContent = new JSONObject();
            messageContent.put("token", receiver_token); // Receiver's FCM token
            messageContent.put("notification", notification);

            message.put("message", messageContent);

            // Set up the Volley request
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(url, message,
                    response -> Toast.makeText(OutgoingCallActivity.this, "Invitation sent successfully.", Toast.LENGTH_SHORT).show(),
                    error -> {
                        String errorBody = error.networkResponse != null ? new String(error.networkResponse.data) : "No response body";
                        int statusCode = error.networkResponse != null ? error.networkResponse.statusCode : -1;
                        Log.e("OutgoingCallActivity", "Status Code: " + statusCode + ", Error Body: " + errorBody);
                        Toast.makeText(OutgoingCallActivity.this, "Failed to send notification: " + errorBody, Toast.LENGTH_LONG).show();
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token); // Use Bearer token for authentication
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            queue.add(request);

        } catch (Exception e) {
            Toast.makeText(OutgoingCallActivity.this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.e("OutgoingCallActivity", "Exception: " + e.getLocalizedMessage());
        }
    }
}