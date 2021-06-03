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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.astrologer.AstrologerListAct;
import com.astro4callapp.astro4call.home.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Register extends AppCompatActivity {

    private EditText et_name, et_dobTime, et_dobDate, et_dobPlace;
    private RadioGroup radioGroup;
    RadioButton radioButton_Male, radioFemale;
    int hour, minute_val;
    int day, month, year;
    private CheckBox checkBox;
    private Button btn_register;

    public SharedPreferences sp;
    private static final String SHARED_PREFERENCE = "mypref";
    private static final String KEY_NAME = "number";
    private static final String KEY_ASTRO = "astro";
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;


    private FirebaseAuth mAuth;
    private String name, dob, time, place, gender, astro, number, status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        et_name = findViewById(R.id.et_name);
        et_dobDate = findViewById(R.id.et_dob);
        et_dobTime = findViewById(R.id.et_time);
        et_dobPlace = findViewById(R.id.birth_place);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton_Male = findViewById(R.id.radio_male);
        radioFemale = findViewById(R.id.radio_female);
        checkBox = findViewById(R.id.checkBox);
        btn_register = findViewById(R.id.btn_register);

        sp = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);


        number = getIntent().getStringExtra("number");


        // getSupportActionBar().hide();
        final Calendar calendar = Calendar.getInstance();
        et_dobDate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.YEAR);
                year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        et_dobDate.setText(SimpleDateFormat.getInstance().format(calendar.getTime()));
                    }
                }, year, month, day);
                datePickerDialog.show();

            }
        });

        radioButton_Male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Register.this, "" + radioButton_Male.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        radioFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Register.this, "" + radioFemale.getText(), Toast.LENGTH_SHORT).show();
            }
        });


        et_dobTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dTime();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    public void dTime() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hour = hourOfDay;
                minute_val = minute;

                et_dobTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute_val, true);
        timePickerDialog.setTitle("Select time");
        timePickerDialog.show();
    }


    public void registerUser() {

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
            et_name.setError("Name is required..");
        } else if (et_dobDate.getText().toString().trim().isEmpty()) {
            et_dobDate.setError("Dob is required..");
        } else if (et_dobTime.getText().toString().trim().isEmpty()) {
            et_dobTime.setError("Dob is required..");
        } else if (et_dobPlace.getText().toString().trim().isEmpty()) {
            et_dobPlace.setError("Dob is required..");
        } else {


//            FirebaseFirestore db = FirebaseFirestore.getInstance();
            SharedPreferences.Editor editor = sp.edit();

            HashMap<String, Object> us = new HashMap<>();
            us.put("name", name);
            us.put("gender", gender);
            us.put("dob", dob);
            us.put("time", time);
            us.put("place", place);
            us.put("Astrologer", astro);
            us.put("number", number);


            rootNode = FirebaseDatabase.getInstance();
            if (checkBox.isChecked()) {
                reference = rootNode.getReference().child("Astrologers");

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name", name);
                hashMap.put("exp", "4");
                hashMap.put("charge", "20");
                hashMap.put("image", "put_url");
                hashMap.put("rating", "5");
                hashMap.put("status", "online");
                hashMap.put("number", number);

                String astrokey=reference.push().getKey();
                DatabaseReference num =  reference.child(astrokey).child("number");


//               Query query = reference.orderByChild(astrokey).orderByChild("number");

                //Log.d("number",query.toString());

                Log.d("astrokey",num.toString());


                //   reference.setValue(helper); // this for update data
//                 reference.push().setValue(hashMap);


                editor.putString(KEY_NAME, number);
                editor.putString(KEY_ASTRO, astro);
                editor.apply();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));


            } else {
                reference = rootNode.getReference().child("Users");
                reference.push().setValue(us);
                editor.putString(KEY_NAME, number);
                editor.putString(KEY_ASTRO, null);
                editor.apply();
                startActivity(new Intent(getApplicationContext(), AstrologerListAct.class));
                finish();

            }
        }

    }
}