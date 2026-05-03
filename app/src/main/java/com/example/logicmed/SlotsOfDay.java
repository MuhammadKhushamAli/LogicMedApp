package com.example.logicmed;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class SlotsOfDay implements Parcelable {
    private String day;
    private List<String> slots;
    protected SlotsOfDay(Parcel in) {
        this.day = in.readString();
        this.slots = in.createStringArrayList();
    }

    public SlotsOfDay(String day, List<String> slots) {
        this.day = day;
        this.slots = slots;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<String> getSlots() {
        return slots;
    }

    public void setSlots(List<String> slots) {
        this.slots = slots;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(day);
        parcel.writeStringList(slots);

    }
    public static final Creator<SlotsOfDay> creator = new Creator<SlotsOfDay>() {
        @Override
        public SlotsOfDay createFromParcel(Parcel parcel) {
            return new SlotsOfDay(parcel);
        }

        @Override
        public SlotsOfDay[] newArray(int i) {
            return new SlotsOfDay[i];
        }
    };
}
