package com.example.logicmed;

import android.app.Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyApplication extends Application {
    public List<String> cities;
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

    }
}
