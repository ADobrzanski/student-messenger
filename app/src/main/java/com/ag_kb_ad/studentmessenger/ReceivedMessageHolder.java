package com.ag_kb_ad.studentmessenger;

import android.icu.text.SimpleDateFormat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import static java.security.AccessController.getContext;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText, nameText;
    ImageView profileImage;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm");


    ReceivedMessageHolder(View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        nameText = (TextView) itemView.findViewById(R.id.text_message_name);
        profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
    }

    void bind(BaseMessage message) {
        messageText.setText(message.getMessage());
        // Format the stored timestamp into a readable String using method.
        timeText.setText(formatter.format(message.getCreatedAt()));
        nameText.setText(message.getSender().getNickname());
    }
}