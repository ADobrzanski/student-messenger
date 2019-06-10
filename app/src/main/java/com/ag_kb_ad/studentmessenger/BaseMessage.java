package com.ag_kb_ad.studentmessenger;

import android.content.Context;

public class BaseMessage {
    public String message;
    public long createdAt;
    public String userId;
    public String nickname;

    public BaseMessage() {

    }

    public BaseMessage(String message,  long createdAt, String userId, String nickname) {
        this.message = message;
        this.createdAt = createdAt;
        this.userId = userId;
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public BaseMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public BaseMessage setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public BaseMessage setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public BaseMessage setNickname(String nickname) {

        this.nickname = nickname;
        return this;
    }
}