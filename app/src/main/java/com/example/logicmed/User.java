package com.example.logicmed;

import java.util.ArrayList;
import java.util.List;

public class User {
    static public final String PROFILE_IMG_URL_FIELD = "profileImageUrl";
    static public final String ROLE_FIELD = "role";
    private String fullName;
    private String profileImageUrl;
    private String role;
    private String city;

    public User() {

    }

    public User(String fullName, String profileImageUrl, String role, String city) {
        this.fullName = fullName;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.city = city;
    }

    public User(String fullName, String role, String city) {
        this(fullName, null, role, city);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
