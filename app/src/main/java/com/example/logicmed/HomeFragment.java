package com.example.logicmed;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment
        implements AppointmentRecyclerAdapter.setOnClickListener {
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
    private ActivityResultLauncher<Intent> voiceActivityResultLauncher;
    private Context context;
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;
    private String currentAppointmentID;
    private String currentAppointmentFeedback;
    private SimpleDateFormat simpleDateFormat;

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
        currentAppointmentID = null;
        currentAppointmentFeedback = null;
        context = requireContext();
        tvRole = view.findViewById(R.id.home_card_role);
        tvName = view.findViewById(R.id.home_card_name);
        tvMonth = view.findViewById(R.id.home_card_month);
        ibNext = view.findViewById(R.id.home_card_inc_btn);
        ibPrev = view.findViewById(R.id.home_card_dec_btn);
        cgDated = view.findViewById(R.id.home_card_appointment_dates);
        rvAppointments = view.findViewById(R.id.home_upcoming_appointments_rv);

        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context)
                .setTitle("Are you done with it?")
                .setPositiveButton("Yes", (a, b) -> {
                    sendFeedBackToFirestore();
                })
                .setNegativeButton("No", (a, b) -> {
                    currentAppointmentFeedback = null;
                });

        voiceActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                        ArrayList<String> matches = result.getData()
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (matches != null && !(matches.isEmpty())) {
                            String mostAppropriateMatch = matches.get(0);
                            materialAlertDialogBuilder.setMessage(mostAppropriateMatch);
                            currentAppointmentFeedback = mostAppropriateMatch;
                            materialAlertDialogBuilder.show();
                        }
                    }
                }
        );

        app = (MyApplication) requireContext().getApplicationContext();

        SharedPreferences sPref = app.sPrefUser;

        monthsOfYear = new ArrayList<>(Arrays.asList(
                "Trust",
                "Clarity",
                "Empathy",
                "Patience",
                "Honesty",
                "Listening",
                "Respect",
                "Precision",
                "Support",
                "Consent",
                "Diligence",
                "Resilience"
        ));

        tvRole.setText(sPref.getString(KeyUtils.rolePrefKey, ""));
        tvName.setText(sPref.getString(KeyUtils.namePrefKey, ""));
        Random random = new Random();
        tvMonth.setText(monthsOfYear.get(random.nextInt(12)));
        simpleDateFormat = new SimpleDateFormat(KeyUtils.dateFormate, Locale.US);

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
        String currentDate = simpleDateFormat.format(new Date());

        Query query = app.firestore.collection(KeyUtils.firebaseAppointmentCollectionKey)
                .whereGreaterThanOrEqualTo(Appointment.DATE_FIELD, currentDate)
                .whereEqualTo(Appointment.STATUS_ID_FIELD, Appointment.PENDING_STATUS);

        if (app.sPrefUser.getString(KeyUtils.rolePrefKey, KeyUtils.patientKey).equals(KeyUtils.doctorKey)) {
            query.whereEqualTo(Appointment.DOCTOR_ID_FIELD, app.firebaseAuth.getCurrentUser().getUid());
        }
        else {
            query.whereEqualTo(Appointment.PATIENT_ID_FIELD, app.firebaseAuth.getCurrentUser().getUid());
        }

        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>()
                .setQuery(query, Appointment.class)
                .build();
        Context context = requireContext();
        adapter = new AppointmentRecyclerAdapter(options, context, this);
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
    @Override
    public void onChangeStatus(String apID) {
        currentAppointmentID = apID;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                .putExtra(RecognizerIntent.EXTRA_PROMPT, "Give Your Feedback Now!");
        try {
            voiceActivityResultLauncher.launch(intent);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No voice app found", Toast.LENGTH_LONG).show();
        }
    }
    private void sendFeedBackToFirestore() {
        if (currentAppointmentFeedback == null || currentAppointmentFeedback.isEmpty()) {
            Toast.makeText(context, "Feedback is Required", Toast.LENGTH_LONG).show();
            return;
        }

        if (currentAppointmentID == null || currentAppointmentID.isEmpty()) {
            Toast.makeText(context, "Appointment ID is Required", Toast.LENGTH_LONG).show();
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put(Appointment.STATUS_ID_FIELD, Appointment.RESOLVED_STATUS);
        map.put(Appointment.CHECKUP_FEEDBACK_FIELD, currentAppointmentFeedback);

        app.firestore.collection(KeyUtils.firebaseAppointmentCollectionKey)
                .document(currentAppointmentID)
                .update(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Appointment Updated Successfully", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(context, "Appointment not Updated Successfully", Toast.LENGTH_LONG).show();
                    }
                });
    }
}