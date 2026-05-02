package com.example.logicmed;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.List;

public class Message {
    public static final String CHAT_ID_FIELD = "chatId";
    public static final String TIMESTAMP_FIELD = "timeStamps";
    private String message;
    private String senderId;
    private String chatId;
    private String senderName;
    private Boolean isImage;
    @ServerTimestamp
    private Timestamp timeStamps;

    public Message() {

    }

    public Message(String message, String senderId, String chatId, String senderName, Boolean isImage) {
        this.message = message;
        this.senderId = senderId;
        this.chatId = chatId;
        this.senderName = senderName;
        this.isImage = isImage;
        this.timeStamps = null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Boolean getIsImage() {
        return isImage;
    }

    public void setIsImage(Boolean image) {
        isImage = image;
    }

    public Timestamp getTimeStamps() {
        return timeStamps;
    }

    public void setTimeStamps(Timestamp timeStamps) {
        this.timeStamps = timeStamps;
    }
}
