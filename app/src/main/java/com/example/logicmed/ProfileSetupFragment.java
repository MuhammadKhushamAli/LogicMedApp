package com.example.logicmed;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.google.firestore.v1.TargetOrBuilder;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileSetupFragment extends Fragment {
    private ImageView ivProfile;
    private ImageButton ibUpload;
    private ImageButton ibCamera;
    private MaterialButton mbSubmit;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private ActivityResultLauncher<Intent> uploadActivityResultLauncher;
    private MyApplication app;
    private final int CAMERA_PERMISSION_CODE = 1;
    private Bitmap bitmap;
    private Uri uri;
    private Context context;
    private setOnClickListener listener;

    public interface setOnClickListener {
        void onClickListener(Bitmap bitmap, Uri uri);
    }

    public ProfileSetupFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (setOnClickListener) getParentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        ibUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            uploadActivityResultLauncher.launch(intent);
        });
        ibCamera.setOnClickListener(v -> {
            checkCameraPermission();
        });


        mbSubmit.setOnClickListener(v -> {
            listener.onClickListener(bitmap, uri);
        });
    }
    private void init(View view) {
        context = requireContext();
        ivProfile = view.findViewById(R.id.setup_profile_profile_img);
        ibCamera = view.findViewById(R.id.setup_profile_camera_btn);
        ibUpload = view.findViewById(R.id.setup_profile_upload_image_btn);
        mbSubmit = view.findViewById(R.id.setup_profile_submit_btn);
        app = (MyApplication) context.getApplicationContext();

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
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
        else {
            ActivityCompat.requestPermissions(
                    requireActivity(),
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