package com.astro4callapp.astro4call.register;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.astrologer.AstrologerListAct;
import com.astro4callapp.astro4call.home.MainActivity;
import com.astro4callapp.astro4call.utilities.Constants;
import com.astro4callapp.astro4call.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class Register extends AppCompatActivity {

    private EditText et_name, et_dobTime, et_dobDate, et_dobPlace;
    private RadioGroup radioGroup;
    RadioButton radioButton_Male, radioFemale;
    int hour, minute_val;
    int day, month, year;
    private CheckBox checkBox;
    private Button btn_register;
    private static FirebaseUser currentUser;


    public SharedPreferences sp;
    private static final String SHARED_PREFERENCE = "mypref";
    private static final String KEY_NAME = "number";
    private static final String KEY_ASTRO = "astro";
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private PreferenceManager preferenceManager;

    private FirebaseAuth mAuth;
    private String name, dob, time, place, gender, astro, number;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );


        mAuth = FirebaseAuth.getInstance();
        currentUser =FirebaseAuth.getInstance().getCurrentUser();

        et_name = findViewById( R.id.et_name );
        et_dobDate = findViewById( R.id.et_dob );
        et_dobTime = findViewById( R.id.et_time );
        et_dobPlace = findViewById( R.id.birth_place );
        radioGroup = findViewById( R.id.radioGroup );
        radioButton_Male = findViewById( R.id.radio_male );
        radioFemale = findViewById( R.id.radio_female );
        checkBox = findViewById( R.id.checkBox );
        btn_register = findViewById( R.id.btn_register );

        sp = getSharedPreferences( SHARED_PREFERENCE, MODE_PRIVATE );
        preferenceManager = new PreferenceManager( getApplicationContext() );

        number = getIntent().getStringExtra( "number" );




        final Calendar calendar = Calendar.getInstance();
        et_dobDate.setOnClickListener( new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                day = calendar.get( Calendar.DAY_OF_MONTH );
                month = calendar.get( Calendar.YEAR );
                year = calendar.get( Calendar.YEAR );

                DatePickerDialog datePickerDialog = new DatePickerDialog( Register.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        et_dobDate.setText( SimpleDateFormat.getInstance().format( calendar.getTime() ) );
                    }
                }, year, month, day );
                datePickerDialog.show();

            }
        } );



        radioButton_Male.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText( Register.this, "" + radioButton_Male.getText(), Toast.LENGTH_SHORT ).show();
            }
        } );
        radioFemale.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText( Register.this, "" + radioFemale.getText(), Toast.LENGTH_SHORT ).show();
            }
        } );


        et_dobTime.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dTime();
            }
        } );

        btn_register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                    return; }
                                String  token = task.getResult();
                              //  sendToken( token );
                                registerUser(token);
                                // Toast.makeText( Register.this, ""+token, Toast.LENGTH_SHORT ).show();
                            }
                        });
            }
        } );

    }

    public void dTime() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                minute_val = minute;

                et_dobTime.setText( String.format( Locale.getDefault(), "%02d:%02d", hour, minute ) );
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog( this, onTimeSetListener, hour, minute_val, true );
        timePickerDialog.setTitle( "Select time" );
        timePickerDialog.show();
    }

    public void registerUser(String token) {

        String userId=FirebaseAuth.getInstance().getUid();

        name = et_name.getText().toString();
        if (radioButton_Male.isChecked()) {
            gender = radioButton_Male.getText().toString();
        } else {
            gender = radioFemale.getText().toString();
        }
        dob = et_dobDate.getText().toString();
        time = et_dobTime.getText().toString();
        if (checkBox.isChecked()) {
            astro = "yes";
        } else {
            astro = "no";
        }
        place = et_dobPlace.getText().toString();

        if (et_name.getText().toString().trim().isEmpty()) {
            et_name.setError( "Name is required.." );
        } else if (et_dobDate.getText().toString().trim().isEmpty()) {
            et_dobDate.setError( "Dob is required.." );
        } else if (et_dobTime.getText().toString().trim().isEmpty()) {
            et_dobTime.setError( "Dob is required.." );
        } else if (et_dobPlace.getText().toString().trim().isEmpty()) {
            et_dobPlace.setError( "Dob is required.." );
        } else {

            SharedPreferences.Editor editor = sp.edit();

            HashMap<String, Object> us = new HashMap<>();

            us.put( "name", name );
            us.put( "gender", gender );
            us.put( "dob", dob );
            us.put( "time", time );
            us.put( "place", place );
            us.put( "number", number );
            us.put( "status", "online" );
            us.put( "token", token );
            us.put( "userId", userId );


            rootNode = FirebaseDatabase.getInstance();


            if (checkBox.isChecked()) {
                reference = rootNode.getReference().child( "Astrologers" ).push();

                String id=reference.getKey();

                Toast.makeText( this, ""+id, Toast.LENGTH_SHORT ).show();

                Log.d( "iddd",id );

               preferenceManager.putString(Constants.KEY_USER_REG_ID1,id);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put( "name", name );
                hashMap.put( "exp", "4" );
                hashMap.put( "charge", "20" );
                hashMap.put( "image", "put_url" );
                hashMap.put( "rating", "5" );
                hashMap.put( "status", "online" );
                hashMap.put( "number", number );
                hashMap.put( "token", token );
                us.put( "userId", userId );

                //   reference.setValue(helper); // this for update data
                reference.setValue(hashMap);
                editor.putString( KEY_NAME, number );
                editor.putString( KEY_ASTRO, astro );
                editor.apply();

                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra( "key",id );
                Log.d( "id",id );
                startActivity( intent );


            } else {
                reference = rootNode.getReference().child( "Users" );
                String id=reference.getKey();
                Toast.makeText( this, ""+id, Toast.LENGTH_SHORT ).show();
                Log.d( "iddd",id );

                preferenceManager.putString(Constants.KEY_USER_REG_ID1,id);

                reference.push().setValue( us );
                editor.putString( KEY_NAME, number );
                editor.putString( KEY_ASTRO, null );
                editor.apply();
               startActivity( new Intent( getApplicationContext(), AstrologerListAct.class ) );
              finish();
            }

        }
    }

//    private void sendToken(String token) {
//
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//
//        HashMap<String, Object> user = new HashMap<>();
//
//        user.put(Constants.KEY_FIRST_NAME, et_name.getText().toString());
//        user.put(Constants.KEY_FCM_TOKEN, token);
//
//        database.collection(Constants.KEY_COLLECTIONS_USERS)
//                .add(user)
//                .addOnSuccessListener(documentReference -> {
//                    preferenceManager.pustBoolean(Constants.KEY_IS_SIGNED, true);
//                    preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
//                    preferenceManager.putString(Constants.KEY_FIRST_NAME, et_name.getText().toString());
//                    preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
//                })
//                .addOnFailureListener(e -> {
//                });
//    }
}