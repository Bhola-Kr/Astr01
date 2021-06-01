 package com.astro4callapp.astro4call.userslist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.astro4callapp.astro4call.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UesrListAdapterAct extends AppCompatActivity {

    KProgressHUD progressHUD;
    RecyclerView mRecyclerView;
    DatabaseReference mDatabase;
    FirebaseRecyclerOptions mOptions;
    FirebaseRecyclerAdapter<UesrListModel, ItemViewHolder> mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astrolist);

        progressHUD = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait...")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.keepSynced(true);
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                progressHUD.dismiss();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s){}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot){}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError){}
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.myRecycleView);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.orderByKey();

        mRecyclerView.hasFixedSize();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mOptions = new FirebaseRecyclerOptions.Builder<UesrListModel>().setQuery(query, UesrListModel.class).build();

        mRecyclerViewAdapter = new FirebaseRecyclerAdapter<UesrListModel, ItemViewHolder>(mOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull UesrListModel model) {
                holder.setTitle(model.getTitle());
                holder.setImage(getBaseContext(), model.getImage());
            }

            @Override
            public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_astrolist, parent, false);
                return new ItemViewHolder(view);
            }
        };

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
    }




    @Override
    public void onStart() {
        super.onStart();
        mRecyclerViewAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mRecyclerViewAdapter.stopListening();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ItemViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        public void setTitle(String title){
            TextView post_title = (TextView)mView.findViewById(R.id.title);
            post_title.setText(title);
        }

        public void setImage(Context ctx, String image){
            CircleImageView post_image = (CircleImageView) mView.findViewById(R.id.image);
            Picasso.get().load(image).into(post_image);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
