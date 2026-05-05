package com.example.logicmed;

public class AiChat {
    String senderName;
    String message;

    public AiChat(String senderName, String message) {
        this.senderName = senderName;
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessage() {
        return message;
    }
}
