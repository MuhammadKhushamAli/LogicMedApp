package com.example.logicmed;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;

public class DoctorDetailActivity extends AppCompatActivity {
    MyApplication app;
    private ImageButton ibGoBack;
    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvFee;
    private RecyclerView rvTimings;
    private RecyclerView rvCategories;
    private FloatingActionButton addMessage;

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
    }

    private void getDoctorData() {
        Intent intent = getIntent();
        String uId = intent.getStringExtra(KeyUtils.doctorsUIDIntentKey);
        if (uId == null || uId.isEmpty()) {
            Toast.makeText(DoctorDetailActivity.this, "Unable to Get ID, Try Again", Toast.LENGTH_LONG).show();
            return;
        }
        app.firestore.collection(KeyUtils.firebaseUserCollectionKey).document(uId)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Doctor doctor = task.getResult().toObject(Doctor.class);

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
}