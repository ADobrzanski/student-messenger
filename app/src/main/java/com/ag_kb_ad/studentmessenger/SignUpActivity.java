package com.ag_kb_ad.studentmessenger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private TextView txt_email, txt_name, txt_password, txt_password_repeat;
    private Button btn_sign_up, btn_back;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        bindViews();
        bindEventListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(this, SampleUserActivity.class);
            finish();
            startActivity(intent);
        }
    }

    private void bindViews(){
        txt_email = findViewById(R.id.txt_email);
        txt_name = findViewById(R.id.txt_name);
        txt_password = findViewById(R.id.txt_password);
        txt_password_repeat = findViewById(R.id.txt_password_repeat);

        btn_sign_up = findViewById(R.id.btn_sign_up);
        btn_back = findViewById(R.id.btn_back);
    }

    private void bindEventListeners() {
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivity.this.finish();
            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSingIn();
            }
        });

//        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//                if(currentUser != null){
//                    appendNameToCreatedProfile(currentUser);
//                }
//            }
//        });
    }

    private boolean isFormValidatedSuccessfuly(){
        String name = txt_name.getText().toString();

        if(name.trim().isEmpty()){
            Toast.makeText(this, "Name cannot be empty",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        String password = txt_password.getText().toString();
        String password_repeat = txt_password_repeat.getText().toString();

        if(!password.equals(password_repeat)){
            Toast.makeText(this, "Passwords do not match",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void onClickSingIn(){
        if(!isFormValidatedSuccessfuly()) return;

        String email = txt_email.getText().toString();
        String password = txt_password.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if(currentUser != null){
                                appendNameToCreatedProfile(currentUser);
                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void appendNameToCreatedProfile(FirebaseUser currentUser){
        String display_name = txt_name.getText().toString();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(display_name)
                .build();

        currentUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            Intent intent = new Intent(SignUpActivity.this, SampleUserActivity.class);
                            SignUpActivity.this.finish();
                            startActivity(intent);
                        }
                    }
                });
    }
}
