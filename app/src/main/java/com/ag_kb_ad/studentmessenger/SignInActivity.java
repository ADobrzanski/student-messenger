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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {

    private final static String TAG = "SignInActivity";
    private static final int RC_SIGN_IN_GOOGLE = 101;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private CallbackManager mCallbackManager;

    private TextView txt_email, txt_password;
    private Button btn_sign_in, btn_sign_up, btn_sign_in_google, btn_sign_in_facebook;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mCallbackManager = CallbackManager.Factory.create();

        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        btn_sign_in_google = findViewById(R.id.btn_sing_in_google);
        btn_sign_in_facebook = findViewById(R.id.btn_sign_in_facebook);

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignInEmail();
            }
        });

        btn_sign_in_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignInGoogle();
            }
        });

        btn_sign_in_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignInFacebook();
            }
        });

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(SignInActivity.this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                AccessToken accessToken = loginResult.getAccessToken();
                handleFacebookAccessToken(accessToken);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Error during facebook auth", error.getCause());
                Toast.makeText(SignInActivity.this, "Error occured!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSignUp();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) startActivity(new Intent(this, MainActivity.class));

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn){
            handleFacebookAccessToken(accessToken);
        }
    }

    private void onClickSignInEmail(){
        String email = txt_email.getText().toString();
        String password = txt_password.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        }else{
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void onClickSignInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    private void onClickSignInFacebook(){
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
    }

    private void onClickSignUp(){
        startActivity(new Intent(this, SignUpActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }else{
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "firebaseAuthWithGoogle failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
