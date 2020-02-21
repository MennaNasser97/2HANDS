package com.example.a2hands;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a2hands.homePackage.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileimage;
    TextView hisname,userstatus;
    EditText message;
    ImageButton sendButton;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersdbref;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    ValueEventListener seenListner;
    DatabaseReference userRefForseen;

    List<Chat> chatList;
    adapterChat adapterChat;

   public String hisUid;
    String myUid;
    String hisImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar=findViewById(R.id.toolbar);

        recyclerView=findViewById(R.id.chatrecycleview);
        profileimage=findViewById(R.id.profile_Image);
        hisname=findViewById(R.id.HisName);
        userstatus=findViewById(R.id.userstatus);
        message=findViewById(R.id.messageEdit);
        sendButton=findViewById(R.id.sendbutton);


        final Intent intent =getIntent();
        hisUid=intent.getStringExtra("hisUid");


        firebaseAuth =FirebaseAuth.getInstance();
        myUid=firebaseAuth.getCurrentUser().getUid();
        firebaseDatabase =FirebaseDatabase.getInstance();
        usersdbref =firebaseDatabase.getReference("users");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        db = FirebaseFirestore.getInstance();
        loadhisinfo();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Message=message.getText().toString().trim();
                if(TextUtils.isEmpty(Message)){
                    Toast.makeText(ChatActivity.this, "Cannot send the empty message...", Toast.LENGTH_SHORT).show();
                }else {
                    sendMessage(Message);
                }
            }
        });

        readmessage();
        seenmassege();

    }

    private void seenmassege() {
        userRefForseen =FirebaseDatabase.getInstance().getReference("Chats");
        seenListner =userRefForseen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Chat chat= ds.getValue(Chat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String,Object> hasSeenHashmap=new HashMap<>();
                        hasSeenHashmap.put("isSeen",true);
                        ds.getRef().updateChildren(hasSeenHashmap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readmessage() {
        chatList =new ArrayList<>();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Chats");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Chat chat=ds.getValue(Chat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }

                    adapterChat =new adapterChat(ChatActivity.this,chatList,hisImage);
                    adapterChat.notifyDataSetChanged();
                    //set adapter to recyclerview
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String messagebody) {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        Calendar cal =Calendar.getInstance(Locale.ENGLISH);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEEE , dd-MMM-yyyy hh:mm:ss a");
        String dateTime =simpleDateFormat.format(cal.getTime());
        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("Sender",myUid);
        hashMap.put("Receiver",hisUid);
        hashMap.put("Message",messagebody);
        hashMap.put("Timestamp",dateTime);
        hashMap.put("isSeen",false);
        databaseReference.child("Chats").push().setValue(hashMap);

        //reset edittext after sending message
        message.setText("");
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userRefForseen.removeEventListener(seenListner);
    }

    //get other user's information
   private void loadhisinfo() {
       db.collection("users/").document(hisUid)
               .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               User user = task.getResult().toObject(User.class);
               loadPhotos(profileimage,"Profile_Pics/"+hisUid+"/"+user.profile_pic );
               hisname.setText(user.first_name+" "+user.last_name);
           }
       });
    }

    ///load other user's profileImage
    void loadPhotos(final ImageView imgV , String path){

        mStorageRef.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                hisImage=uri.toString();
                Picasso.get().load(uri.toString()).into(imgV);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser user =firebaseAuth.getCurrentUser();
        if(user !=null){
            myUid=user.getUid();
        }else {
            startActivity(new Intent(ChatActivity.this, HomeFragment.class));
            finish();
        }
    }

    //@Override
    //public boolean onCreateOptionsMenu(Menu menu) {
      //  getMenuInflater().inflate(R.menu.bottom_navigation,menu);
        //menu.findItem(R.id.searchNav);

     //   return super.onCreateOptionsMenu(menu);
    //}

    //@Override
    //public boolean onOptionsItemSelected(@NonNull MenuItem item) {
      //  return super.onOptionsItemSelected(item);
    //}
}
