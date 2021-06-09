package com.astro4callapp.astro4call.chats;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.astro4callapp.astro4call.DbHelper;
import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.UpdateWallet;
import com.astro4callapp.astro4call.utilities.Constants;
import com.astro4callapp.astro4call.utilities.PreferenceManager;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatDetails extends AppCompatActivity implements UpdateWallet {

    FirebaseDatabase database;
    RecyclerView recyclerView_chat;
    FirebaseAuth auth;
    ImageView send_message;
    EditText et_sendMessage;
    TextView tv_chatStatus, tv_timer;

    CircleImageView profileImg;
    TextView tv_ProfileName;
    private String reciveId, astroKey;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;


    // timer variable

    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;
    boolean timerStarted = false;
    private String charge;
    private DbHelper helper;


    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat_details );

        tv_chatStatus = findViewById( R.id.tv_chat_status );

        profileImg = findViewById( R.id.chat_profile );
        tv_ProfileName = findViewById( R.id.tv_chat_name );
        tv_timer = findViewById( R.id.chat_tv_timer );

        helper = new DbHelper( getApplicationContext() );

        preferenceManager = new PreferenceManager( getApplicationContext() );

        rootNode = FirebaseDatabase.getInstance();
//        reference = rootNode.getReference().child( "Astrologers" ).child( preferenceManager.getString( Constants.KEY_USER_REG_ID1_ASTRO ) );


        et_sendMessage = findViewById( R.id.et_sendmessage );
        recyclerView_chat = findViewById( R.id.chatRecyclerView );
        send_message = findViewById( R.id.sent_message );

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        final String senderId = auth.getUid();

        astroKey = getIntent().getStringExtra( "astroKey" );

        if (astroKey == null) {
            astroKey = preferenceManager.getString( Constants.KEY_USER_REG_ID1_ASTRO );
            reference = rootNode.getReference().child( "Astrologers" ).child( preferenceManager.getString( Constants.KEY_USER_REG_ID1_ASTRO ) );

        } else {
            astroKey = getIntent().getStringExtra( "astroKey" );
            reference = rootNode.getReference().child( "Astrologers" ).child( astroKey );
        }

        reciveId = getIntent().getStringExtra( "userId" );
        String userName = getIntent().getStringExtra( "astroName" );
        String profilePic = getIntent().getStringExtra( "astroImage" );


        if (astroKey != null) {
            tv_ProfileName.setText( "" + userName );
            // Picasso.get().load(profilePic).placeholder(R.drawable.welcome_img).into(profileImg);
            Glide.with( getApplicationContext() ).load( profilePic ).into( profileImg );
        }


        final ArrayList<MessagesModel> messagesModels = new ArrayList<>();
        final ChatAdapter chatAdapter = new ChatAdapter( messagesModels, this, reciveId );
        recyclerView_chat.setAdapter( chatAdapter );

        LinearLayoutManager layoutManager = new LinearLayoutManager( this );
        recyclerView_chat.setLayoutManager( layoutManager );

        // Toast.makeText( this, "" + senderId, Toast.LENGTH_SHORT ).show();


        //  final String senderRoom = senderId + reciveId;
        //  final String reciverRoom = reciveId + senderId;

        // Toast.makeText( this, ""+astroKey, Toast.LENGTH_SHORT ).show();

        database.getReference().child( "chats" ).child( astroKey )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messagesModels.clear();
                        for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                            MessagesModel model = snapshot1.getValue( MessagesModel.class );
                            model.setMessageId( snapshot1.getKey() );
                            recyclerView_chat.smoothScrollToPosition( recyclerView_chat.getAdapter().getItemCount() );
                            messagesModels.add( model );
                        }
                        chatAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );

        send_message.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = et_sendMessage.getText().toString();
                final MessagesModel model = new MessagesModel( senderId, message );
                model.setTimestamp( new Date().getTime() );
                et_sendMessage.setText( "" );

                database.getReference().child( "chats" ).child( astroKey )
                        .push()
                        .setValue( model ).addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                } );
            }
        } );


        et_sendMessage.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    updateStatus( "online" );
                } else {
                    updateStatus( "typing..." );
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );


        // Timer code
        timer = new Timer();

    }


    @Override
    protected void onStart() {
        super.onStart();
        updateStatus( "online" );


        if (!timerStarted) {
            timerStarted = true;
            startTimer();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateStatus( "online" );
    }

    Date currentTime = Calendar.getInstance().getTime();

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister all sensors
        updateWallet();

        Log.d( "date", "" + currentTime );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateStatus( "offline" );

        timerStarted = false;
        timerTask.cancel();


        // preferenceManager.updateFloat( Constants.KEY_WALLET_FLOAT_AMOUNT,deductPrice );

        //  Log.d( "update",""+deductPrice );

        // preferenceManager.putFloatVal( Constants.KEY_WALLET_FLOAT_UPDATED_AMOUNT,deductPrice );


    }


    public void updateStatus(String status) {
        reference.child( "chat_status" ).setValue( status );
        if (astroKey != null) {
            getdata();
        }
    }

    private void getdata() {
        reference.child( "chat_status" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue( String.class );

                tv_chatStatus.setText( value );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText( ChatDetails.this, "Fail to get data.", Toast.LENGTH_SHORT ).show();
            }
        } );
    }


    float deductPrice;
    float totaltimes;


    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        tv_timer.setText( getTimerText() );

                    }
                } );
            }

        };
        timer.scheduleAtFixedRate( timerTask, 0, 1000 );
    }

    private String getTimerText() {
        int rounded = (int) Math.round( time );

        int seconds = ((rounded % 86400) % 3600) % 60;
        Log.d( "secondscheck", "" + seconds );
        int minutes = ((rounded % 86400) % 3600) / 60;
        Log.d( "secondscheck", "" + minutes );
        int hours = ((rounded % 86400) / 3600);
        Log.d( "secondscheck", "" + hours );

        //   int totalTime = seconds + minutes * 60 + hours * 3600;

        Long totaltime = Long.valueOf( (seconds + minutes * 60 + hours * 3600) );
        Log.d( "totaltime", "" + totaltime );


        float chargeprice = 0.0f;
        charge = getIntent().getStringExtra( "charge" );
        chargeprice = Float.parseFloat( charge );
        float secondcharge = chargeprice / 60;


        totaltimes = (totaltime * secondcharge);
        Log.d( "totaltimessss", "" + totaltimes );


        float walletPrice = preferenceManager.getFloat( Constants.KEY_WALLET_AMOUNT );
//
//        Log.d( "finalPrice",""+finalPrice );
//


        Log.d( "dedcut", "" + deductPrice );

        if (deductPrice >= 0) {
            deductPrice = walletPrice - totaltimes;
        } else {
            onBackPressed();
        }
//

        getTime( seconds, minutes, hours );

        return formatTime( seconds, minutes, hours );


    }

    private void getTime(int seconds, int minutes, int hours) {

        Boolean check = helper.isInsert( "" + currentTime,  String.format( "%02d", hours ), String.format( "%02d", minutes ), "" +String.format( "%02d", seconds ) );

        if (check == true) {
            Toast.makeText( this, "Inserted", Toast.LENGTH_SHORT ).show();
        }
    }


    private String formatTime(int seconds, int minutes, int hours) {
        return String.format( "%02d", hours ) + " : " + String.format( "%02d", minutes ) + " : " + String.format( "%02d", seconds );
    }


    @Override
    public void updateWallet() {
        preferenceManager.updateFloat( Constants.KEY_WALLET_FLOAT_UPDATED_AMOUNT, deductPrice );
        Log.d( "getvalue", "" + preferenceManager.getFloat( Constants.KEY_WALLET_FLOAT_UPDATED_AMOUNT ) );
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}