package com.example.logicmed;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private List<String> messagesIds;
    private String senderUId;
    private String receiverUId;

    public Chat() {

    }
    public Chat(String senderUId, String receiverUId) {
        this.messagesIds = new ArrayList<>();
        this.senderUId = senderUId;
        this.receiverUId = receiverUId;
    }

    public List<String> getMessagesIds() {
        return messagesIds;
    }

    public void setMessagesIds(List<String> messagesIds) {
        this.messagesIds = messagesIds;
    }

    public String getSenderUId() {
        return senderUId;
    }

    public void setSenderUId(String senderUId) {
        this.senderUId = senderUId;
    }

    public String getReceiverUId() {
        return receiverUId;
    }

    public void setReceiverUId(String receiverUid) {
        this.receiverUId = receiverUid;
    }
}
