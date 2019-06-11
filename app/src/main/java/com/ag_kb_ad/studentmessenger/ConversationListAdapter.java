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

public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.ConversationViewHolder> {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    public class ConversationViewHolder extends RecyclerView.ViewHolder{
        ImageView img_avatar;
        TextView txt_name;
        TextView txt_last_message;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            img_avatar = itemView.findViewById(R.id.img_avatar);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_last_message = itemView.findViewById(R.id.txt_last_message);
        }
    }

    private ArrayList<ConversationModel> mDataset;

    public ConversationListAdapter(ArrayList<ConversationModel> conversations){
        mDataset = conversations;
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
    }

    public void setDataset(ArrayList<ConversationModel> conversations){
        mDataset = conversations;
    }

    @NonNull
    @Override
    public ConversationListAdapter.ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.item_conversation, viewGroup, shouldAttachToParentImmediately);
        return new ConversationListAdapter.ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ConversationListAdapter.ConversationViewHolder viewHolder, int i) {
        final ConversationModel conversationModel = mDataset.get(i);


        Picasso.get().load(Uri.parse(conversationModel.getFriendAvatarURL())).into(viewHolder.img_avatar);
        viewHolder.txt_name.setText(conversationModel.getFriendDisplayName());
        viewHolder.txt_last_message.setText(conversationModel.getLastMessage());

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}
