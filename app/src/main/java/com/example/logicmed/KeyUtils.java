package com.example.logicmed;

import okhttp3.MediaType;

public class KeyUtils {
    static public final String userPrefFileKey = "userPref";
    static public final String isLoggedInPrefKey = "isLoggedInPref";
    static public final String isFirstTimePrefKey = "isFirstTimePref";
    static public final String rolePrefKey = "rolePref";
    static public final String namePrefKey = "namePref";
    static public final String emailPrefKey = "emailPref";
    static public final String profileUrlPrefKey = "profileUrlPref";
    static public final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    static public final String firebaseUserCollectionKey = "users";
    static public final String doctorKey = "Doctor";
    static public final String patientKey = "Patient";
}