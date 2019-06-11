package com.ag_kb_ad.studentmessenger;

import android.content.Context;

import java.util.Date;

public class BaseMessage {
    public String message;
    public Date createdAt;
    public String userId;
    public String nickname;

    public BaseMessage() {

    }

    public BaseMessage(String message,  Date createdAt) {
        this.message = message;
        this.createdAt = createdAt;}

    public String getMessage() {
        return message;
    }

    public BaseMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public BaseMessage setCreatedAt(Date createdAt) {
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