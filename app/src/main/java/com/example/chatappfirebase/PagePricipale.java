package com.example.chatappfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappfirebase.databinding.ActivityPagePricipaleBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagePricipale extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private ActivityPagePricipaleBinding binding;
    private ArrayList<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use View Binding to set the content view
        binding = ActivityPagePricipaleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

        if(item.getItemId() == R.id.lougout){
            FirebaseAuth.getInstance().signOut();
            Intent s =new Intent(PagePricipale.this ,Login.class);
            startActivity(s);
        }if (item.getItemId() == R.id.profile) {
            Intent s =new Intent(PagePricipale.this ,Profil.class);
            startActivity(s);
        }
            return false;
    }
});
        // Use a more descriptive variable name
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    binding.toolbar.setTitle(user.getUsername());
                }

                // Use Glide to load images asynchronously
                if (user.getImageUser().equals("default")) {
                    binding.toolbar.setNavigationIcon(R.drawable.account_circle);
                } else {
//                    Glide.with(getBaseContext()).load(user.getImageUser()).into(binding.toolbar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database errors
                Toast.makeText(PagePricipale.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    User user = snap.getValue(User.class);
                    String userId = user.getId();
                    String firebaseUserId = firebaseUser.getUid();
                    
                    if (userId != null && firebaseUserId != null && !userId.equalsIgnoreCase(firebaseUserId)) {
                        userList.add(user);
                    }
                }
                RecycleAdapter adapter = new RecycleAdapter(getBaseContext(), userList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PagePricipale.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void statut(String statut){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        Map<String, Object> update = new HashMap<>();
        update.put("statut", statut);
        reference.updateChildren(update);
    }

    @Override
    protected void onResume() {
        super.onResume();
        statut("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        statut("Offline");
    }
}
