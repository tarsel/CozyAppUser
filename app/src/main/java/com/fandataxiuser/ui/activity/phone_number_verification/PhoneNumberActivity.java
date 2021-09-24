package com.fandataxiuser.ui.activity.phone_number_verification;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.fandataxiuser.R;
import com.fandataxiuser.data.SharedHelper;
import com.fandataxiuser.ui.activity.register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhoneNumberActivity extends AppCompatActivity {

    @BindView(R.id.etPhoneNumber)
    EditText etPhoneNumber;

    @BindView(R.id.btnSendOTP)
    AppCompatButton btnSendOTP;


    FirebaseAuth mAuth;

    String codeSend;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();


        initUI();

    }

    private void initUI() {

        // send verification code

        btnSendOTP.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (TextUtils.isEmpty(etPhoneNumber.getText().toString())) {
                    Toast.makeText(PhoneNumberActivity.this, "Phone number required .", Toast.LENGTH_SHORT).show();
                } else if (etPhoneNumber.getText().toString().length() < 10) {
                    Toast.makeText(PhoneNumberActivity.this, "Insert valid phone number", Toast.LENGTH_SHORT).show();
                } else {


                    sendVerificationCode(etPhoneNumber.getText().toString());

                }

            }
        });


    }


    private void sendVerificationCode(String phoneNumber) {


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                30,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


        etPhoneNumber.setText("");

        showAlertDialog();


    }

    private void showAlertDialog() {


        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.verify_number_dialog);


        Button dialogButton = dialog.findViewById(R.id.btnVerify);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        EditText edtVerification_code = dialog.findViewById(R.id.edtVerification_code);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(edtVerification_code.getText().toString())) {

                    Toast.makeText(PhoneNumberActivity.this, "Enter Verification code .", Toast.LENGTH_SHORT).show();

                } else {
                    verifyPhoneNumber(edtVerification_code.getText().toString());

                }
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {


        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSend = s;


            Toast.makeText(PhoneNumberActivity.this, "Check your phone for OTP code ", Toast.LENGTH_SHORT).show();

        }
    };

    private void verifyPhoneNumber(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSend, code);

        signInWithPhoneAuthCredential(credential);


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = task.getResult().getUser();

                            System.out.println("Phone_num" + user.getPhoneNumber());

                            Toast.makeText(PhoneNumberActivity.this, "Your number is verified .", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            SharedHelper.putKey(PhoneNumberActivity.this,"phone_with_code",user.getPhoneNumber());

                            Intent intent = new Intent(PhoneNumberActivity.this, RegisterActivity.class);
                            startActivity(intent);


                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(PhoneNumberActivity.this, "Verification failed .", Toast.LENGTH_SHORT).show();

                            }
                        }


                        if (!task.isSuccessful()){
                            Toast.makeText(PhoneNumberActivity.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
