package com.ag_kb_ad.studentmessenger;

import android.content.Context;

public class BaseMessage {
    public String message;
    public BaseMessage sender;
    public long createdAt;
    public String userId;
    public String nickname;

    public BaseMessage() {

    }

    public BaseMessage(String message, BaseMessage sender, long createdAt) {
        this.message = message;
        this.sender = sender;
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BaseMessage getSender() {
        return sender;
    }

    public void setSender(BaseMessage sender) {
        this.sender = sender;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}