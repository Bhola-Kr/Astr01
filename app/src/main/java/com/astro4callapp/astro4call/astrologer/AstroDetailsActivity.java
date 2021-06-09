package com.astro4callapp.astro4call.astrologer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.astro4callapp.astro4call.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

public class AstroDetailsActivity extends AppCompatActivity {

    private ImageView astroImage;
    private TextView nameds,expText,location,chargeText,language,astroType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_astro_details);

        astroImage=findViewById(R.id.iv_astroDetails);
        nameds=findViewById(R.id.astroNameDetails);
        location=findViewById(R.id.astroLocationText);
        expText=findViewById(R.id.astroExpDetails);
        chargeText=findViewById(R.id.astroChargeDetails);
        language=findViewById(R.id.astroLanguageDetails);
        astroType=findViewById(R.id.astroTypeDetails);

        // getSupportActionBar().hide();


        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String image = intent.getStringExtra("image");
        String charge = intent.getStringExtra("charge");
        String exp = intent.getStringExtra("exp");


        if (intent !=null){
            nameds.setText(name);
            chargeText.setText(charge);
            expText.setText(exp);
            Glide.with(getApplicationContext()).load(image).into(astroImage);
        }else {
            Picasso.get().load(image).placeholder(R.drawable.astro_image).into(astroImage);
        }

    }
}