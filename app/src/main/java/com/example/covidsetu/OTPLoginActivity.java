package com.example.covidsetu;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPLoginActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private Button btnGetOTP;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otplogin);

        // initialize fields
        phoneNumber = (EditText) findViewById(R.id.edit_text_phone_number);
        btnGetOTP = (Button) findViewById(R.id.btn_get_otp);

        progressBar = (ProgressBar) findViewById(R.id.progressbar_otp_login);

        btnGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _phoneNumber = phoneNumber.getText().toString().trim();
                if (!_phoneNumber.isEmpty()) {
                    if (_phoneNumber.length() == 10) {

                        progressBar.setVisibility(View.VISIBLE);
                        btnGetOTP.setVisibility(View.INVISIBLE);

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+91" + phoneNumber.getText().toString().trim(),
                                60,
                                TimeUnit.SECONDS,
                                OTPLoginActivity.this,
                                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                    @Override
                                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                        progressBar.setVisibility(View.GONE);
                                        btnGetOTP.setVisibility(View.VISIBLE);

                                    }

                                    @Override
                                    public void onVerificationFailed(@NonNull FirebaseException e) {
                                        progressBar.setVisibility(View.GONE);
                                        btnGetOTP.setVisibility(View.VISIBLE);

                                        Toast.makeText(OTPLoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        progressBar.setVisibility(View.GONE);
                                        btnGetOTP.setVisibility(View.VISIBLE);

                                        Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);
                                        intent.putExtra("phone-number", _phoneNumber);
                                        intent.putExtra("backend-otp", s);
                                        startActivity(intent);
                                    }
                                }
                        );


                    } else {
                        Toast.makeText(OTPLoginActivity.this, "Please enter correct number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OTPLoginActivity.this, "Enter mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}