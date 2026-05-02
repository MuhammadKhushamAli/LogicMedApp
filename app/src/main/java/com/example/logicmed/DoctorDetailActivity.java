package com.example.logicmed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DoctorDetailActivity extends AppCompatActivity {
    MyApplication app;
    private ImageButton ibGoBack;
    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvFee;
    private RecyclerView rvTimings;
    private RecyclerView rvCategories;
    private FloatingActionButton addMessage;
    private ProgressBar progressBar;
    private String uId;
    private Doctor doctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        getDoctorData();
        startConversation();
        ibGoBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void init() {
        app = (MyApplication) getApplicationContext();
        ibGoBack = findViewById(R.id.doctor_detail_go_back_btn);
        ivProfile = findViewById(R.id.doctor_detail_profile_img);
        tvName = findViewById(R.id.doctor_detail_name);
        tvFee = findViewById(R.id.doctor_detail_fee);
        rvTimings = findViewById(R.id.doctor_detail_timings);
        rvCategories = findViewById(R.id.doctor_detail_categories);
        addMessage = findViewById(R.id.doctor_detail_start_conversation_btn);
        progressBar = findViewById(R.id.doctor_detail_progress_bar);
        uId = null;
        doctor = null;
    }
    private void getDoctorData() {
        Intent intent = getIntent();
        uId = intent.getStringExtra(KeyUtils.doctorsUIDIntentKey);
        if (uId == null || uId.isEmpty()) {
            Toast.makeText(DoctorDetailActivity.this, "Unable to Get ID, Try Again", Toast.LENGTH_LONG).show();
            return;
        }
        app.firestore.collection(KeyUtils.firebaseUserCollectionKey).document(uId)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        doctor = task.getResult().toObject(Doctor.class);

                        if (doctor == null) {
                            Toast.makeText(DoctorDetailActivity.this, "Doctor Data Not Found", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (doctor.getProfileImageUrl() != null) {
                            Glide.with(this)
                                    .load(doctor.getProfileImageUrl())
                                    .placeholder(R.drawable.avatar)
                                    .error(R.drawable.avatar)
                                    .into(ivProfile);
                        }
                        tvName.setText(doctor.getFullName());
//
                        String strFee = "Fee: " + doctor.getFee();
                        tvFee.setText(strFee);

                        DoctorTimingAndCategoryAdapter doctorTimingAdapter = new DoctorTimingAndCategoryAdapter(DoctorDetailActivity.this, doctor.getDocTimings());
                        rvTimings.setHasFixedSize(true);
                        rvTimings.setLayoutManager(new GridLayoutManager(DoctorDetailActivity.this, 3));
                        rvTimings.setAdapter(doctorTimingAdapter);

                        DoctorTimingAndCategoryAdapter doctorCategoryAdapter = new DoctorTimingAndCategoryAdapter(DoctorDetailActivity.this, doctor.getDocCategories());
                        rvCategories.setHasFixedSize(true);
                        rvCategories.setLayoutManager(new LinearLayoutManager(DoctorDetailActivity.this, LinearLayoutManager.VERTICAL, false));
                        rvCategories.setAdapter(doctorCategoryAdapter);
                    }
                });
    }
    private void startConversation() {
        addMessage.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            String currentUId = app.firebaseAuth.getUid();

            if(uId == null || currentUId == null || uId.isEmpty() || currentUId.isEmpty()) {
                Toast.makeText(DoctorDetailActivity.this, "User not Found", Toast.LENGTH_LONG).show();
                return;
            }
            String chatSignature = generateChatSignature(currentUId, uId);
            app.firestore.collection(KeyUtils.firebaseChatCollectionKey)
                    .whereArrayContains(Chat.PARTICIPANTS_FIELD, currentUId)
                    .whereEqualTo(Chat.SIGNATURE_FIELD, chatSignature)
                    .get()
                    .addOnCompleteListener(taskOfChats -> {
                        if (taskOfChats.isSuccessful()) {
                            QuerySnapshot querySnapshot = taskOfChats.getResult();
                            if(querySnapshot != null && !(querySnapshot.isEmpty())) {
                                String chatId = querySnapshot.getDocuments().get(0).getId();
                                navigateToChatActivity(chatId);
                            }
                            else {
                                createNewChat();
                            }
                        }
                        else {
                            Toast.makeText(DoctorDetailActivity.this, taskOfChats.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    });
        });
    }

    private void navigateToChatActivity(String chatId) {
        Intent intent = new Intent(DoctorDetailActivity.this, ChatActivity.class);
        intent.putExtra(KeyUtils.chatUIDIntentKey, chatId);
        startActivity(intent);
        finish();
    }

    private void createNewChat() {
        String currentUId = app.firebaseAuth.getUid();

        if(uId == null || currentUId == null || uId.isEmpty() || currentUId.isEmpty()) {
            Toast.makeText(DoctorDetailActivity.this, "User not Found", Toast.LENGTH_LONG).show();
            return;
        }

        String chatSignature = generateChatSignature(currentUId, uId);
        Chat chat = new Chat(new ArrayList<>(
                Arrays.asList(
                        currentUId,
                        uId
                )),
                chatSignature,
                new ArrayList<>(
                        Arrays.asList(
                                new ParticipantDetail(currentUId,
                                        app.sPrefUser.getString(KeyUtils.namePrefKey, ""),
                                        app.sPrefUser.getString(KeyUtils.profileUrlPrefKey, "")
                                ),
                                new ParticipantDetail(uId,
                                        doctor.getFullName(),
                                        doctor.getProfileImageUrl()
                                )
                        )
                )
        );

        app.firestore.collection(KeyUtils.firebaseChatCollectionKey)
                .add(chat)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        navigateToChatActivity(task.getResult().getId());
                    }
                    else {
                        Toast.makeText(DoctorDetailActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }
    private String generateChatSignature(String currentUId, String otherUId) {
        if (currentUId.compareTo(otherUId) < 0) {
            return currentUId + "_" + otherUId;
        }
        else {
            return otherUId + "_" + currentUId;
        }
    }
}