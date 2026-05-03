package com.example.logicmed;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.List;

public class Appointment implements Parcelable {
    public static final String PATIENT_ID_FIELD = "patientId";
    public static final String DOCTOR_ID_FIELD = "doctorId";
    private String patientId;
    private String doctorId;
    private ParticipantDetail patientDetails;
    private ParticipantDetail doctorDetails;
    private String checkUpFeedBack;
    private String day;
    private String date;
    private String timeSlot;
    private String status;
    @ServerTimestamp
    private Timestamp appointMentTimeStamp;

    public Appointment() {
    }
    protected Appointment(Parcel parcel) {
        date = parcel.readString();
        day = parcel.readString();
        timeSlot = parcel.readString();
    }

    public Appointment(String patientId, String doctorId, String date, String day, String timeSlot, ParticipantDetail patientDetails, ParticipantDetail doctorDetails) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.checkUpFeedBack = null;
        this.appointMentTimeStamp = null;
        this.date = date;
        this.day = day;
        this.timeSlot = timeSlot;
        this.patientDetails = patientDetails;
        this.doctorDetails = doctorDetails;
        this.status = "Pending";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(day);
        parcel.writeString(timeSlot);
    }

    public static final Creator<Appointment> creator = new Creator<Appointment>() {
        @Override
        public Appointment createFromParcel(Parcel parcel) {
            return new Appointment(parcel);
        }

        @Override
        public Appointment[] newArray(int i) {
            return new Appointment[i];
        }
    };
}
