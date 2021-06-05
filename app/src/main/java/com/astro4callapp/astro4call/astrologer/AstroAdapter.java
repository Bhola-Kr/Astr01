package com.astro4callapp.astro4call.astrologer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.chats.ChatDetails;
import com.astro4callapp.astro4call.invitation.RejectActivity;
import com.astro4callapp.astro4call.utilities.Constants;
import com.astro4callapp.astro4call.utilities.PreferenceManager;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
public class AstroAdapter extends RecyclerView.Adapter<AstroAdapter.AstroViewHolder> {


    ArrayList<AstrorecyclerModel> datalist;
    Context context;


    public AstroAdapter(ArrayList<AstrorecyclerModel> datalist, Context context) {
        this.datalist = datalist;
        this.context = context;

    }

    @NonNull
    @Override
    public AstroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.astro_list, parent, false );
        return new AstroViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull AstroViewHolder holder, int position) {
        final AstrorecyclerModel model = new AstrorecyclerModel();


//        Glide.with(context).load(datalist.get(position).getImage())
//                .into(holder.image);

        Picasso.get().load( model.getImage() ).placeholder( R.drawable.welcome_img ).into( holder.image );


        holder.name.setText( datalist.get( position ).getName() );
        holder.charge.setText( datalist.get( position ).getCharge() + " / Minute" );
        holder.exp.setText( datalist.get( position ).getExp() + " Year" );
        holder.astroRating.setText( datalist.get( position ).getRating() );

        String status = datalist.get( position ).getStatus();
        String token = datalist.get( position ).getToken();

        if (status.equals( "online" )) {
            holder.online.setVisibility( View.VISIBLE );
            holder.offline.setVisibility( View.GONE );
            holder.busy.setVisibility( View.GONE );
            holder.status.setText( "online" );

        } else if (status.equals( "busy" )) {
            holder.online.setVisibility( View.GONE );
            holder.offline.setVisibility( View.GONE );
            holder.busy.setVisibility( View.VISIBLE );
            holder.status.setText( "busy" );
        } else {
            holder.online.setVisibility( View.GONE );
            holder.offline.setVisibility( View.VISIBLE );
            holder.busy.setVisibility( View.GONE );
            holder.status.setText( "offline" );
        }

        holder.iv_call.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (status.equals( "offline" ) || status.equals( "busy" )) {
                    Toast.makeText( context, "At this time you can't call to " + datalist.get( position ).getName(), Toast.LENGTH_SHORT ).show();
                } else {

                    if (token == null || token.trim().isEmpty()) {
                        Toast.makeText( context, "User is not avilable for meeting", Toast.LENGTH_SHORT ).show();
                    } else {
                        // Toast.makeText( context, ""+token, Toast.LENGTH_SHORT ).show();

                        Intent intent = new Intent( context, RejectActivity.class );
                        intent.putExtra( "name", holder.name.getText().toString() );
                        intent.putExtra( "type", "call" );
                        intent.putExtra( "token", token);
                        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        context.startActivity( intent );
                    }
                }
            }
        } );

        holder.iv_video_call.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals( "offline" ) || status.equals( "busy" )) {
                    Toast.makeText( context, "At this time you can't call to" + datalist.get( position ).getName(), Toast.LENGTH_SHORT ).show();
                } else {

                    if (token == null || token.trim().isEmpty()) {
                        Toast.makeText( context, "User is not avilable for meeting", Toast.LENGTH_SHORT ).show();
                    } else {

                        Intent intent = new Intent( context, RejectActivity.class );
                        intent.putExtra( "name", holder.name.getText().toString() );
                        intent.putExtra( "type", "video");
                        intent.putExtra( "token", token);
                        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        context.startActivity( intent );
                    }
                }
            }
        } );

        holder.iv_chat.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status.equals( "offline" ) || status.equals( "busy" )) {
                    Toast.makeText( context, "At this time you can't call to" + datalist.get( position ).getName(), Toast.LENGTH_SHORT ).show();
                } else {
                    if (token == null || token.trim().isEmpty()) {
                        Toast.makeText( context, "User is not avilable for meeting", Toast.LENGTH_SHORT ).show();
                    } else {

                        Intent intent = new Intent( context, ChatDetails.class );
                        intent.putExtra( "userId", datalist.get( position ).getUesrId() );
                        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        context.startActivity( intent );
                    }
                }
            }
        } );
        holder.image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( context, AstroDetailsActivity.class );

                intent.putExtra( "name", holder.name.getText().toString() );
                intent.putExtra( "exp", holder.exp.getText().toString() );
                intent.putExtra( "charge", holder.charge.getText().toString() );
                intent.putExtra( "image", model.getImage() );

                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                context.startActivity( intent );
            }
        } );
        holder.name.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( context, AstroDetailsActivity.class );

                intent.putExtra( "name", holder.name.getText().toString() );
                intent.putExtra( "exp", holder.exp.getText().toString() );
                intent.putExtra( "charge", holder.charge.getText().toString() );
                intent.putExtra( "image", model.getImage() );

                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                context.startActivity( intent );
            }
        } );


    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class AstroViewHolder extends RecyclerView.ViewHolder {
        TextView name, charge, exp, online, offline, busy, status, astroRating;
        CircleImageView image;
        ImageView iv_call, iv_chat, iv_video_call;


        public AstroViewHolder(@NonNull View itemView) {
            super( itemView );

            name = itemView.findViewById( R.id.astroName1 );
            charge = itemView.findViewById( R.id.astroCharge1 );
            exp = itemView.findViewById( R.id.astroExp1 );
            online = itemView.findViewById( R.id.astroOnline1 );
            offline = itemView.findViewById( R.id.astroOffline1 );
            busy = itemView.findViewById( R.id.astroOfBuisy1 );
            image = itemView.findViewById( R.id.image1 );

            status = itemView.findViewById( R.id.astroStatus1 );
            astroRating = itemView.findViewById( R.id.astroRating1 );

            iv_call = itemView.findViewById( R.id.call_iv_item );
            iv_chat = itemView.findViewById( R.id.chat_iv_item );
            iv_video_call = itemView.findViewById( R.id.video_call_iv );

        }
    }
}
