package com.example.chatappfirebase;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatappfirebase.databinding.ActivityProfilBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.BaseProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Profil extends AppCompatActivity {
    ActivityProfilBinding bind;
    FirebaseUser firebaseUser;
    Uri imgUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);


        bind = ActivityProfilBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        bind.circularProgressIndicator.setVisibility(View.INVISIBLE);

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null) {
                            bind.imageView2.setImageURI(result);
                            imgUri = result;

                        } else {
                            Toast.makeText(Profil.this, "Image selection canceled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    String img = user.getImageUser();
                    if (img == null || img.equals("default")) {
                        bind.imageView2.setImageResource(R.drawable.pic);
                    } else {
                            Glide.with(getApplicationContext()).load(user.getImageUser()).into(bind.imageView2);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });


        bind.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityResultLauncher.launch("image/*");
            }
        });
        bind.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Profil.this);

                // Inflate the custom layout
                View customLayout = getLayoutInflater().inflate(R.layout.dialogue, null);
                builder.setView(customLayout);
                ImageView img = customLayout.findViewById(R.id.imageView4);
                userReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            String name = user.getImageUser();
                            if (name.equals("default")) {
                                img.setImageResource(R.drawable.pic);
                            } else {
                                Glide.with(getApplicationContext()).load(user.getImageUser()).into(img);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        bind.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bind.circularProgressIndicator.setVisibility(View.VISIBLE);
                StorageReference reference = FirebaseStorage.getInstance().getReference("images/" + UUID.randomUUID().toString());
                reference.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    userReference.child("imageUser").setValue(uri.toString());
                                    bind.circularProgressIndicator.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Profil.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();

                                }
                            });
                        } else {
                            Toast.makeText(Profil.this, "failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }
}