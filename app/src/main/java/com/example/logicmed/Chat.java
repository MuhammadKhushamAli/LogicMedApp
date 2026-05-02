package com.example.logicmed;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    public static final String PARTICIPANTS_FIELD = "participantsUId";
    private List<String> messagesIds;
    private List<String> participantsUId;

    public Chat() {

    }
    public Chat(List<String> participantsUId) {
        this.messagesIds = new ArrayList<>();
        this.participantsUId = participantsUId;
    }

    public List<String> getMessagesIds() {
        return messagesIds;
    }

    public void setMessagesIds(List<String> messagesIds) {
        this.messagesIds = messagesIds;
    }

    public List<String> getParticipantsUId() {
        return participantsUId;
    }

    public void setParticipantsUId(List<String> participantsUId) {
        this.participantsUId = participantsUId;
    }
}
