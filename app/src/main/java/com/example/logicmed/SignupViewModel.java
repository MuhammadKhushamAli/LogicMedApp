package com.example.logicmed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignupViewModel extends ViewModel {
    private final MutableLiveData<Integer> currentPage = new MutableLiveData<>();

    public void setCurrentPage(int currentPage) {
        this.currentPage.setValue(currentPage);
    }
    public LiveData<Integer> getCurrentPage() {
        return currentPage;
    }
}
