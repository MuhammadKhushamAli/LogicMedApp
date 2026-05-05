package com.example.logicmed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIActivity extends AppCompatActivity {
    private ContentLoadingProgressBar contentLoadingProgressBar;
    private ImageButton ibBack;
    private RecyclerView rvMessages;
    private ImageButton ibSend;
    private TextInputEditText etMessage;
    private String prevChatId;
    private MyApplication app;
    private  MessageRecyclerAdapter messageRecyclerAdapter;
    private ArrayList<AiChat> aiChats;
    private AIChatRecyclerAdapter aiChatRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aiactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        ibBack.setOnClickListener(v -> {
            finish();
        });
        ibSend.setOnClickListener(v -> {
            sendMessage("https://logicmedagent-production.up.railway.app/talk");
        });
    }
    private void init() {
        contentLoadingProgressBar = findViewById(R.id.ai_chat_progress_bar);
        ibBack = findViewById(R.id.ai_chat_back_arrow);
        rvMessages = findViewById(R.id.ai_chat_messages);
        ibSend = findViewById(R.id.ai_chat_send_btn);
        etMessage = findViewById(R.id.ai_chat_message_input);
        app = (MyApplication) getApplicationContext();
        prevChatId = null;
        aiChats = new ArrayList<>();
        aiChatRecyclerAdapter = new AIChatRecyclerAdapter(aiChats);
        rvMessages.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(linearLayoutManager);
        rvMessages.setAdapter(aiChatRecyclerAdapter);
        aiChatRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                rvMessages.smoothScrollToPosition(aiChats.size() - 1);
            }
        });
    }
    private void sendMessage(String url)
    {
        if (app.firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(
                    AIActivity.this,
                    AuthenticationScreen.class
            ));
            finish();
        }

        String message = Objects.requireNonNull(etMessage.getText()).toString();
        if (message.isEmpty()) {
            return;
        }
        if (app.sPrefUser.getString(KeyUtils.namePrefKey, "").isEmpty()) {
            Toast.makeText(this, "User Detail Not Found! Login Again", Toast.LENGTH_LONG).show();
            return;
        }

        etMessage.setText(null);
        aiChats.add(
            new AiChat(
                    app.sPrefUser.getString(KeyUtils.namePrefKey, ""),
                    message
            )
        );
        aiChatRecyclerAdapter.notifyItemInserted(aiChats.size() - 1);
        ibSend.setClickable(false);

        if (prevChatId != null) {
            prevChatId = "\"" + prevChatId + "\"";
        }
        String jsonRequest = "{\"user_id\":\"" + app.firebaseAuth.getCurrentUser().getUid() + "\", \"prev_history_id\":" + prevChatId + "}";

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .build();

        RequestBody requestBody = RequestBody.create(jsonRequest, KeyUtils.JSON);

        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(url))
                .newBuilder()
                .addQueryParameter("query", message)
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        aiChats.add(
                                new AiChat(
                                        "Dr. LogicMed",
                                        jsonObject.getString("response")
                                )
                        );
                        aiChatRecyclerAdapter.notifyItemInserted(aiChats.size() - 1);
                        prevChatId = jsonObject.getString("prev_history_id");
                    } catch (JSONException | IOException e) {
                                throw new RuntimeException(e);
                    }
                }
                else {
                    AIActivity.this.runOnUiThread(() -> {
                        Toast.makeText(AIActivity.this, "Unable to Chat to Agent", Toast.LENGTH_SHORT).show();
                    });
                }
                AIActivity.this.runOnUiThread(() -> {
                    ibSend.setClickable(true);
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                AIActivity.this.runOnUiThread(() -> {
                    Toast.makeText(AIActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    ibSend.setClickable(true);
                });
            }
        });
    }
}