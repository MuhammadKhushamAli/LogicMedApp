package com.example.logicmed;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.UploadRequest;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Query;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private ContentLoadingProgressBar contentLoadingProgressBar;
    private ImageButton ibBack;
    private ImageView ivProfile;
    private TextView tvName;
    private RecyclerView rvMessages;
    private ImageButton ibUpload;
    private ImageButton ibCamera;
    private ImageButton ibSend;
    private TextInputEditText etMessage;
    private String chatId;
    private MyApplication app;
    private  MessageRecyclerAdapter messageRecyclerAdapter;
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private ActivityResultLauncher<Intent> uploadActivityResultLauncher;
    private final int CAMERA_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        getChatAndUpdate();
        sendMessage();
        ibBack.setOnClickListener(v -> {
            finish();
        });
        ibUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            uploadActivityResultLauncher.launch(intent);
        });
        ibCamera.setOnClickListener(v -> {
            checkCameraPermission();
        });
    }

    private void init() {
        contentLoadingProgressBar = findViewById(R.id.chat_progress_bar);
        ibBack = findViewById(R.id.chat_back_arrow);
        ivProfile = findViewById(R.id.chat_profile);
        tvName = findViewById(R.id.chat_name);
        rvMessages = findViewById(R.id.chat_messages);
        ibUpload = findViewById(R.id.chat_upload_btn);
        ibCamera = findViewById(R.id.chat_camera_btn);
        ibSend = findViewById(R.id.chat_send_btn);
        etMessage = findViewById(R.id.chat_message_input);
        app = (MyApplication) getApplicationContext();

        Intent intent = getIntent();

        chatId = intent.getStringExtra(KeyUtils.chatUIDIntentKey);
        Glide.with(this)
                .load(intent.getStringExtra(KeyUtils.userProfileUrlIntentKey))
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(ivProfile);

        tvName.setText(intent.getStringExtra(KeyUtils.userNameIntentKey));

        cameraActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) Objects.requireNonNull(result.getData().getExtras()).get("data");
                        uploadToCloudinary(bitmap, null);
                    }
                }
        );

        uploadActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        uploadToCloudinary(null, uri);
                    }
                }
        );
    }
    private void getChatAndUpdate() {
        Query query = app.firestore.collection(KeyUtils.firebaseMessageCollectionKey)
                .whereEqualTo(Message.CHAT_ID_FIELD, chatId)
                .orderBy(Message.TIMESTAMP_FIELD, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        messageRecyclerAdapter = new MessageRecyclerAdapter(options, this);
        messageRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                rvMessages.smoothScrollToPosition(0);
            }
        });
        rvMessages.setHasFixedSize(true);
        rvMessages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
    }
    private void sendMessage() {
        ibSend.setOnClickListener(v -> {
            String message = Objects.requireNonNull(etMessage.getText()).toString();
            FirebaseUser firebaseUser = app.firebaseAuth.getCurrentUser();
            String senderName = app.sPrefUser.getString(KeyUtils.namePrefKey, "");
            etMessage.setText(null);

            if (firebaseUser == null || senderName.isEmpty() || chatId.isEmpty()) {
                Toast.makeText(this, "Incomplete Credentials, Re-Login", Toast.LENGTH_LONG).show();
                return;
            }

            if (!(message.isEmpty())) {
                Message messageObj = new Message(message, firebaseUser.getUid(), chatId, senderName, false);
                sendMessageToDB(messageObj);
            }
        });
    }

    private void sendMessageToDB(Message message) {
        app.firestore.collection(KeyUtils.firebaseMessageCollectionKey)
                .add(message)
                .addOnCompleteListener(task -> {
                   if (!(task.isSuccessful())) {
                       Toast.makeText(this, "Unable to Send, Try Again", Toast.LENGTH_LONG).show();
                   }
                });
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
    private void uploadToCloudinary(Bitmap bitmap, Uri uri) {
        if (bitmap == null && uri == null) {
            Toast.makeText(this, "Please Upload Image First", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser firebaseUser = app.firebaseAuth.getCurrentUser();
        String senderName = app.sPrefUser.getString(KeyUtils.namePrefKey, "");

        if (firebaseUser == null || senderName.isEmpty() || chatId.isEmpty()) {
            Toast.makeText(this, "Incomplete Credentials, Re-Login", Toast.LENGTH_LONG).show();
            return;
        }

        MediaManager mediaManager = MediaManager.get();
        UploadRequest<?> uploadRequest = null;
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            uploadRequest = mediaManager.upload(bytes);
        } else if (uri != null) {
            uploadRequest = mediaManager.upload(uri);
        }
        if (uploadRequest != null) {
            bitmap = null;
            uri = null;
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
                            String cloudinaryURL = Objects.requireNonNull(resultData.get("secure_url")).toString();
                            Message message = new Message(cloudinaryURL,
                                    firebaseUser.getUid(),
                                    chatId,
                                    senderName,
                                    true);
                            sendMessageToDB(message);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Toast.makeText(ChatActivity.this, "Unable to Upload Image", Toast.LENGTH_LONG).show();
                            return;
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }
                    }).dispatch();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        rvMessages.setAdapter(messageRecyclerAdapter);
        messageRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageRecyclerAdapter.stopListening();
    }
}