package com.example.logicmed;

import com.google.firebase.firestore.ServerTimestamp;

import java.sql.Timestamp;
import java.util.List;

public class Appointment {
    private String patientId;
    private String doctorId;
    private ParticipantDetail patientDetails;
    private ParticipantDetail doctorDetails;
    private String checkUpFeedBack;
    private String day;
    private String timeSlot;
    @ServerTimestamp
    private Timestamp appointMentTimeStamp;

    public Appointment() {
    }

    public Appointment(String patientId, String doctorId, String day, String timeSlot, ParticipantDetail patientDetails, ParticipantDetail doctorDetails) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.checkUpFeedBack = null;
        this.appointMentTimeStamp = null;
        this.day = day;
        this.timeSlot = timeSlot;
        this.patientDetails = patientDetails;
        this.doctorDetails = doctorDetails;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public ParticipantDetail getPatientDetails() {
        return patientDetails;
    }

    public void setPatientDetails(ParticipantDetail patientDetails) {
        this.patientDetails = patientDetails;
    }

    public ParticipantDetail getDoctorDetails() {
        return doctorDetails;
    }

    public void setDoctorDetails(ParticipantDetail doctorDetails) {
        this.doctorDetails = doctorDetails;
    }

    public String getCheckUpFeedBack() {
        return checkUpFeedBack;
    }

    public void setCheckUpFeedBack(String checkUpFeedBack) {
        this.checkUpFeedBack = checkUpFeedBack;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Timestamp getAppointMentTimeStamp() {
        return appointMentTimeStamp;
    }

    public void setAppointMentTimeStamp(Timestamp appointMentTimeStamp) {
        this.appointMentTimeStamp = appointMentTimeStamp;
    }
}
