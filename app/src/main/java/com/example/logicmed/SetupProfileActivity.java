package com.example.logicmed;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.UploadRequest;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.SetOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.Permission;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;

public class SetupProfileActivity extends AppCompatActivity {
    private ImageView ivProfile;
    private ImageButton ibUpload;
    private ImageButton ibCamera;
    private MaterialButton mbSubmit;
    private MaterialButton mbSkip;
    private ProgressBar progressBar;
    private TextInputEditText etFee;
    private TextInputLayout tIFeeLayout;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private ActivityResultLauncher<Intent> uploadActivityResultLauncher;
    private MyApplication app;
    private final int CAMERA_PERMISSION_CODE = 1;
    private Bitmap bitmap;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setup_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();


        ibUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            uploadActivityResultLauncher.launch(intent);
        });
        ibCamera.setOnClickListener(v -> {
            checkCameraPermission();
        });

        mbSkip.setOnClickListener(v -> {
            startActivity(
                    new Intent(
                            this,
                            MainActivity.class
                    )
            );
        });

        mbSubmit.setOnClickListener(v -> {
            if (bitmap != null || uri != null) {
                uploadToCloudinaryAndMaintainDB();
            }
            else {
                Toast.makeText(SetupProfileActivity.this, "Go for Skip", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init() {
        ivProfile = findViewById(R.id.setup_profile_profile_img);
        ibCamera = findViewById(R.id.setup_profile_camera_btn);
        ibUpload = findViewById(R.id.setup_profile_upload_image_btn);
        mbSubmit = findViewById(R.id.setup_profile_submit_btn);
        mbSkip = findViewById(R.id.setup_profile_skip_btn);
        progressBar = findViewById(R.id.setup_profile_progress_bar);
        progressBar.setVisibility(View.GONE);
        etFee = findViewById(R.id.setup_profile_doc_fee);
        tIFeeLayout = findViewById(R.id.setup_profile_doc_fee_layout);
        app = (MyApplication) getApplicationContext();

        if(app.sPrefUser.getString(KeyUtils.rolePrefKey, KeyUtils.patientKey).equals(KeyUtils.doctorKey)) {
            tIFeeLayout.setVisibility(View.VISIBLE);
        }
        else {
            tIFeeLayout.setVisibility(View.GONE);
        }


        bitmap = null;
        uri = null;

        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        bitmap = (Bitmap) Objects.requireNonNull(result.getData().getExtras()).get("data");
                        ivProfile.setImageBitmap(bitmap);
                    }
                }
        );

        uploadActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        uri = result.getData().getData();
                        ivProfile.setImageURI(uri);
                    }
                }
        );
    }
    private void uploadToCloudinaryAndMaintainDB() {
        if (app.sPrefUser.getString(KeyUtils.rolePrefKey, KeyUtils.patientKey).equals(KeyUtils.doctorKey)) {
            if (Objects.requireNonNull(etFee.getText()).toString().isEmpty()) {
                Toast.makeText(SetupProfileActivity.this, "Fee is Required", Toast.LENGTH_LONG).show();
                return;
            }
        }
        progressBar.setVisibility(View.VISIBLE);
        MediaManager mediaManager = MediaManager.get();
        UploadRequest<?> uploadRequest = null;
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            uploadRequest = mediaManager.upload(bytes);
        }
        else if (uri != null) {
            uploadRequest = mediaManager.upload(uri);
        }
        if (uploadRequest != null) {
            uploadRequest.unsigned("logicmed")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {

                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {

                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            MyApplication app = (MyApplication) getApplicationContext();
                            FirebaseUser user = app.firebaseAuth.getCurrentUser();
                            if (user != null) {
                                String uID = user.getUid();

                                String cloudinaryUrl = Objects.requireNonNull(resultData.get("secure_url")).toString();

                                Map<String, Object> dataMap = new HashMap<>();

                                if (app.sPrefUser.getString(KeyUtils.rolePrefKey, KeyUtils.patientKey).equals(KeyUtils.doctorKey)) {
                                    dataMap.put(Doctor.FEE_FIELD, Float.parseFloat(String.valueOf(etFee.getText())));
                                }

                                dataMap.put(User.PROFILE_IMG_URL_FIELD, cloudinaryUrl);

                                app.firestore.collection(KeyUtils.firebaseUserCollectionKey)
                                        .document(uID)
                                        .update(dataMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    app.sPrefUserEdit.putString(KeyUtils.profileUrlPrefKey, cloudinaryUrl).apply();
                                                }
                                                else {
                                                    Toast.makeText(SetupProfileActivity.this, "Unable to Update Profile", Toast.LENGTH_LONG).show();
                                                }
                                                progressBar.setVisibility(View.GONE);
                                                startActivity(
                                                        new Intent(
                                                                SetupProfileActivity.this,
                                                                MainActivity.class
                                                        )
                                                );
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Toast.makeText(SetupProfileActivity.this, "Unable to Upload Image", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }
                    }).dispatch();
        }
    }
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
        else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE
            );
        }
    }
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraActivityResultLauncher.launch(intent);
    }
}