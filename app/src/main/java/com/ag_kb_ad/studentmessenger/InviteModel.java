package com.ag_kb_ad.studentmessenger;

public class InviteModel {
    private String displaName;
    private String avatarURL;

    public InviteModel setDisplaName(String displaName) {
        this.displaName = displaName;
        return this;
    }

    public InviteModel setAvatarURL(String avatarURL) {
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
