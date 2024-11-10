package com.example.finalproject_socialnetwork.model;

import java.text.SimpleDateFormat;

public class ChatMessage {
    private String id;
    private String chatId;
    private String authorUid;
    private String content;
    private long timestamp;

    // for firebase
    public ChatMessage() {
    }

    public ChatMessage(String id, String chatId, String authorUid, String content, long timestamp) {
        this.id = id;
        this.chatId = chatId;
        this.authorUid = authorUid;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getChatId() {
        return chatId;
    }

    public String getAuthorUid(){
        return authorUid;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String timeStampTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new java.util.Date(timestamp));
    }
}
