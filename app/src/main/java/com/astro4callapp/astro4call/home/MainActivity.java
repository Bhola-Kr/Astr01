package com.astro4callapp.astro4call.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.register.PhoneAct;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;

    public SharedPreferences sp;
    private static final String SHARED_PREFERENCE = "mypref";
    private static final String KEY_NAME = "number";
    private static final String KEY_ASTRO = "astro";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        sp = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);

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

            editor.putString(KEY_NAME, null);
            editor.putString(KEY_ASTRO, null);
            editor.apply();
            startActivity(new Intent(getApplicationContext(), PhoneAct.class));
            finish();
        }
    }

}