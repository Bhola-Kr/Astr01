package com.astro4callapp.astro4call.astrologer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.register.PhoneAct;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AstrologerListAct extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<AstrorecyclerModel> list;
    AstroAdapter astroAdapter;
    FirebaseDatabase database;
    private SwipeRefreshLayout swipeRefreshLayout;
    FirebaseAuth auth;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astrologer_main);
        auth = FirebaseAuth.getInstance();
        getSupportActionBar().hide();

        myRef=FirebaseDatabase.getInstance().getReference().child("Astrologer");

        database = FirebaseDatabase.getInstance();
        recyclerView = findViewById(R.id.recyler_astrolist1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        astroAdapter = new AstroAdapter(list, getApplicationContext());
        recyclerView.setAdapter(astroAdapter);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::getUesrs);

        getUesrs();

      //  getData();

    }

    public void getUesrs() {
        swipeRefreshLayout.setRefreshing(true);
        database.getReference().child("Astrologers").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                swipeRefreshLayout.setRefreshing(false);
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {


                    AstrorecyclerModel users = dataSnapshot1.getValue(AstrorecyclerModel.class);
                    users.setUesrId(dataSnapshot1.getKey());

                    if (!users.getUesrId().equals(FirebaseAuth.getInstance().getUid())) {
                        list.add(users);
                    }

                }
                astroAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void exit_app(View view) {
        if (auth.getCurrentUser() != null) {
            auth.signOut();
            startActivity(new Intent(getApplicationContext(), PhoneAct.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void getData(){

       myRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               list.clear();

               for (DataSnapshot snapshot1 : snapshot.getChildren()){

                   AstrorecyclerModel model=new AstrorecyclerModel();

                   model.setImage(snapshot1.child("image").getValue().toString());
                   model.setName(snapshot1.child("name").getValue().toString());
                   model.setExp(snapshot1.child("exp").getValue().toString());
                   model.setCharge(snapshot1.child("charge").getValue().toString());

                   list.add(model);

               }



               astroAdapter = new AstroAdapter(list, getApplicationContext());
               recyclerView.setAdapter(astroAdapter);
               astroAdapter.notifyDataSetChanged();

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               Toast.makeText(AstrologerListAct.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
           }
       });


    }
}