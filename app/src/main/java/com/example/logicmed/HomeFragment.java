package com.example.logicmed;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeFragment extends Fragment {
    private TextView tvRole;
    private TextView tvName;
    private TextView tvMonth;
    private ImageButton ibNext;
    private ImageButton ibPrev;
    private ChipGroup cgDated;
    private TextView tvHomeCardNoAppointmentPlaceHolder;
    private TextView tvHomeNoAppointmentPlaceHolder;
    private RecyclerView rvAppointments;
    private MyApplication app;
    private ArrayList<String> monthsOfYear;
    private ArrayList<String> appointments;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        monthsIterator();
        appointmentsRenderer();
    }

    private void init(View view) {
        tvRole = view.findViewById(R.id.home_card_role);
        tvName = view.findViewById(R.id.home_card_name);
        tvMonth = view.findViewById(R.id.home_card_month);
        ibNext = view.findViewById(R.id.home_card_inc_btn);
        ibPrev = view.findViewById(R.id.home_card_dec_btn);
        cgDated = view.findViewById(R.id.home_card_appointment_dates);
        tvHomeCardNoAppointmentPlaceHolder = view.findViewById(R.id.home_card_no_upcoming_appointment_placeholder);
        tvHomeNoAppointmentPlaceHolder = view.findViewById(R.id.home_no_upcoming_appointment_placeholder);
        rvAppointments = view.findViewById(R.id.home_upcoming_appointments_rv);

        app = (MyApplication) requireContext().getApplicationContext();

        SharedPreferences sPref = app.sPrefUser;

        monthsOfYear = new ArrayList<>(Arrays.asList(
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December"
        ));
        appointments = new ArrayList<>();

        tvRole.setText(sPref.getString(KeyUtils.rolePrefKey, ""));
        tvName.setText(sPref.getString(KeyUtils.namePrefKey, ""));
        tvMonth.setText(monthsOfYear.get(0));
    }
    private void monthsIterator() {
        ibNext.setOnClickListener(v -> {
            String currentMonth = tvMonth.getText().toString();
            int currentIndex = monthsOfYear.indexOf(currentMonth);
            if (currentIndex < (monthsOfYear.size() - 1))
                tvMonth.setText(monthsOfYear.get(currentIndex + 1));
        });
        ibPrev.setOnClickListener(v -> {
            String currentMonth = tvMonth.getText().toString();
            int currentIndex = monthsOfYear.indexOf(currentMonth);
            if (currentIndex > 0)
                tvMonth.setText(monthsOfYear.get(currentIndex - 1));
        });
    }
    private void appointmentsRenderer() {
        if (appointments.isEmpty()) {
            cgDated.setVisibility(View.GONE);
            rvAppointments.setVisibility(View.GONE);
            tvHomeCardNoAppointmentPlaceHolder.setVisibility(View.VISIBLE);
            tvHomeNoAppointmentPlaceHolder.setVisibility(View.VISIBLE);
        }
        else {
            cgDated.setVisibility(View.VISIBLE);
            rvAppointments.setVisibility(View.VISIBLE);
            tvHomeCardNoAppointmentPlaceHolder.setVisibility(View.GONE);
            tvHomeNoAppointmentPlaceHolder.setVisibility(View.GONE);
        }
    }
}