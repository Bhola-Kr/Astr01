package com.astro4callapp.astro4call.chats;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.astro4callapp.astro4call.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetails extends AppCompatActivity {

    FirebaseDatabase database;
    RecyclerView recyclerView_chat;
    FirebaseAuth auth;
    ImageView send_message;
    EditText et_sendMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);

        et_sendMessage=findViewById(R.id.et_sendmessage);
        recyclerView_chat=findViewById(R.id.chatRecyclerView);
        send_message=findViewById(R.id.sent_message);

        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();


        final String senderId=auth.getUid();
        String  reciveId=getIntent().getStringExtra("userId");
//        String  userName=getIntent().getStringExtra("userName");
//        String  profilePic=getIntent().getStringExtra("profilePic");



        final ArrayList<MessagesModel> messagesModels=new ArrayList<>();
        final ChatAdapter chatAdapter=new ChatAdapter(messagesModels,this,reciveId);
        recyclerView_chat.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView_chat.setLayoutManager(layoutManager);

        final String senderRoom=senderId+reciveId;
        final String reciverRoom=reciveId+senderId;

        database.getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messagesModels.clear();
                        for (DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                            MessagesModel model=snapshot1.getValue(MessagesModel.class);
                            model.setMessageId(snapshot1.getKey());
                            recyclerView_chat.smoothScrollToPosition(recyclerView_chat.getAdapter().getItemCount());
                            messagesModels.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




        send_message.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String message  = et_sendMessage.getText().toString();
                final MessagesModel model=new MessagesModel(senderId,message);
                model.setTimestamp(new Date().getTime());
                et_sendMessage.setText("");

                database.getReference().child("chats")
                        .child(senderRoom)
                        .push()
                        .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        database.getReference().child("chats")
                                .child(reciverRoom)
                                .push()
                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                    }
                });
            }
        });
    }
}