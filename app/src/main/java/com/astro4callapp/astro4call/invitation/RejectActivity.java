package com.astro4callapp.astro4call.invitation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RejectActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private String inviterToken = null;
    private String meetingRoom = null;
    private String meetingType = null;
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject);

        getSupportActionBar().hide();


/*--          get current user phone number -----*/
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String phoneNumber = user.getPhoneNumber();
//        Toast.makeText(this, ""+phoneNumber, Toast.LENGTH_LONG).show();

        ImageView  imageViewMeetingType= findViewById(R.id.out_imageMeetingType);
        TextView textFirstChar = findViewById(R.id.text_invit_send_firstChar);
        TextView textName = findViewById(R.id.text_invit_send_UserName);
        TextView texMeeting =findViewById(R.id.textsendingMeetingInvition1);
        // TextView textEmail = findViewById(R.id.textInvit_sind_Email);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String call = intent.getStringExtra("call");

        textName.setText(name);
        textFirstChar.setText(name.substring(0,1));


        if (intent != null) {

            if (call.equals("call")) {
                texMeeting.setText("Phone Calling..");
                imageViewMeetingType.setImageResource(R.drawable.telephone1);
            } else {
                texMeeting.setText("Video Calling..");
                imageViewMeetingType.setImageResource(R.drawable.video);
            }
        }


        ImageView stopInvitation = findViewById(R.id.reject_send_meeting_iv);

        stopInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}