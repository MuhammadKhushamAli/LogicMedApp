package com.example.logicmed;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AllAppointmentsFragment extends Fragment implements AllAppointmentsRecyclerAdapter.setOnClickListener {
    private RecyclerView rvAppointments;
    private AllAppointmentsRecyclerAdapter adapter;
    private MyApplication app;
    private ActivityResultLauncher<Intent> voiceActivityResultLauncher;
    private Context context;
    private String currentAppointmentID;
    private String currentAppointmentFeedback;
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;


    public AllAppointmentsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        currentAppointmentID = null;
        currentAppointmentFeedback = null;
        context = requireContext();
        rvAppointments = view.findViewById(R.id.all_appointments_rv);
        app = (MyApplication) context.getApplicationContext();


        Query query = app.firestore.collection(KeyUtils.firebaseAppointmentCollectionKey);
        if (app.sPrefUser.getString(KeyUtils.rolePrefKey, KeyUtils.patientKey).equals(KeyUtils.doctorKey)) {
            query = query.whereEqualTo(Appointment.DOCTOR_ID_FIELD, app.firebaseAuth.getCurrentUser().getUid());
        }
        else {
            query = query.whereEqualTo(Appointment.PATIENT_ID_FIELD, app.firebaseAuth.getCurrentUser().getUid());
        }

        FirestoreRecyclerOptions<Appointment> options = new FirestoreRecyclerOptions.Builder<Appointment>()
                .setQuery(query, Appointment.class)
                .build();
        adapter = new AllAppointmentsRecyclerAdapter(options, context, this);
        rvAppointments.setHasFixedSize(true);
        rvAppointments.setLayoutManager(new GridLayoutManager(context, 2));

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