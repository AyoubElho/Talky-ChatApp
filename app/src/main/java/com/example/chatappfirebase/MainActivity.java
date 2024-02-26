package com.example.chatappfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatappfirebase.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import kotlin.jvm.internal.markers.KMutableMap;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding bind;
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mAuth = FirebaseAuth.getInstance();
        bind.iHaveAlreadyAnAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Login.class);
                startActivity(i);
            }
        });
        bind.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String usename = bind.username.getText().toString();
                String email = bind.editTextTextEmailAddress.getText().toString().trim();
                String pass = bind.editTextTextPassword.getText().toString().trim();
                if (email.isEmpty()) {
                    Snackbar.make(bind.textView, "Email is required!", Toast.LENGTH_SHORT).show();
                }
                if (pass.isEmpty()) {
                    Snackbar.make(bind.textView, "Password is required!", Toast.LENGTH_SHORT).show();
                }

                if (!email.isEmpty() && !pass.isEmpty()) {
                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                saveUserToDatabase(user.getUid(), usename, "default"); // You can pass the user image URL here

                                            }

                                            Intent i = new Intent(getBaseContext(), Login.class);
                                            i.putExtra("user", email);
                                            i.putExtra("pass", pass);
                                            startActivity(i);
                                            finish();
                                            bind.editTextTextEmailAddress.setText("");
                                            bind.editTextTextPassword.setText("");
                                            Snackbar.make(bind.textView, "User registered successfuly. Please verify your email id.", Snackbar.LENGTH_LONG).show();


                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(bind.textView, "" + e.getMessage(), Snackbar.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(bind.textView, "" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void saveUserToDatabase(String userId, String username, String imageUser) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        User user = new User(userId, username, imageUser);
        userRef.setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "added", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to save user data
                            Toast.makeText(MainActivity.this, "Failed to save user data.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




}