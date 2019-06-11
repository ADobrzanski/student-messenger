package com.ag_kb_ad.studentmessenger;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MessageListActivity extends AppCompatActivity implements EventListener<QuerySnapshot> {
    private final static String TAG = "Message sending";
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private FirebaseAuth messagesAuth;
    private FirebaseFirestore messagesBase;
    private Query messageSearch;
    private ArrayList<BaseMessage> messages;
    private String path;
    private Button sendButton;
    private TextView messageField;



    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);


        messageField = findViewById(R.id.edittext_chatbox);
        sendButton = findViewById(R.id.button_chatbox_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> data = new HashMap<>();
                String message = messageField.getText().toString();
                data.put("message", message);
                data.put("timestamp", Timestamp.now());
                data.put("uid", messagesAuth.getCurrentUser().getUid());

                messagesBase.document(path)
                        .collection("messages")
                        .document()
                        .set(data)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        });
        messagesAuth = FirebaseAuth.getInstance();
        messagesBase = FirebaseFirestore.getInstance();
        path = getIntent().getExtras().getString("path");
        Log.d("PATH", path);
        messages = new ArrayList<>();
        messageSearch = messagesBase.document(path)
                .collection("messages")
                .orderBy("timestamp"
                        , Query.Direction.DESCENDING);
        messageSearch.addSnapshotListener(this);


        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        mMessageRecycler.setLayoutManager(layoutManager);
        mMessageRecycler.setAdapter(mMessageAdapter);
    }

    @Override
    public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
        Log.d("TAAAG", "co jest!");
        if (e != null) {
            Log.w("FriendRequestsFragment", "Listen failed.", e);
            return;
        }

        if (snapshot == null) return;
        if (snapshot.getQuery() == messageSearch) {
            handleReceivedMessages(snapshot);
        }
    }

    private void handleReceivedMessages(QuerySnapshot snapshot) {
        boolean shouldUpdateNow = (snapshot.size() < 3);
        for (DocumentChange dc : snapshot.getDocumentChanges()) {
            QueryDocumentSnapshot doc = dc.getDocument();

            switch (dc.getType()) {
                case ADDED:
                    messages.add(0, new BaseMessage()
                            .setUserId(doc.getString("uid"))
                            .setMessage(doc.getString("message"))
                            .setCreatedAt(doc.getTimestamp("timestamp").toDate()));

                    if (shouldUpdateNow) {
                        mMessageAdapter.setDataset(messages);
                        mMessageAdapter.notifyItemInserted(messages.size() - 1);
                    }
                    break;
                case MODIFIED:
                    break;
                case REMOVED:
                    break;
            }
        }
        if (!shouldUpdateNow) {
            mMessageAdapter.setDataset(messages);
            mMessageAdapter.notifyDataSetChanged();
        }
    }
}