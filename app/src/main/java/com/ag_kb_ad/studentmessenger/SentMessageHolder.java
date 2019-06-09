package com.ag_kb_ad.studentmessenger;

import android.icu.text.SimpleDateFormat;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SentMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText, nameText;
    ImageView profileImage;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm");


    SentMessageHolder(@NonNull View itemView) {
        super(itemView);
        messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        nameText = (TextView) itemView.findViewById(R.id.text_message_name);
    }

    void bind(BaseMessage message) {
        messageText.setText(message.getMessage());
        // Format the stored timestamp into a readable String using method.
        timeText.setText(formatter.format(message.getCreatedAt()));
    }
}