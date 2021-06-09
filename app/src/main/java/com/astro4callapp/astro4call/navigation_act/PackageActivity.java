package com.astro4callapp.astro4call.navigation_act;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.utilities.Constants;
import com.astro4callapp.astro4call.utilities.PreferenceManager;

public class PackageActivity extends AppCompatActivity {

    PreferenceManager preferenceManager;

    CardView cardpackage1,cardpackage2,cardpackage3,cardpackage4,cardpackage5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_package );

        preferenceManager=new PreferenceManager(getApplicationContext());

        preferenceManager.putFloatVal( Constants.KEY_WALLET_FLOAT_AMOUNT,10.0f);
    }
}