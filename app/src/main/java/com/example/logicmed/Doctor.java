package com.example.logicmed;

import java.util.List;

public class Doctor extends User {
    static public final String FEE_FIELD = "fee";
    private float fee;
    private List<String> docCategories;
    private List<Schedule> docTimings;
    private String slotDuration;

    public Doctor() {
        super();

    }

    public Doctor(String fullName, String profileImageUrl, String role, String city, float fee, List<String> docCategories, List<Schedule> docTimings) {
        super(fullName, profileImageUrl, role, city);
        this.fee = fee;
        this.docCategories = docCategories;
        this.docTimings = docTimings;
    }

    public Doctor(String fullName, String role, String city, float fee, List<String> docCategories, List<Schedule> docTimings) {
        super(fullName, role, city);
        this.fee = fee;
        this.docCategories = docCategories;
        this.docTimings = docTimings;
    }

    public Doctor(String fullName, String role, String city, List<String> docCategories, List<Schedule> docTimings) {
        this(fullName, role, city, 0.0f, docCategories, docTimings);
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public List<String> getDocCategories() {
        return docCategories;
    }

    public void setDocCategories(List<String> docCategories) {
        this.docCategories = docCategories;
    }

    public List<Schedule> getDocTimings() {
        return docTimings;
    }

    public void setDocTimings(List<Schedule> docTimings) {
        this.docTimings = docTimings;
    }
}
