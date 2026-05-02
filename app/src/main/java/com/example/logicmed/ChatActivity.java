package com.example.logicmed;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;

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
    }
    private void getChatAndUpdate() {
        Query query = app.firestore.collection(KeyUtils.firebaseMessageCollectionKey)
                .whereEqualTo(Message.CHAT_ID_FIELD, chatId);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        messageRecyclerAdapter = new MessageRecyclerAdapter(options, this);
        rvMessages.setHasFixedSize(true);
        rvMessages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        rvMessages.setAdapter(messageRecyclerAdapter);
        messageRecyclerAdapter.startListening();
    }
    private void sendMessage() {
        ibSend.setOnClickListener(v -> {
            String message = Objects.requireNonNull(etMessage.getText()).toString();
            FirebaseUser firebaseUser = app.firebaseAuth.getCurrentUser();
            String senderName = app.sPrefUser.getString(KeyUtils.namePrefKey, "");

            if (firebaseUser == null || senderName.isEmpty() || chatId.isEmpty()) {
                Toast.makeText(this, "Incomplete Credentials, Re-Login", Toast.LENGTH_LONG).show();
                return;
            }

            if (!(message.isEmpty())) {
                Message messageObj = new Message(message, firebaseUser.getUid(), chatId, senderName, false);
                sendMessageToDB(messageObj, false);
            }
        });
    }

    private void sendMessageToDB(Message message, Boolean isImage) {
        app.firestore.collection(KeyUtils.firebaseMessageCollectionKey)
                .add(message)
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       etMessage.setText("");
                   }
                   else {
                       Toast.makeText(this, "Unable to Send, Try Again", Toast.LENGTH_LONG).show();
                   }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageRecyclerAdapter.stopListening();
    }
}