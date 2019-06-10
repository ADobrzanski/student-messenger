package com.ag_kb_ad.studentmessenger;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    private TextView txt_email, txt_name, txt_password, txt_password_repeat;
    private Button btn_sign_up, btn_back;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        bindViews();
        bindEventListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(this, MainActivity.class);
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
//                    appendNameToFirestoreProfile(currentUser);
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
                            Log.d(TAG, "createUserWithEmail:success");

                            //lame delay to make sure that user document got created at firestore
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if(currentUser != null){
                                        appendNameToFirebaseProfile(currentUser);
                                        appendNameToFirestoreProfile(currentUser);
                                    }
                                }
                            }, 1500);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private int failureCount = 0;
    private void appendNameToFirestoreProfile(final FirebaseUser currentUser){
        String display_name = txt_name.getText().toString();

        FirebaseUser user = mAuth.getCurrentUser();
        String avatarURL = "https://ui-avatars.com/api/?name=" + display_name.replaceAll("\\s+", "+");

        user.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(display_name)
                .setPhotoUri(Uri.parse(avatarURL))
                .build()
        );

        DocumentReference userFirestoreDocument = mFirestore.collection("users").document(currentUser.getUid());

        userFirestoreDocument
                .update("displayName", display_name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User profile updated.");
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        SignUpActivity.this.finish();
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ++failureCount;
                        if(failureCount < 3){
                            appendNameToFirestoreProfile(currentUser);
                        }
                        Log.d(TAG, "User profile update failed");
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(SignUpActivity.this, "Sth went wrong :<", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void appendNameToFirebaseProfile(FirebaseUser currentUser) {
        String display_name = txt_name.getText().toString();
        final String avatarURL = "https://ui-avatars.com/api/?name=" + display_name.replaceAll("\\s+", "+");

        currentUser.updateProfile(new UserProfileChangeRequest.Builder()
                .setDisplayName(display_name)
                .setPhotoUri(Uri.parse(avatarURL))
                .build()
        );

    }
}
