package com.example.logicmed;

import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    public static final String PARTICIPANTS_FIELD = "participantsUId";
    public static final String SIGNATURE_FIELD = "chatSignature";
    private List<String> participantsUId;
    private String chatSignature;
    private List<ParticipantDetail> participantDetails;

    public Chat() {

    }
    public Chat(List<String> participantsUId, String chatSignature, List<ParticipantDetail> participantDetails) {
        this.participantsUId = participantsUId;
        this.chatSignature = chatSignature;
        this.participantDetails = participantDetails;
    }

    public List<String> getParticipantsUId() {
        return participantsUId;
    }

    public List<ParticipantDetail> getParticipantDetails() {
        return participantDetails;
    }

    public void setParticipantDetails(List<ParticipantDetail> participantDetails) {
        this.participantDetails = participantDetails;
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

    public ParticipantDetail giveOtherOne(String uid) {
        for (ParticipantDetail detail: participantDetails) {
            if (!(detail.getParticipantId().equals(uid))) {
                return detail;
            }
        }
        return null;
    }
}
