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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class InvitesAdapter extends RecyclerView.Adapter<InvitesAdapter.InviteViewHolder> {

    public class InviteViewHolder extends RecyclerView.ViewHolder{
        ImageView user_avaar;
        TextView user_display_name;
        ImageButton button_accept;
        ImageButton button_reject;

        public InviteViewHolder(@NonNull View itemView) {
            super(itemView);
            user_avaar = itemView.findViewById(R.id.user_avatar);
            user_display_name = itemView.findViewById(R.id.user_display_name);
            button_accept = itemView.findViewById(R.id.btn_accept);
            button_reject = itemView.findViewById(R.id.btn_reject);
        }
    }

    private ArrayList<InviteModel> mDataset;

    public InvitesAdapter(ArrayList<InviteModel> searchResults){
        mDataset = searchResults;
    }

    public void setDataset(ArrayList<InviteModel> searchResults){
        mDataset = searchResults;
    }

    @NonNull
    @Override
    public InviteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(R.layout.item_invite, viewGroup, shouldAttachToParentImmediately);
        InviteViewHolder viewHolder = new InviteViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InviteViewHolder inviteViewHolder, int i) {
        InviteModel inviteModel = mDataset.get(i);
        Picasso.get().load(Uri.parse(inviteModel.getAvatarURL())).into(inviteViewHolder.user_avaar);
        inviteViewHolder.user_display_name.setText(inviteModel.getDisplaName());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
