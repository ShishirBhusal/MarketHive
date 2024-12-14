package com.example.iconnect;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iconnect.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    Button signin;
    TextView GoLogIn;
    EditText email,password,name,profession;
    CheckBox adminCheckbox;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signin = findViewById(R.id.signin);
        GoLogIn = findViewById(R.id.GoLogIn);
        email = findViewById(R.id.email);
        password= findViewById(R.id.password);
        name= findViewById(R.id.name);
        profession= findViewById(R.id.profession);
        adminCheckbox = findViewById(R.id.AdmincheckBox2);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        adminCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adminCheckbox.isChecked()){
                }
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String passwordnew = password.getText().toString();
                            if(adminCheckbox.isChecked()){
                                passwordnew =  password.getText().toString()+"Admin";
                                Toast.makeText(SignInActivity.this, ""+passwordnew, Toast.LENGTH_SHORT).show();

                            }

                            User user = new User(name.getText().toString(),profession.getText().toString(),
                                    email.getText().toString(),passwordnew,adminCheckbox.isChecked());
                            String id = task.getResult().getUser().getUid();
                            database.getReference().child("Users").child(id).setValue(user);
                            Toast.makeText(SignInActivity.this, "Signing in...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(SignInActivity.this, "Some error occurred!!Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        GoLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });
    }
}