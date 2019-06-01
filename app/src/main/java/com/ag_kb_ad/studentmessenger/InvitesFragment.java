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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class InvitesFragment extends Fragment implements EventListener<QuerySnapshot> {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private CollectionReference mIncomingInvites;
    private ArrayList<InviteModel> invitesDataset;

    private RecyclerView recyclerView;
    private InvitesAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageButton btn_settings;


    public InvitesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        invitesDataset = new ArrayList<>();

        mIncomingInvites = mFirestore.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("incoming-invites");
        mIncomingInvites.addSnapshotListener(this);

        mAdapter = new InvitesAdapter(invitesDataset);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invites, container, false);
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
            Log.w("InvitesFragment", "Listen failed.", e);
            return;
        }

        try{
            boolean shouldUpdateNow = (snapshot.size() == 1);
            for (DocumentChange dc : snapshot.getDocumentChanges()){
                switch (dc.getType()){
                    case ADDED:
                        QueryDocumentSnapshot doc = dc.getDocument();
                        invitesDataset.add(new InviteModel()
                                .setDisplaName(doc.getString("displayName"))
                                .setAvatarURL(doc.getString("avatarURL")));

                        if(shouldUpdateNow){
                            mAdapter.setDataset(invitesDataset);
                            mAdapter.notifyItemInserted(invitesDataset.size()-1);
                        }
                        break;
                    case MODIFIED:
                        break;
                    case REMOVED:
                        break;
                }
            }

            if(!shouldUpdateNow){
                mAdapter.setDataset(invitesDataset);
                mAdapter.notifyDataSetChanged();
            }
        }catch (Exception e1){
            Log.e("Invites", e1.getMessage());
        }


//        if (snapshot != null && !snapshot.isEmpty()) {
//            snapshot
//        } else {
//            Log.d(TAG, "Current data: null");
//        }
    }
}
