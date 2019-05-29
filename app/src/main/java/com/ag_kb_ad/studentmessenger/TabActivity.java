package com.ag_kb_ad.studentmessenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TabActivity extends AppCompatActivity {

    private static final String TAG = "TabActivity";
    private FirebaseAuth mAuth;
    
    private TextView txt_user_data;
    private Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        mAuth = FirebaseAuth.getInstance();
        
        txt_user_data = findViewById(R.id.txt_user_data);
        btn_logout = findViewById(R.id.btn_log_out);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
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
}
