package com.example.logicmed;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private List<String> messagesIds;
    private String senderUId;
    private String receiverUid;

    public Chat() {

    }
    public Chat(String senderUId, String receiverUid) {
        this.messagesIds = new ArrayList<>();
        this.senderUId = senderUId;
        this.receiverUid = receiverUid;
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

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }
}
