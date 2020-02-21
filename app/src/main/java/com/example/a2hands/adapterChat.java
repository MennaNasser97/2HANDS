package com.example.a2hands;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class adapterChat extends RecyclerView.Adapter<adapterChat.MyHolder>{

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;

    Context context;
    List<Chat> chatList;
    String imageURI;

    FirebaseUser user;


    public adapterChat(Context context, List<Chat> chatList, String imageURI) {
        this.context = context;
        this.chatList = chatList;
        this.imageURI = imageURI;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
            return new MyHolder(view);
        }else {
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String message = chatList.get(position).getMessage();
        String timestamp = chatList.get(position).getTimestamp();
        holder.message.setText(message);
        holder.Time.setText(timestamp);
        try{
            Picasso.get().load(imageURI).into(holder.otherProfileImage);
        }
        catch (Exception e){

        }
        if(position==chatList.size()-1){
            if ((chatList.get(position).isSeen)){
                holder.isSeen.setText("seen");
            }else {
                holder.isSeen.setText("Delivered");
            }
        }else {
            holder.isSeen.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        user= FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(user.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder{


        ImageView otherProfileImage;
        TextView message,Time,isSeen;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            otherProfileImage =itemView.findViewById(R.id.profileIv);
            message =itemView.findViewById(R.id.messageTv);
            Time =itemView.findViewById(R.id.messageTime);
            isSeen =itemView.findViewById(R.id.isSeen);
        }
    }
}

