package com.astro4callapp.astro4call.chats;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.astro4callapp.astro4call.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter{

    ArrayList<MessagesModel> messagesModels;
    Context context;
    String recvierId;

    int SENDER_VIEW_TYPE=1;
    int RECIVER_VIEW_TYPE=2;

    public ChatAdapter(ArrayList<MessagesModel> messagesModels, Context context) {
        this.messagesModels = messagesModels;
        this.context = context;

    }

    public ChatAdapter(ArrayList<MessagesModel> messagesModels, Context context, String recvierId) {
        this.messagesModels = messagesModels;
        this.context = context;
        this.recvierId = recvierId;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==SENDER_VIEW_TYPE){
            View view= LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewHolder(view);
        }else {
            View view= LayoutInflater.from(context).inflate(R.layout.sample_reciver,parent,false);
            return new ReciverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if(messagesModels.get(position).getUid().equals(FirebaseAuth.getInstance().getUid())){
           return SENDER_VIEW_TYPE;
        }else {
            return RECIVER_VIEW_TYPE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final MessagesModel messagesModel=messagesModels.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure want to delete this message for both ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
                                String sendeRoom= FirebaseAuth.getInstance().getUid()+recvierId;
                                firebaseDatabase.getReference().child("chats").child(sendeRoom)
                                        .child(messagesModel.getMessageId())
                                        .setValue(null);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
                return false;
            }
        });

        if(holder.getClass()==SenderViewHolder.class){
            ((SenderViewHolder)holder).senderMesage.setText(messagesModel.getMessage());
        }else {
            ((ReciverViewHolder)holder).reciverMesage.setText(messagesModel.getMessage());
        }

    }


    @Override
    public int getItemCount() {
        return messagesModels.size();
    }

    public class ReciverViewHolder extends RecyclerView.ViewHolder{

        TextView reciverMesage,reciverTime;

        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);
            reciverMesage=itemView.findViewById(R.id.reciver_tv);
            reciverTime=itemView.findViewById(R.id.reciver_tv_time);
        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView senderMesage,senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMesage=itemView.findViewById(R.id.tv_sender_msg);
            senderTime=itemView.findViewById(R.id.tv_sender_time);

        }
    }
}
