package com.astro4callapp.astro4call.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.astrologer.AstrologerListAct;
import com.astro4callapp.astro4call.home.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashAct extends AppCompatActivity {

    FirebaseAuth auth;
    public SharedPreferences sp;
    private static final String SHARED_PREFERENCE = "mypref";
    private static final String KEY_NAME = "number";
    private static final String KEY_ASTRO = "astro";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        auth=FirebaseAuth.getInstance();

        sp = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);

        if (auth.getCurrentUser() != null) {
            String astro=sp.getString(KEY_ASTRO,null);
            if (astro !=null){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }else {
                startActivity(new Intent(getApplicationContext(), AstrologerListAct.class));
                finish();
            }
        }else{
          //  getSupportActionBar().hide();
            new Thread()
            {
                public void run()
                {
                    super.run();
                    try{
                        Thread.sleep(1500);
                        startActivity(new Intent(SplashAct.this,PhoneAct.class));

                        SplashAct.super.finish();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}