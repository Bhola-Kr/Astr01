package com.astro4callapp.astro4call.invitation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.network.ApiClient;
import com.astro4callapp.astro4call.network.ApiService;
import com.astro4callapp.astro4call.utilities.Constants;
import com.astro4callapp.astro4call.utilities.PreferenceManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcceptActivity extends AppCompatActivity {

    private String meetingType=null;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept);



        preferenceManager = new PreferenceManager( getApplicationContext() );

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child( "Astrologers" ).child(preferenceManager.getString( Constants.KEY_USER_REG_ID1 ));

        ImageView imageViewMeetingType = findViewById(R.id.imageMeetingType);

        meetingType = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);

        if (meetingType != null) {
            if (meetingType.equals("video")) {
                imageViewMeetingType.setImageResource(R.drawable.video);
            }else{
                imageViewMeetingType.setImageResource(R.drawable.telephone1);
            }
        }

        Toast.makeText( this, ""+meetingType, Toast.LENGTH_SHORT ).show();


        TextView textFirstChar = findViewById(R.id.text_incomfirstChar);
        TextView userName = findViewById(R.id.text_incom_UserName);
        TextView userEmail = findViewById(R.id.textIncomEmail);


        String firstName = getIntent().getStringExtra(Constants.KEY_FIRST_NAME);
        // String firstEmail = getIntent().getStringExtra(Constants.KEY_EMAIL);


        if (firstName != null) {
            textFirstChar.setText(firstName.substring(0, 1));
        }
        userName.setText(firstName);
       // userEmail.setText(firstEmail);

        ImageView imageAcceptedInvitation = findViewById(R.id.imageAcceptMeeting_incom);
        ImageView imageRejectedInvitation = findViewById(R.id.reject_meeting_incom_iv);


        imageAcceptedInvitation.setOnClickListener(v -> sendInvitationResponse(
                Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                getIntent().getStringExtra(Constants.REMOTE_MSG_INVITOR_TOKEN)
        ));

        imageRejectedInvitation.setOnClickListener(v -> sendInvitationResponse(
                Constants.REMOTE_MSG_INVITATION_REJECTED,
                getIntent().getStringExtra(Constants.REMOTE_MSG_INVITOR_TOKEN)
        ));
    }


    private void sendInvitationResponse(String type, String reciverToken) {

        try {
            JSONArray token = new JSONArray();
            token.put(reciverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INITIATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INITIATION_RESPONSE, type);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, token);

            sendRemoteMessage(body.toString(), type);


        } catch (Exception e) {
            Toast.makeText(this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }


    }


    private void sendRemoteMessage(String remoteMessageBody, String type) {

        ApiClient.getClient().create( ApiService.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {

                    if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
                        try {
//                            URL serverUrl=new URL("https://meet.jit.si");
//
//                            JitsiMeetConferenceOptions.Builder builder=new JitsiMeetConferenceOptions.Builder();
//                            builder.setServerURL(serverUrl);
//                            builder.setWelcomePageEnabled(false);
//                            builder.setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM));
//
//                            if (meetingType.equals("audio")){
//                                builder.setVideoMuted(true);
//                            }
//
//                            JitsiMeetActivity.launch(IncomingInvitationAct.this,builder.build());
//                            finish();

                        }catch (Exception e){
                            Toast.makeText(AcceptActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }


                    } else {
                        Toast.makeText(AcceptActivity.this, "Call Rejected..", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } else {
                    Toast.makeText(AcceptActivity.this, "Response error" + response.message(), Toast.LENGTH_LONG).show();
                    finish();
                }


            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(AcceptActivity.this, "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INITIATION_RESPONSE);

            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)) {
                    Toast.makeText(context, "Invitation Cancelled..", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter( Constants.REMOTE_MSG_INITIATION_RESPONSE)
        );
        updateStatus( "busy" );
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateStatus( "online" );
    }

    public void updateStatus(String status) {
        reference.child( "status" ).setValue( status );
    }
}