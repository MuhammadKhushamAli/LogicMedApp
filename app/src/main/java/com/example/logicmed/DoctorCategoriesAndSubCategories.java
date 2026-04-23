package com.example.logicmed;

import java.util.ArrayList;
import java.util.Locale;

public class DoctorCategoriesAndSubCategories {
    private final String category;
    private final ArrayList<String> subCategories;

    public DoctorCategoriesAndSubCategories(String category, ArrayList<String> subCategories) {
        this.category = category;
        this.subCategories = subCategories;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<String> getSubCategories() {
        return subCategories;
    }
}
