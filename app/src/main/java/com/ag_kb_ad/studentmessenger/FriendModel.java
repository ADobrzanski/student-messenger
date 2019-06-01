package com.ag_kb_ad.studentmessenger;

public class FriendModel {
    private String uid;
    private String displaName;
    private String avatarURL;

    public FriendModel setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public FriendModel setDisplaName(String displaName) {
        this.displaName = displaName;
        return this;
    }

    public FriendModel setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public String getDisplaName() {
        return displaName;
    }

    public String getAvatarURL() {
        return avatarURL;
    }
}
