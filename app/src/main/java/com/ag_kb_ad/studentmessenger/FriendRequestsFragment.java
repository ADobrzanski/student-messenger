package com.ag_kb_ad.studentmessenger;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jakewharton.rxbinding3.widget.RxTextView;

import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class FriendRequestsFragment extends Fragment implements EventListener<QuerySnapshot> {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private Query mIncomingFriendRequests;
    private Query mFriendSerach;
    private ArrayList<FriendModel> friendRequests;
    private ArrayList<FriendModel> searchResult;

    private Disposable exitTextDisposable;

    private RecyclerView recyclerView;
    private FriendRequestsAdapter mAdapter;
    private boolean requestMode;
    private RecyclerView.LayoutManager layoutManager;

    private ImageButton btn_settings;
    private EditText txt_search;


    public FriendRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        friendRequests = new ArrayList<>();
        searchResult = new ArrayList<>();

        requestMode = true;

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

        txt_search = view.findViewById(R.id.txt_search);
        exitTextDisposable = RxTextView.textChanges(txt_search)
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence){
                        Log.d("DEBOUNCE", "myyyk");
                        if(charSequence.length() > 0){
                            requestMode = false;
                            searchFriendsByDisplayName(charSequence.toString());
                        }else{
                            requestMode = true;
                            mAdapter.setDataset(friendRequests);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        exitTextDisposable.dispose();
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

                    if(shouldUpdateNow && requestMode){
                        mAdapter.setDataset(friendRequests);
                        mAdapter.setItemType(FriendRequestsAdapter.ItemType.REQUEST);
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
                    if(shouldUpdateNow && requestMode){
                        mAdapter.setDataset(friendRequests);
                        mAdapter.notifyItemRemoved(removedItemIndex);
                    }
                    break;
            }
        }

        if(!shouldUpdateNow && requestMode){
            mAdapter.setDataset(friendRequests);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void searchFriendsByDisplayName(String displayName){
        mFriendSerach = mFirestore.collection("users")
                .orderBy("displayName")
                .whereGreaterThanOrEqualTo("displayName", displayName)
                .whereLessThanOrEqualTo("displayName", displayName+'\uf8ff');

        mFriendSerach.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                searchResult.clear();
                for(DocumentSnapshot dc : queryDocumentSnapshots){
                    searchResult.add(
                            new FriendModel().setDisplaName(dc.getString("displayName"))
                                    .setAvatarURL(dc.getString("avatarURL"))
                                    .setUid(dc.getId())
                    );
                }
                mAdapter.setDataset(searchResult);
                mAdapter.setItemType(FriendRequestsAdapter.ItemType.SEARCH_RESULT);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
