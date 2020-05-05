package com.example.puneet2singla.chatapp;


import com.google.firebase.database.Exclude;

public class ChatMessage {
    String msg;
    String sender;
    @Exclude
    boolean sentByMe;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Exclude
    String key;

    public ChatMessage(){}

    public ChatMessage(String sender, String msg){
        this.msg = msg;
        this.sender = sender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isSentByMe() {
        return sentByMe;
    }

    public void setSentByMe(boolean sentByMe) {
        this.sentByMe = sentByMe;
    }
}
