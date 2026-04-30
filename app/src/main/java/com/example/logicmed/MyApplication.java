package com.example.logicmed;

import android.app.Application;
import android.content.SharedPreferences;
import android.provider.MediaStore;

import com.cloudinary.android.MediaManager;
import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyApplication extends Application {
    public FirebaseFirestore firestore;
    public FirebaseAuth firebaseAuth;
    public List<String> cities;
    public SharedPreferences sPrefUser;
    public SharedPreferences.Editor sPrefUserEdit;
    public ArrayList<DoctorCategoriesAndSubCategories> doctorsCategoriesAndSubCategories;
    @Override
    public void onCreate() {
        super.onCreate();
        doctorsCategoriesAndSubCategories = new ArrayList<>(
                Arrays.asList(
                    new DoctorCategoriesAndSubCategories(
                        "Primary Care & General Practice",
                        new ArrayList<>(
                            Arrays.asList(
                                    "Family Medicine",
                                    "Internal Medicine (Internists)",
                                    "Pediatricians",
                                    "Geriatricians"
                            )
                        )
                    ),
                    new DoctorCategoriesAndSubCategories(
                        "Internal Medicine Sub-Specialties",
                        new ArrayList<>(
                            Arrays.asList(
                                    "Cardiology",
                                    "Endocrinology",
                                    "Gastroenterology",
                                    "Hematology",
                                    "Infectious Disease",
                                    "Nephrology",
                                    "Oncology",
                                    "Pulmonology",
                                    "Rheumatology"

                            )
                        )
                    ),
                    new DoctorCategoriesAndSubCategories(
                        "Surgical Specialties",
                        new ArrayList<>(
                            Arrays.asList(
                                    "General Surgery",
                                    "Orthopedic Surgery",
                                    "Neurosurgery",
                                    "Cardiothoracic Surgery",
                                    "Plastic & Reconstructive Surgery"
                            )
                        )
                    ),
                    new DoctorCategoriesAndSubCategories(
                        "Specialized Care by System or Patient Type",
                        new ArrayList<>(
                            Arrays.asList(
                                    "Neurology",
                                    "Dermatology",
                                    "Obstetrics & Gynecology (OB-GYN)",
                                    "Ophthalmology",
                                    "Otolaryngology (ENT)",
                                    "Psychiatry",
                                    "Urology"
                            )
                        )
                    ),
                    new DoctorCategoriesAndSubCategories(
                        "Diagnostic & Support Specialties",
                        new ArrayList<>(
                            Arrays.asList(
                                    "Radiology",
                                    "Pathology",
                                    "Anesthesiology",
                                    "Emergency Medicine"
                            )
                        )
                    )
                )
        );

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        sPrefUser = getSharedPreferences(KeyUtils.userPrefFileKey, MODE_PRIVATE);
        sPrefUserEdit = sPrefUser.edit();

        Map<String, Object> cloudinaryInitMap = new HashMap<>();
        cloudinaryInitMap.put("cloud_name", "dlsbqnmnb");
        cloudinaryInitMap.put("secure", true);
        MediaManager.init(this, cloudinaryInitMap);

    }
}
