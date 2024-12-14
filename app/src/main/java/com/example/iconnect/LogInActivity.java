package com.example.iconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {
    FirebaseAuth auth;
    EditText email,password;
    FirebaseUser currentuser;
    CheckBox adminCheckbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Button login = findViewById(R.id.login);
        TextView signin = findViewById(R.id.GoSignIn);
        email = findViewById(R.id.email);
        password= findViewById(R.id.password);
        adminCheckbox = findViewById(R.id.AdmincheckBox);

        auth = FirebaseAuth.getInstance();
        currentuser = auth.getCurrentUser();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAdmin = adminCheckbox.isChecked();
                String passwordnew = password.getText().toString();
                if(isAdmin){
                    passwordnew =  password.getText().toString()+"Admin";

                }
                Toast.makeText(LogInActivity.this, "pass:"+passwordnew, Toast.LENGTH_SHORT).show();

                Toast.makeText(LogInActivity.this, "login clicked", Toast.LENGTH_SHORT).show();


                if (email.getText().toString().isEmpty() || passwordnew.isEmpty()) {
                    Toast.makeText(LogInActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }


                auth.signInWithEmailAndPassword(email.getText().toString(),passwordnew).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LogInActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Toast.makeText(LogInActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                            Toast.makeText(LogInActivity.this, "Some error occurred!!Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentuser!=null){
            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}