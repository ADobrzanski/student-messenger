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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.FriendRequestViewHolder> {

    private FirebaseFirestore firestore;

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
        firestore = FirebaseFirestore.getInstance();
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
    public void onBindViewHolder(@NonNull FriendRequestViewHolder viewHolder, int i) {
        FriendModel friendModel = mDataset.get(i);

        Picasso.get().load(Uri.parse(friendModel.getAvatarURL())).into(viewHolder.user_avaar);
        viewHolder.user_display_name.setText(friendModel.getDisplaName());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
