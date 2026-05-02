package com.example.logicmed;

public class ParticipantDetail {
    private String participantId;
    private String name;
    private String profileUrl;
    public ParticipantDetail() {

    }

    public ParticipantDetail(String participantId, String name, String profileUrl) {
        this.participantId = participantId;
        this.name = name;
        this.profileUrl = profileUrl;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
