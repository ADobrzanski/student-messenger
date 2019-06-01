package com.ag_kb_ad.studentmessenger;

public class FriendRequestModel {
    private String displaName;
    private String avatarURL;

    public FriendRequestModel setDisplaName(String displaName) {
        this.displaName = displaName;
        return this;
    }

    public FriendRequestModel setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
        return this;
    }

    public String getDisplaName() {
        return displaName;
    }

    public String getAvatarURL() {
        return avatarURL;
    }
}
