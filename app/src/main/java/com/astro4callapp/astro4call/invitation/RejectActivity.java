package com.astro4callapp.astro4call.invitation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.audio_video.AudioActivity;
import com.astro4callapp.astro4call.audio_video.VideoCallingActivity;
import com.astro4callapp.astro4call.network.ApiClient;
import com.astro4callapp.astro4call.network.ApiService;
import com.astro4callapp.astro4call.utilities.Constants;
import com.astro4callapp.astro4call.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class RejectActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private String inviterToken = null;
    private String meetingRoom = null;
    private String meetingType = null;
    private MediaPlayer player;
    private String reciverToken = null;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_reject );


        /*--          get current user phone number -----*/
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String phoneNumber = user.getPhoneNumber();
//        Toast.makeText(this, ""+phoneNumber, Toast.LENGTH_LONG).show();

        preferenceManager = new PreferenceManager( getApplicationContext() );

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child( "Users" ).child(preferenceManager.getString( Constants.KEY_USER_REG_ID1 ));

        ImageView imageViewMeetingType = findViewById( R.id.out_imageMeetingType );
        TextView textFirstChar = findViewById( R.id.text_invit_send_firstChar );
        TextView textName = findViewById( R.id.text_invit_send_UserName );
        TextView texMeeting = findViewById( R.id.textsendingMeetingInvition1 );
        // TextView textEmail = findViewById(R.id.textInvit_sind_Email);

        Intent intent = getIntent();
        String name = intent.getStringExtra( "name" );
        reciverToken = intent.getStringExtra( "token" );

        meetingType = getIntent().getStringExtra( "type" );

        textName.setText( name );
        textFirstChar.setText( name.substring( 0, 1 ) );


        if (meetingType != null) {
            if (meetingType.equals( "call" )) {
                texMeeting.setText( "Phone Calling.." );
                imageViewMeetingType.setImageResource( R.drawable.telephone1 );
            } else {
                texMeeting.setText( "Video Calling.." );
                imageViewMeetingType.setImageResource( R.drawable.video );
            }
        }


        ImageView stopInvitation = findViewById( R.id.reject_send_meeting_iv );

        stopInvitation.setOnClickListener( v->{
            if (meetingType != null) {
                cancelledInvitation( reciverToken );
            }
        } );


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener( task->{
                    if (!task.isSuccessful()) {
                        Log.w( TAG, "Fetching FCM registration token failed", task.getException() );
                        return;
                    }
                    inviterToken = task.getResult();
                    initiateMeeting( meetingType, reciverToken );
                } );
    }


    private void initiateMeeting(String meetingType, String reciverToken) {

        try {

            JSONArray tokens = new JSONArray();
            tokens.put( reciverToken );

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put( Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION );
            data.put( Constants.REMOTE_MSG_MEETING_TYPE, meetingType );
            data.put( Constants.KEY_FIRST_NAME, preferenceManager.getString( Constants.KEY_FIRST_NAME ) );
            data.put( Constants.REMOTE_MSG_INVITOR_TOKEN, inviterToken );

            meetingRoom = preferenceManager.getString( Constants.KEY_USER_ID ) + " " +
                    UUID.randomUUID().toString().substring( 0, 5 );

            data.put( Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom );

            body.put( Constants.REMOTE_MSG_DATA, data );
            body.put( Constants.REMOTE_MSG_REGISTRATION_IDS, tokens );

            sendRemoteMessage( body.toString(), Constants.REMOTE_MSG_INVITATION );


        } catch (Exception exception) {
            Toast.makeText( this, "Error" + exception.getMessage(), Toast.LENGTH_LONG ).show();
            finish();
        }


    }


    private void sendRemoteMessage(String remoteMessageBody, String type) {

        ApiClient.getClient().create( ApiService.class ).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue( new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {

                    if (type.equals( Constants.REMOTE_MSG_INVITATION )) {
                        //   Toast.makeText(OutGoingMeetingInvitation.this, "Invitation sent successful..", Toast.LENGTH_SHORT).show();
                    } else if (type.equals( Constants.REMOTE_MSG_INITIATION_RESPONSE )) {
                        //    Toast.makeText(OutGoingMeetingInvitation.this, "Call cancelled..", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } else {
                    // error
                    //  Toast.makeText(OutGoingMeetingInvitation.this, "Response error" + response.message(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                //Toast.makeText(OutGoingMeetingInvitation.this, "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        } );
        startPlayer();
    }


    private void cancelledInvitation(String reciverToken) {

        try {
            JSONArray token = new JSONArray();
            token.put( reciverToken );

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put( Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INITIATION_RESPONSE );
            data.put( Constants.REMOTE_MSG_INITIATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELLED );

            body.put( Constants.REMOTE_MSG_DATA, data );
            body.put( Constants.REMOTE_MSG_REGISTRATION_IDS, token );

            sendRemoteMessage( body.toString(), Constants.REMOTE_MSG_INITIATION_RESPONSE );

        } catch (Exception e) {
            Toast.makeText( this, "Error " + e.getMessage(), Toast.LENGTH_SHORT ).show();
            finish();
        }
        stopPlayer();
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra( Constants.REMOTE_MSG_INITIATION_RESPONSE );

            if (type != null) {
                if (type.equals( Constants.REMOTE_MSG_INVITATION_ACCEPTED )) {
                    try {

//                        Toast.makeText( context, "", Toast.LENGTH_SHORT ).show();

                        if (type.equals( "audio" )){

                        }else {

                            startActivity( new Intent(getApplicationContext(), AudioActivity.class) );
                            finish();
                           // startActivity( new Intent(getApplicationContext(),VideoCallingActivity.class) );
                          //  finish();
                        }
                        //  URL serverUrl=new URL("https://meet.jit.si");
//                        JitsiMeetConferenceOptions.Builder builder=new JitsiMeetConferenceOptions.Builder();
//                        builder.setServerURL(serverUrl);
//                        builder.setWelcomePageEnabled(false);
//                        builder.setRoom(meetingRoom);
//                        if (meetingType.equals("audio")){
//                            builder.setVideoMuted(true);
//                        }
//                        JitsiMeetActivity.launch(OutGoingMeetingInvitation.this,builder.build());
//                        finish();

                    } catch (Exception e) {
                        Toast.makeText( context, "Error in meeting" + e.getMessage(), Toast.LENGTH_SHORT ).show();
                        finish();
                    }

                } else if (type.equals( Constants.REMOTE_MSG_INVITATION_REJECTED )) {
                    Toast.makeText( context, "Invitation  Rejected..", Toast.LENGTH_SHORT ).show();
                    finish();
                }
            }
            stopPlayer();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance( getApplicationContext() ).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter( Constants.REMOTE_MSG_INITIATION_RESPONSE )
        );

        updateStatus( "busy" );

    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance( getApplicationContext() ).unregisterReceiver(
                invitationResponseReceiver
        );
        stopPlayer();
    }


    private void startPlayer(){
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.ring_dialing);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });
        } player.start();
    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlayer();
        updateStatus( "online" );
    }

    public void updateStatus(String status) {
        reference.child( "status" ).setValue( status );
    }
}