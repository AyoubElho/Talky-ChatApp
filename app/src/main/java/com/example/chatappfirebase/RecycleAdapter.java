package com.example.chatappfirebase;// UserAdapter.java

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatappfirebase.User;

import java.util.ArrayList;
import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

    Context context;
    private ArrayList<User> userList;


    public RecycleAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleAdapter.ViewHolder holder, int position) {
        if(userList.get(position).getStatut().equals("Online")){
            holder.statut.setImageResource(R.drawable.online);
        }else{
            holder.statut.setImageResource(R.drawable.offline);
        }

        holder.textViewusername.setText(userList.get(position).username);


        if (userList.get(position).getImageUser().equals("default")) {
            holder.imageView.setImageResource(R.drawable.pic);
        } else {
            Glide.with(context).load(userList.get(position).getImageUser()).into(holder.imageView);
        }
        holder.conts.setOnClickListener(v -> {
            Intent intent = new Intent(context, Conversation.class);
            String value = userList.get(position).getId();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("id" ,value);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewusername;
        LinearLayout conts;
        ImageView statut;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            conts = itemView.findViewById(R.id.linear);
            imageView = itemView.findViewById(R.id.imageView);
            textViewusername = itemView.findViewById(R.id.textViewUsername);
            statut =itemView.findViewById(R.id.online);

        }
    }

}
