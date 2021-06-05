package com.astro4callapp.astro4call.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.chats.ChatDetails;
import com.astro4callapp.astro4call.register.PhoneAct;
import com.astro4callapp.astro4call.utilities.Constants;
import com.astro4callapp.astro4call.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private PreferenceManager preferenceManager;


    private String id;
    public SharedPreferences sp;
    private static final String SHARED_PREFERENCE = "mypref";
    private static final String KEY_NAME = "number";
    private static final String KEY_ASTRO = "astro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        auth = FirebaseAuth.getInstance();

        sp = getSharedPreferences( SHARED_PREFERENCE, MODE_PRIVATE );
        preferenceManager = new PreferenceManager( getApplicationContext() );

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child( "Astrologers" ).child(preferenceManager.getString( Constants.KEY_USER_REG_ID1 ));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void logout(View view) {
        SharedPreferences.Editor editor = sp.edit();

        if (auth.getCurrentUser() != null) {
            auth.signOut();

            editor.putString( KEY_NAME, null );
            editor.putString( KEY_ASTRO, null );
            editor.apply();
            startActivity( new Intent( getApplicationContext(), PhoneAct.class ) );
            finish();
        }
    }

    public void chatDetails(View view) {
        startActivity( new Intent( getApplicationContext(), ChatDetails.class ) );
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateStatus( "online" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateStatus( "offline" );
    }


    public void updateStatus(String status) {
        reference.child( "status" ).setValue( status );
    }

}