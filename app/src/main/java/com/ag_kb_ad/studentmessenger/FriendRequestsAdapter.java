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

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.FriendRequestViewHolder> {

    public enum ItemType {
        REQUEST,
        SEARCH_RESULT
    }

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private ItemType itemType;

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public class FriendRequestViewHolder extends RecyclerView.ViewHolder{
        ImageView user_avaar;
        TextView user_display_name;
        ImageButton button_send_request;
        ImageButton button_accept;
        ImageButton button_reject;

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            user_avaar = itemView.findViewById(R.id.user_avatar);
            user_display_name = itemView.findViewById(R.id.user_display_name);
            button_send_request = itemView.findViewById(R.id.btn_send_request);
            button_accept = itemView.findViewById(R.id.btn_accept);
            button_reject = itemView.findViewById(R.id.btn_reject);
        }
    }

    private ArrayList<FriendModel> mDataset;

    public FriendRequestsAdapter(ArrayList<FriendModel> searchResults){
        mDataset = searchResults;
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // default value for item display format is request
        itemType = ItemType.REQUEST;
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

        if(itemType == ItemType.REQUEST){
            viewHolder.button_accept.setVisibility(View.VISIBLE);
            viewHolder.button_reject.setVisibility(View.VISIBLE);
            viewHolder.button_send_request.setVisibility(View.GONE);
        }else{
            viewHolder.button_accept.setVisibility(View.GONE);
            viewHolder.button_reject.setVisibility(View.GONE);
            viewHolder.button_send_request.setVisibility(View.VISIBLE);
        }

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
                
                conversation.put("lastMessage", "");
                conversation.put("timestamp", Timestamp.now());

                conversation.put(userUid, mAuth.getCurrentUser().getDisplayName());
                conversation.put(userUid+"_img", mAuth.getCurrentUser().getPhotoUrl());

                conversation.put(issuerUid, mDataset.get(viewHolder.getAdapterPosition()).getDisplaName());
                conversation.put(issuerUid+"_img", mDataset.get(viewHolder.getAdapterPosition()).getAvatarURL());


                transaction.set(newConversation, conversation);
                transaction.commit();
            }
        });

        viewHolder.button_send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String personFoundUid = mDataset.get(viewHolder.getAdapterPosition()).getUid();
                final FirebaseUser user = mAuth.getCurrentUser();

                DocumentReference invitation = mFirestore.collection("users")
                        .document(personFoundUid)
                        .collection("friend-requests-incoming")
                        .document(user.getUid());

                HashMap<String, Object> invitationData = new HashMap<>();
                invitationData.put("avatarURL", user.getPhotoUrl().toString());
                invitationData.put("displayName", user.getDisplayName());
                invitationData.put("timestamp", Timestamp.now());

                invitation.set(invitationData);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
