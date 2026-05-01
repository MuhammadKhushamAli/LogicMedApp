package com.example.logicmed;

public class Message {
    private String message;
    private String senderId;
    private String chatId;
    public Message() {

    }
    public Message(String message, String senderId, String chatId) {
        this.message = message;
        this.senderId = senderId;
        this.chatId = chatId;
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
}
