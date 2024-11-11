package com.example.finalproject_socialnetwork.model;

import java.text.SimpleDateFormat;

public class Message {
    private String senderUid;
    private String receiverUid;
    private String content;
    private String timestamp;

    //for firebase
    public Message() {}

    public Message(String senderUid, String receiverUid, String content) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.content = content;
        this.timestamp = getCurrentTimestamp();
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    public String getSenderUid() {
        return senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public String getContent() {
        return content;
    }
}
