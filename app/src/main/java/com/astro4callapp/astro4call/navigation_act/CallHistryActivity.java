package com.astro4callapp.astro4call.navigation_act;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.TextView;

import com.astro4callapp.astro4call.DbHelper;
import com.astro4callapp.astro4call.R;

public class CallHistryActivity extends AppCompatActivity {

    DbHelper helper;
    String date,hour,second,minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_call_histry );

        TextView textView=findViewById( R.id.callHistryTv );

        helper=new DbHelper( getApplicationContext() );

        Cursor mydata = helper.readData();
        if (mydata.getCount() == 0) {

        }
        StringBuffer buffer = new StringBuffer();
        while (mydata.moveToNext()) {
            // buffer.append("NAME is " + mydata.getString(1));
            date = mydata.getString(1);
            hour = mydata.getString(2);
            minute = mydata.getString(3);
            second = mydata.getString(4);
        }
        textView.setText( date+hour+second+minute );

        Log.d( "hour",""+buffer );

    }
}