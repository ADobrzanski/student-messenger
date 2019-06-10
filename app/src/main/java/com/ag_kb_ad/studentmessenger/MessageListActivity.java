package com.ag_kb_ad.studentmessenger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MessageListActivity extends AppCompatActivity implements EventListener<QuerySnapshot> {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private FirebaseAuth messagesAuth;
    private FirebaseFirestore messagesBase;
    private Query messageSearch;
    private ArrayList<BaseMessage> messages;
//    private ArrayList<BaseMessage> handleIncomingMessages;
    private String path;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messagesAuth = FirebaseAuth.getInstance();
        messagesBase = FirebaseFirestore.getInstance();
        path = getIntent().getExtras().getString("path");
        messages = new ArrayList<>();
        messageSearch = messagesBase.document(path)
                .collection("messages")
                .orderBy("timestamp"
                        , Query.Direction.DESCENDING);
        messageSearch.addSnapshotListener(this);
        setContentView(R.layout.activity_conversation);


        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messages);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
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
                    messages.add(new BaseMessage()
                            .setUserId(doc.getId())
                            .setNickname(doc.getString("displayName"))
                            .setMessage(doc.getString("message"))
                            .setCreatedAt(doc.getLong("date")));

                    if(shouldUpdateNow){
                        mMessageAdapter.setDataset(messages);
                        mMessageAdapter.notifyItemInserted(messages.size()-1);}
                    break;
                case MODIFIED:
                    break;
                case REMOVED:
                    break;
            }
        }
    }
}