package com.astro4callapp.astro4call.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.astro4callapp.astro4call.R;
import com.astro4callapp.astro4call.astrologer.AstrologerListAct;
import com.astro4callapp.astro4call.home.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneAct extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText phoneText, codeText;
    private Button contiuneButton;
    private String checker = "", phoneNumber = "";
    private RelativeLayout relativeLayout;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mauth;
    private String mVerification;
    private PhoneAuthProvider.ForceResendingToken mResendtoken;
    private ProgressDialog loadingBox;
    private FirebaseUser currentUser;

    public SharedPreferences sp;
    private static final String SHARED_PREFERENCE = "mypref";
    private static final String KEY_NAME = "number";
    private static final String KEY_ASTRO = "astro";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        mauth = FirebaseAuth.getInstance();
        currentUser = mauth.getCurrentUser();

        sp = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE);


        loadingBox = new ProgressDialog(this);
        phoneText = findViewById(R.id.phoneText);
        codeText = findViewById(R.id.codeText);
        contiuneButton = findViewById(R.id.continueNextButton);
        relativeLayout = findViewById(R.id.phoneAuth);
        ccp = findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneText);

        getSupportActionBar().hide();
        contiuneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contiuneButton.getText().equals("Submit") || checker.equals("Code Sent")) {

                    String verificationCode = codeText.getText().toString();

                    if (verificationCode.equals("")) {
                        Toast.makeText(PhoneAct.this, "Please write verifications code..", Toast.LENGTH_SHORT).show();
                    } else {
                        loadingBox.setTitle("Code verification");
                        loadingBox.setMessage("Please wait while we verify");
                        loadingBox.setCanceledOnTouchOutside(false);
                        loadingBox.show();

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerification, verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }


                } else {
                    phoneNumber = ccp.getFullNumberWithPlus();
                    if (!phoneNumber.equals("")) {
                        loadingBox.setTitle("Phone number verification");
                        loadingBox.setMessage("Please wait while we verify");
                        loadingBox.setCanceledOnTouchOutside(false);
                        loadingBox.show();

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 90, TimeUnit.SECONDS, PhoneAct.this, callbacks);
                    } else {
                        Toast.makeText(PhoneAct.this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                // Toast.makeText(RegisterAct.this, "Invalid phone..", Toast.LENGTH_SHORT).show();
                relativeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneAct.this, "Try after some time..", Toast.LENGTH_SHORT).show();
                loadingBox.dismiss();

                contiuneButton.setText("Continue");
                codeText.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerification = s;
                mResendtoken = forceResendingToken;
                Toast.makeText(PhoneAct.this, "Code sent..", Toast.LENGTH_SHORT).show();
                loadingBox.dismiss();

                relativeLayout.setVisibility(View.GONE);
                checker = "Code Sent";
                contiuneButton.setText("Submit");
                codeText.setVisibility(View.VISIBLE);
            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String phoneNum = phoneText.getText().toString();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            loadingBox.dismiss();

                            String num = sp.getString(KEY_NAME, null);
                            String astro = sp.getString(KEY_ASTRO, null);

                            if (num != null) {
                                if (astro != null) {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(getApplicationContext(), AstrologerListAct.class));
                                    finish();
                                }
                            } else {
                                Intent intent = new Intent(getApplicationContext(), Register.class);
                                intent.putExtra("number", phoneNum);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            loadingBox.dismiss();
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }

                    }
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}