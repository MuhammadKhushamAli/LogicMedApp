package com.example.logicmed;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    public static final String PARTICIPANTS_FIELD = "participantsUId";
    public static final String SIGNATURE_FIELD = "chatSignature";
    private List<String> participantsUId;
    private String chatSignature;

    public Chat() {

    }
    public Chat(List<String> participantsUId, String chatSignature) {
        this.participantsUId = participantsUId;
        this.chatSignature = chatSignature;
    }

    public List<String> getParticipantsUId() {
        return participantsUId;
    }

    public void setParticipantsUId(List<String> participantsUId) {
        this.participantsUId = participantsUId;
    }

    public String getChatSignature() {
        return chatSignature;
    }

    public void setChatSignature(String chatSignature) {
        this.chatSignature = chatSignature;
    }
}
