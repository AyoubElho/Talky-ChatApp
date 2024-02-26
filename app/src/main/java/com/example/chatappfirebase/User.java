package com.example.chatappfirebase;

import com.google.firebase.database.PropertyName;

public class User {
    public String id;
    public String username;
    public String imageUser;
    public String statut;
    public boolean typing;

    // Constructor

    public User() {


    }
    public User(boolean typing){
        this.typing= typing;
    }

    public User(String statut){
        this.statut = statut;

    }
    public User(String id, String username, String imageUser) {
        this.id = id;
        this.username = username;
        this.imageUser = imageUser;
    }

    // Getter methods
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUser() {
        return imageUser;
    }

    public String getStatut() {
        return statut;
    }

    public boolean isTyping() {
        return typing;
    }
}

