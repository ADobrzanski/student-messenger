package com.ag_kb_ad.studentmessenger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TabActivity extends AppCompatActivity {

    private static final String TAG = "TabActivity";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;


    private TextView txt_user_data;
    private Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        
        txt_user_data = findViewById(R.id.txt_user_data);
        btn_logout = findViewById(R.id.btn_log_out);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                signOutGoogle();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(TabActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        if(currentUser != null){
            try{
                String display_name = currentUser.getDisplayName();
                txt_user_data.setText(display_name);
            }catch(Exception ex){
                Log.e(TAG, ex.getMessage());
            }
        }else{
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void signOutGoogle() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }
}
