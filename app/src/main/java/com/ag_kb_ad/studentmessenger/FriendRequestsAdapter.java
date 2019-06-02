package com.ag_kb_ad.studentmessenger;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.FriendRequestViewHolder> {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public class FriendRequestViewHolder extends RecyclerView.ViewHolder{
        ImageView user_avaar;
        TextView user_display_name;
        ImageButton button_accept;
        ImageButton button_reject;

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            user_avaar = itemView.findViewById(R.id.user_avatar);
            user_display_name = itemView.findViewById(R.id.user_display_name);
            button_accept = itemView.findViewById(R.id.btn_accept);
            button_reject = itemView.findViewById(R.id.btn_reject);
        }
    }

    private ArrayList<FriendModel> mDataset;

    public FriendRequestsAdapter(ArrayList<FriendModel> searchResults){
        mDataset = searchResults;
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    public void setDataset(ArrayList<FriendModel> searchResults){
        mDataset = searchResults;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.item_friend_request, viewGroup, shouldAttachToParentImmediately);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendRequestViewHolder viewHolder, int i) {
        final FriendModel friendModel = mDataset.get(i);

        Picasso.get().load(Uri.parse(friendModel.getAvatarURL())).into(viewHolder.user_avaar);
        viewHolder.user_display_name.setText(friendModel.getDisplaName());
        viewHolder.button_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String issuerUid = mDataset.get(viewHolder.getAdapterPosition()).getUid();

                DocumentReference invitation = mFirestore.collection("users")
                        .document(mAuth.getCurrentUser().getUid())
                        .collection("friend-requests-incoming")
                        .document(issuerUid);

                invitation.delete();
            }
        });
        viewHolder.button_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String issuerUid = mDataset.get(viewHolder.getAdapterPosition()).getUid();
                final String userUid = mAuth.getCurrentUser().getUid();
                WriteBatch transaction = mFirestore.batch();

                DocumentReference invitation = mFirestore.collection("users")
                        .document(userUid)
                        .collection("friend-requests-incoming")
                        .document(issuerUid);

                transaction.delete(invitation);

                DocumentReference newConversation = mFirestore.collection("conversations").document();

                HashMap<String, Object> conversation = new HashMap<>();
                conversation.put("name", null);
                ArrayList<String> participants = new ArrayList<String>(){{add(issuerUid); add(userUid);}};
                conversation.put("participants", participants);


                transaction.set(newConversation, conversation);
                transaction.commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
