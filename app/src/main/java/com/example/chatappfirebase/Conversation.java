package com.example.chatappfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatappfirebase.databinding.ActivityConversationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Conversation extends AppCompatActivity {
    FirebaseUser firebaseUser;
    ActivityConversationBinding bind;
    ArrayList<Chat> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        bind = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        Intent i = getIntent();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(i.getStringExtra("id"));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    bind.nameUser.setText(user.username);

                    if (user.getImageUser() != null && user.getImageUser().equals("default")) {
                        bind.userImgConversation.setImageResource(R.drawable.account_circle);
                    } else {
                        Glide.with(getBaseContext()).load(user.getImageUser()).into(bind.userImgConversation);

                    }
                } else {
                    Toast.makeText(Conversation.this, "null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        bind.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bind.messageInput.getText().toString();
                send_msg(firebaseUser.getUid(), i.getStringExtra("id"), bind.messageInput.getText().toString().trim());
                bind.messageInput.setText("");

            }
        });

        RecyclerView recyclerView = findViewById(R.id.chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messages = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference referenced = FirebaseDatabase.getInstance().getReference("chat");

        bind.messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    typing(false);
                }
                if (count > 0) {
                    typing(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        referenced.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat != null && ((chat.getReceiver().equals(i.getStringExtra("id")) && chat.getSender().equals(firebaseUser.getUid())) ||
                            (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(i.getStringExtra("id"))))) {
                        messages.add(chat);
                    }
                }


                ChatAdapter adapter = new ChatAdapter(Conversation.this, messages);
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(messages.size() - 1);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    if (user.getStatut().equals("Online")) {
                        if (user.isTyping()) {
                            bind.typingOnline.setText("typing....");
                        } else {
                            bind.typingOnline.setText("online");
                        }

                    } else {

                    }
                } else {
                    Toast.makeText(Conversation.this, "null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        bind.tolls.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getBaseContext() ,PagePricipale.class);
                startActivity(i);
            }
        });
    }


    public void send_msg(String sender, String receiver, String message) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("chat");
        if (message.length() == 0) {

        } else {
            Chat chat = new Chat(sender, receiver, message);
            userRef.child(userRef.push().getKey()).setValue(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                }
            });
        }


    }

    public void typing(Boolean typing) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        Map<String, Object> update = new HashMap<>();
        update.put("typing", typing);
        reference.updateChildren(update);
    }

    public void statut(String statut) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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