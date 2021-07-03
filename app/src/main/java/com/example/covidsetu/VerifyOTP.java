package com.example.covidsetu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {

    private EditText otp1, otp2, otp3, otp4, otp5, otp6;
    private Button btnVerify;
    private TextView resendOtp, displayPhoneNumber;
    private ProgressBar progressBar;

    String otpBackend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        Initialize();

        // display phone number
        displayPhoneNumber.setText(String.format(
                "+91-%s", getIntent().getStringExtra("phone-number")
        ));

        // verify entered otp
        VerifyOtp();

        // move to next input field
        MoveToNextOtpField();

        // resend otp
        ResendOtp();


    }

    private void Initialize() {
        otp1 = (EditText) findViewById(R.id.edit_text_otp1);
        otp2 = (EditText) findViewById(R.id.edit_text_otp2);
        otp3 = (EditText) findViewById(R.id.edit_text_otp3);
        otp4 = (EditText) findViewById(R.id.edit_text_otp4);
        otp5 = (EditText) findViewById(R.id.edit_text_otp5);
        otp6 = (EditText) findViewById(R.id.edit_text_otp6);

        resendOtp = (TextView) findViewById(R.id.text_view_resend_otp);
        displayPhoneNumber = (TextView) findViewById(R.id.tv_phone_number);

        btnVerify = (Button) findViewById(R.id.btn_verify_otp);

        progressBar = (ProgressBar) findViewById(R.id.profressbar_verify_otp);

        // get otp backend from login activity
        otpBackend = getIntent().getStringExtra("backend-otp");
    }

    private void ResendOtp() {
        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + getIntent().getStringExtra("phone-number"),
                        10,
                        TimeUnit.SECONDS,
                        VerifyOTP.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                otpBackend = s; //new otp
                                Toast.makeText(VerifyOTP.this, "OTP sent successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });
    }

    private void VerifyOtp() {
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validate otp input
                if (!otp1.getText().toString().trim().isEmpty()
                        && !otp2.getText().toString().trim().isEmpty()
                        && !otp3.getText().toString().trim().isEmpty()
                        && !otp4.getText().toString().trim().isEmpty()
                        && !otp5.getText().toString().trim().isEmpty()
                        && !otp6.getText().toString().trim().isEmpty()) {

                    String enteredOtp = otp1.getText().toString().trim() +
                            otp2.getText().toString().trim() +
                            otp3.getText().toString().trim() +
                            otp4.getText().toString().trim() +
                            otp5.getText().toString().trim() +
                            otp6.getText().toString().trim();

                    if (otpBackend != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        btnVerify.setVisibility(View.INVISIBLE);

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpBackend, enteredOtp);

                        FirebaseAuth.getInstance().signInWithCredential(credential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressBar.setVisibility(View.GONE);
                                        btnVerify.setVisibility(View.VISIBLE);

                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(VerifyOTP.this, "Enter the correct OTP", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(VerifyOTP.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(VerifyOTP.this, "Please enter all numbers", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void MoveToNextOtpField() {
        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    otp5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    otp6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}