package com.example.logicmed;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;

public class HomeFragment extends Fragment {
    private TextView tvRole;
    private TextView tvName;
    private TextView tvMonth;
    private ImageButton ibNext;
    private ImageButton ibPrev;
    private ChipGroup cgDated;
    private RecyclerView rvAppointments;
    private MyApplication app;
    private ArrayList<String> monthsOfYear;
    private AppointmentRecyclerAdapter adapter;

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
        getAppointments();
    }

    private void init(View view) {
        tvRole = view.findViewById(R.id.home_card_role);
        tvName = view.findViewById(R.id.home_card_name);
        tvMonth = view.findViewById(R.id.home_card_month);
        ibNext = view.findViewById(R.id.home_card_inc_btn);
        ibPrev = view.findViewById(R.id.home_card_dec_btn);
        cgDated = view.findViewById(R.id.home_card_appointment_dates);
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
    private void getAppointments() {
        Query query = null;
        if (app.sPrefUser.getString(KeyUtils.rolePrefKey, KeyUtils.patientKey).equals(KeyUtils.doctorKey)) {
            query = app.firestore.collection(KeyUtils.firebaseAppointmentCollectionKey)
                    .whereEqualTo(Appointment.DOCTOR_ID_FIELD, app.firebaseAuth.getCurrentUser().getUid());
        }
        else {
            query = app.firestore.collection(KeyUtils.firebaseAppointmentCollectionKey)
                    .whereEqualTo(Appointment.PATIENT_ID_FIELD, app.firebaseAuth.getCurrentUser().getUid());
        }

        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>()
                .setQuery(query, Appointment.class)
                .build();
        Context context = requireContext();
        adapter = new AppointmentRecyclerAdapter(options, context);
        rvAppointments.setHasFixedSize(true);
        rvAppointments.setLayoutManager(new GridLayoutManager(context, 2));
    }

    @Override
    public void onStart() {
        super.onStart();
        rvAppointments.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}