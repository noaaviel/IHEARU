package com.example.ihearu.LoginAndMain;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ihearu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private enum STATE {
        PHONE,
        CODE
    }

    private ImageView login_IMG_back;
    private TextInputLayout login_EDT_input;
    private MaterialButton login_BTN_next;

    private STATE state = STATE.PHONE;

    private String phoneInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String name = user.getDisplayName();
        String phn = user.getPhoneNumber();

        if (user != null) {
            // open menu activity
            finish();
            return;
        }

        findViews();
        initViews();

        Glide
                .with(this)
                .load(R.drawable.img_back)
                .centerCrop()
                .into(login_IMG_back);

        updateUI();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("pttt", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("pttt", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(MainActivity.this, "Wrong Code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks onVerificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            Log.d("pttt", "onCodeSent");
            state = STATE.CODE;
            updateUI();
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            Log.d("pttt", "onCodeAutoRetrievalTimeOut " + s);
            super.onCodeAutoRetrievalTimeOut(s);

            state = STATE.PHONE;
            updateUI();
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d("pttt", "PhoneAuthCredential");
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.d("pttt", "FirebaseException " + e.getMessage());
        }
    };

    private void nextClicked() {
        if (state == STATE.PHONE) {
            login_BTN_next.setEnabled(false);
            phoneInput = login_EDT_input.getEditText().getText().toString();
            Log.d("pttt", "phoneInput= " + phoneInput);

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneInput)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(onVerificationStateChangedCallbacks)
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(options);


        } else if (state == STATE.CODE) {
            login_BTN_next.setEnabled(false);
            String codeInput = login_EDT_input.getEditText().getText().toString();

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();
            firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneInput, codeInput);

            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phoneInput)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(onVerificationStateChangedCallbacks)
                    .build();

            PhoneAuthProvider.verifyPhoneNumber(options);

        }
    }

    private void updateUI() {
        if (state == STATE.PHONE) {
            login_BTN_next.setEnabled(true);
            login_EDT_input.setPlaceholderText("+972 55 1234567");
            login_EDT_input.getEditText().setText("");
            login_EDT_input.setHint(getString(R.string.phone_number));
            login_BTN_next.setText(getString(R.string.next));
        } else if (state == STATE.CODE) {
            login_BTN_next.setEnabled(true);
            login_EDT_input.setPlaceholderText("*** ***");
            login_EDT_input.getEditText().setText("");
            login_EDT_input.setHint(getString(R.string.verification_code));
            login_BTN_next.setText(getString(R.string.ok));
        }
    }

    private void initViews() {
        login_BTN_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextClicked();
            }
        });
    }

    private void findViews() {
        login_IMG_back = findViewById(R.id.login_IMG_back);
        login_EDT_input = findViewById(R.id.login_EDT_input);
        login_BTN_next = findViewById(R.id.login_BTN_next);
    }

    @Override
    public void onBackPressed() {
        if (state == STATE.PHONE) {
            super.onBackPressed();
        } else if (state == STATE.CODE) {
            state = STATE.PHONE;
            updateUI();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}