package com.astro4callapp.astro4call.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.chats.ChatDetails;
import com.astro4callapp.astro4call.register.PhoneAct;
import com.astro4callapp.astro4call.utilities.Constants;
import com.astro4callapp.astro4call.utilities.PreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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


    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    TextView toolbartext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        auth = FirebaseAuth.getInstance();

        sp = getSharedPreferences( SHARED_PREFERENCE, MODE_PRIVATE );


        preferenceManager = new PreferenceManager( getApplicationContext() );

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child( "Astrologers" ).child( preferenceManager.getString( Constants.KEY_USER_REG_ID1_ASTRO ) );


        navigationView = (NavigationView) findViewById( R.id.navigationview );
        drawerLayout = findViewById( R.id.drawer );
        toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        toolbartext = findViewById( R.id.toolbar_text );
        toolbar.setTitle( "" );
        toolbartext.setText( "" );

        toggle = new ActionBarDrawerToggle( this, drawerLayout, toolbar, R.string.open, R.string.close );
        toggle.getDrawerArrowDrawable().setColor( getResources().getColor( R.color.white ) );
        toggle.syncState();
        drawerLayout.addDrawerListener( toggle );



        View hview = navigationView.getHeaderView(0);
        TextView mNameTextView = (TextView) hview.findViewById(R.id.astro_Guestname);
        TextView user_timeNav = (TextView) hview.findViewById(R.id.astro_timeText);
        // CircleImageView img = (CircleImageView) hview.findViewById(R.id.Guestphoto);
        mNameTextView.setText( preferenceManager.getString( Constants.KEY_FIRST_NAME ) );


        navigationView.setNavigationItemSelectedListener( item->{

            switch (item.getItemId()) {
                case R.id.astro_home:

                    drawerLayout.closeDrawer( GravityCompat.START );
                    break;
                case R.id.astro_support:
                    try {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto","astro4call@gmail.com", null));
                        startActivity(Intent.createChooser(intent, "Choose an Email client :"));

                    }catch (Exception exception){
                        Toast.makeText(this, ""+exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    drawerLayout.closeDrawer( GravityCompat.START );
                    break;

                case R.id.astro_share_app:
                    Intent shareintent = new Intent( Intent.ACTION_SEND );
                    shareintent.setType( "text/plain" );
                    String sharebody = "Download this Application now:-https://play.google.com/store/apps/details?id=com.T3UnisexGym.t3unisexgym&hl=en";
                    String shareSub = "T3 Fitness Unisex Gym";
                    shareintent.putExtra( Intent.EXTRA_SUBJECT, shareSub );
                    shareintent.putExtra( Intent.EXTRA_TEXT, sharebody );
                    startActivity( Intent.createChooser( shareintent, "Share Using" ) );
                    drawerLayout.closeDrawer( GravityCompat.START );
                    break;

                case R.id.astro_logout:
                    AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this );
                    builder.setTitle( "Exit !!!" );
                    builder.setMessage( "Are you sure you want to exit?" );
                    builder.setPositiveButton( "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (auth.getCurrentUser() != null) {
                                auth.signOut();
                                startActivity( new Intent( getApplicationContext(), PhoneAct.class ) );
                                finish();
                            }
                        }
                    } );
                    builder.setNegativeButton( "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    } );
                    builder.show();
                    break;
            }
            return true;
        } );


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