package com.example.chatappfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button btn, SignUp;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        btn = findViewById(R.id.Login);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent i =new Intent(Login.this ,PagePricipale.class);
            startActivity(i);

        }


        SignUp = findViewById(R.id.acount);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(Login.this ,MainActivity.class);
                startActivity(i);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user = email.getText().toString().trim();
                String pass = password.getText().toString().trim();
                if (user.isEmpty()) {
                    Toast.makeText(Login.this, "Email is required!", Toast.LENGTH_SHORT).show();
                }
                if (pass.isEmpty()) {
                    Toast.makeText(Login.this, "Password is required!", Toast.LENGTH_SHORT).show();
                }
                if (!user.isEmpty() && !pass.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                    Intent is = new Intent(Login.this, PagePricipale.class);
                                    is.putExtra("email", user);
                                    startActivity(is);
                                    finish();
                                    Snackbar.make(btn, "Login succuesfuly", Snackbar.LENGTH_LONG).show();

                                } else {
                                    Snackbar.make(btn, "The email is not verified", Snackbar.LENGTH_LONG).show();
                                }

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(btn, "" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });


    }
}