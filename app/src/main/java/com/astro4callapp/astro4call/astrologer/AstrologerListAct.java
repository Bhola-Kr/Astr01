package com.astro4callapp.astro4call.astrologer;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.UpdateWallet;
import com.astro4callapp.astro4call.navigation_act.CallHistryActivity;
import com.astro4callapp.astro4call.navigation_act.PackageActivity;
import com.astro4callapp.astro4call.navigation_act.PaymentActivity;
import com.astro4callapp.astro4call.register.PhoneAct;
import com.astro4callapp.astro4call.utilities.Constants;
import com.astro4callapp.astro4call.utilities.PreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AstrologerListAct extends AppCompatActivity implements UpdateWallet {

    RecyclerView recyclerView;
    ArrayList<AstrorecyclerModel> list;
    AstroAdapter astroAdapter;
    FirebaseDatabase database;
    private SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth auth;
    private DatabaseReference myRef;
    private PreferenceManager preferenceManager;

    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    TextView toolbartext,toolPrice;
    ImageView toolwalletImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_astrologer_main );
        auth = FirebaseAuth.getInstance();

        preferenceManager = new PreferenceManager( getApplicationContext() );
        navigationView = (NavigationView) findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);
        toolbartext = findViewById(R.id.toolbar_text);
        toolwalletImage=findViewById( R.id.wallet );
        toolPrice=findViewById( R.id.tool_price );


        toolbar.setTitle("");
        toolbartext.setText("");



        //String price_wallet=toolPrice.getText().toString();

        //Long l_price= Long.valueOf( price_wallet );

       // preferenceManager.putFloatVal(Constants.KEY_WALLET_FLOAT_AMOUNT,100.0f);




        Log.d( "getVal", ""+preferenceManager.getFloat( Constants.KEY_WALLET_FLOAT_UPDATED_AMOUNT ) );

        toolwalletImage.setImageResource( R.drawable.wallet );



        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);

        View hview = navigationView.getHeaderView(0);
        TextView mNameTextView = (TextView) hview.findViewById(R.id.Guestname);
        TextView user_timeNav = (TextView) hview.findViewById(R.id.user_time);
        // CircleImageView img = (CircleImageView) hview.findViewById(R.id.Guestphoto);
        mNameTextView.setText( preferenceManager.getString( Constants.KEY_FIRST_NAME ) );



        navigationView.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.home:
                   // startActivity(new Intent(AstrologerListAct.this, HomeActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.packages:
                    startActivity(new Intent(AstrologerListAct.this, PackageActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.callhistory:
                    startActivity(new Intent(AstrologerListAct.this, CallHistryActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.paymenthistory:
                    startActivity(new Intent(AstrologerListAct.this, PaymentActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.support:


                    try {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto","astro4call@gmail.com", null));
                        startActivity(Intent.createChooser(intent, "Choose an Email client :"));

                    }catch (Exception exception){
                        Toast.makeText(this, ""+exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.share_app:
                    Intent shareintent = new Intent(Intent.ACTION_SEND);
                    shareintent.setType("text/plain");
                    String sharebody = "Download this Application now:-com.astro4callapp.astro4call.astrologer=en";
                    String shareSub = "Astro4Call";
                    shareintent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                    shareintent.putExtra(Intent.EXTRA_TEXT, sharebody);
                    startActivity(Intent.createChooser(shareintent, "Share Using"));
                    drawerLayout.closeDrawer( GravityCompat.START);
                    break;

                case R.id.logout:
                    AlertDialog.Builder builder = new AlertDialog.Builder(AstrologerListAct.this);
                    builder.setTitle("Exit !!!");
                    builder.setMessage("Are you sure you want to exit?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (auth.getCurrentUser() != null) {
                                auth.signOut();
                                startActivity( new Intent( getApplicationContext(), PhoneAct.class ) );
                                finish();
                            }
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                    break;
            }
            return true;
        });






//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                            return; }
//                       String  token = task.getResult();
//
//                       sendFCMTokenToDatabase( token );
//                      // Toast.makeText(AstrologerListAct.this, ""+token, Toast.LENGTH_SHORT).show();
//                    }
//                });

        myRef = FirebaseDatabase.getInstance().getReference().child( "Astrologer" );

        database = FirebaseDatabase.getInstance();
        recyclerView = findViewById( R.id.recyler_astrolist1 );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        list = new ArrayList<>();

        astroAdapter = new AstroAdapter( list, getApplicationContext());
        recyclerView.setAdapter( astroAdapter );
        swipeRefreshLayout = findViewById( R.id.swipeRefreshLayout );
        swipeRefreshLayout.setOnRefreshListener( this::getUesrs );

        getUesrs();

        //  getData();

    }



    private void sendFCMTokenToDatabase(String token) {

    }

    public void getUesrs() {
        swipeRefreshLayout.setRefreshing( true );
        database.getReference().child( "Astrologers" ).addValueEventListener( new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                swipeRefreshLayout.setRefreshing( false );
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {


                    AstrorecyclerModel users = dataSnapshot1.getValue( AstrorecyclerModel.class );
                    users.setUesrId( dataSnapshot1.getKey() );

                    if (!users.getUesrId().equals( FirebaseAuth.getInstance().getUid() )) {
                        list.add( users );
                    }

                }
                astroAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void getData() {

        myRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {

                    AstrorecyclerModel model = new AstrorecyclerModel();

                    model.setImage( snapshot1.child( "image" ).getValue().toString() );
                    model.setName( snapshot1.child( "name" ).getValue().toString() );
                    model.setExp( snapshot1.child( "exp" ).getValue().toString() );
                    model.setCharge( snapshot1.child( "charge" ).getValue().toString() );

                    list.add( model );

                }


                astroAdapter = new AstroAdapter( list, getApplicationContext());
                recyclerView.setAdapter( astroAdapter );
                astroAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText( AstrologerListAct.this, "" + error.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );

    }

    @Override
    protected void onStart() {
        super.onStart();
        getUpdatedWallet();

    }

    @Override
    protected void onResume() {
        super.onResume();

        getUpdatedWallet();
    }

    public void getUpdatedWallet(){
        if (preferenceManager.getFloat( Constants.KEY_WALLET_FLOAT_AMOUNT ) > preferenceManager.getFloat( Constants.KEY_WALLET_FLOAT_UPDATED_AMOUNT )  ){

            Log.d( "getVal", ""+preferenceManager.getFloat( Constants.KEY_WALLET_FLOAT_UPDATED_AMOUNT ) );

            toolPrice.setText( ""+preferenceManager.getFloat( Constants.KEY_WALLET_FLOAT_UPDATED_AMOUNT ) );
        }else {
            toolPrice.setText( ""+preferenceManager.getFloat( Constants.KEY_WALLET_FLOAT_AMOUNT ) );
        }

    }

    @Override
    public void updateWallet() {
        getUpdatedWallet();
    }
}