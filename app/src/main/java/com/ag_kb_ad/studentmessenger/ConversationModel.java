package com.ag_kb_ad.studentmessenger;

import java.util.ArrayList;

public class ConversationModel {

    private String name;
    private ArrayList<String> participants;
    private String friendDisplayName;
    private String friendAvatarURL;
    private String friendUid;
    private String lastMessage;

    public ConversationModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public ConversationModel setParticipants(ArrayList<String> participants) {
        this.participants = participants;
        return this;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public ConversationModel setFriendDisplayName(String friendDisplayName) {
        this.friendDisplayName = friendDisplayName;
        return this;
    }

    public String getFriendDisplayName() {
        return friendDisplayName;
    }

    public ConversationModel setFriendAvatarURL(String friendAvatarURL) {
        this.friendAvatarURL = friendAvatarURL;
        return this;
    }

    public String getFriendAvatarURL() {
        return friendAvatarURL;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public ConversationModel setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
        return this;
    }

    public String getFriendUid() {
        return friendUid;
    }

    public ConversationModel setFriendUid(String friendUid) {
        this.friendUid = friendUid;
        return this;
    }
}
