package com.astro4callapp.astro4call.astrologer;

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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.navigation_act.CallHistryActivity;
import com.astro4callapp.astro4call.navigation_act.HomeActivity;
import com.astro4callapp.astro4call.navigation_act.PackageActivity;
import com.astro4callapp.astro4call.navigation_act.PaymentActivity;
import com.astro4callapp.astro4call.navigation_act.SupportActivity;
import com.astro4callapp.astro4call.register.PhoneAct;
import com.astro4callapp.astro4call.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class AstrologerListAct extends AppCompatActivity {

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
    TextView toolbartext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_astrologer_main );
        auth = FirebaseAuth.getInstance();


        navigationView = (NavigationView) findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);
        toolbartext = findViewById(R.id.toolbar_text);
        toolbar.setTitle("");
        toolbartext.setText("");

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);


        navigationView.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.home:
                    startActivity(new Intent(AstrologerListAct.this, HomeActivity.class));
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
                    startActivity(new Intent(AstrologerListAct.this, SupportActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.share_app:
                    Intent shareintent = new Intent(Intent.ACTION_SEND);
                    shareintent.setType("text/plain");
                    String sharebody = "Download this Application now:-https://play.google.com/store/apps/details?id=com.T3UnisexGym.t3unisexgym&hl=en";
                    String shareSub = "T3 Fitness Unisex Gym";
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




        preferenceManager = new PreferenceManager( getApplicationContext() );

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return; }
                        String token = task.getResult();

                        sendTokentoServer(token);
                        // Toast.makeText(AstrologerListAct.this, ""+token, Toast.LENGTH_SHORT).show();
                    }
                });



        myRef = FirebaseDatabase.getInstance().getReference().child( "Astrologer" );

        database = FirebaseDatabase.getInstance();
        recyclerView = findViewById( R.id.recyler_astrolist1 );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        list = new ArrayList<>();

        astroAdapter = new AstroAdapter( list, getApplicationContext() );
        recyclerView.setAdapter( astroAdapter );
        swipeRefreshLayout = findViewById( R.id.swipeRefreshLayout );
        swipeRefreshLayout.setOnRefreshListener( this::getUesrs );

        getUesrs();

        //  getData();

    }

    private void sendTokentoServer(String token) {


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


                astroAdapter = new AstroAdapter( list, getApplicationContext() );
                recyclerView.setAdapter( astroAdapter );
                astroAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText( AstrologerListAct.this, "" + error.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );


    }
}