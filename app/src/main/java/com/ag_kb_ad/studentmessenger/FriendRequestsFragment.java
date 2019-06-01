package com.ag_kb_ad.studentmessenger;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FriendRequestsFragment extends Fragment implements EventListener<QuerySnapshot> {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private Query mIncomingFriendRequests;
    private ArrayList<FriendModel> friendRequests;

    private RecyclerView recyclerView;
    private FriendRequestsAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageButton btn_settings;


    public FriendRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        friendRequests = new ArrayList<>();

        mIncomingFriendRequests = mFirestore.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("friend-requests-incoming")
                .orderBy("timestamp", Query.Direction.DESCENDING);
        mIncomingFriendRequests.addSnapshotListener(this);

        mAdapter = new FriendRequestsAdapter(friendRequests);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_requests, container, false);
        recyclerView = view.findViewById(R.id.recycler_almost_friends);

        btn_settings = view.findViewById(R.id.btn_settings);
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(getActivity(), SignInActivity.class));
            }
        });


        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w("FriendRequestsFragment", "Listen failed.", e);
            return;
        }

        if(snapshot == null) return;
        if(snapshot.getQuery() == mIncomingFriendRequests){
            handleIncomingFriendRequests(snapshot);
        }
    }

    private void handleIncomingFriendRequests(QuerySnapshot snapshot){
        boolean shouldUpdateNow = (snapshot.size() < 3);

        for (DocumentChange dc : snapshot.getDocumentChanges()){
            QueryDocumentSnapshot doc = dc.getDocument();

            switch (dc.getType()){
                case ADDED:
                    friendRequests.add(new FriendModel()
                            .setUid(doc.getId())
                            .setDisplaName(doc.getString("displayName"))
                            .setAvatarURL(doc.getString("avatarURL")));

                    if(shouldUpdateNow){
                        mAdapter.setDataset(friendRequests);
                        mAdapter.notifyItemInserted(friendRequests.size()-1);
                    }
                    break;
                case MODIFIED:
                    //ignore modify data, not important
                    break;
                case REMOVED:
                    int removedItemIndex = 0;
                    for(FriendModel invitation : friendRequests){
                        if(invitation.getUid().equals(doc.getId())) break;
                        ++removedItemIndex;
                    }
                    if(removedItemIndex >= friendRequests.size()) return;

                    friendRequests.remove(removedItemIndex);
                    if(shouldUpdateNow){
                        mAdapter.setDataset(friendRequests);
                        mAdapter.notifyItemRemoved(removedItemIndex);
                    }
                    break;
            }
        }

        if(!shouldUpdateNow){
            mAdapter.setDataset(friendRequests);
            mAdapter.notifyDataSetChanged();
        }
    }
}
