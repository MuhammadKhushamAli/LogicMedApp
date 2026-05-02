package com.example.logicmed;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ChatFragment extends Fragment implements ChatPersonRecyclerAdapter.setOnClickListener {
    private SearchView searchView;
    private RecyclerView rvChats;
    private ChatPersonRecyclerAdapter chatPersonRecyclerAdapter;

    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onStop() {
        super.onStop();
        chatPersonRecyclerAdapter.stopListening();
    }

    private void init(View view) {
        Context context = requireContext();
        searchView = view.findViewById(R.id.chat_search_view);
        rvChats = view.findViewById(R.id.chat_list_rv);

        MyApplication app = (MyApplication) context.getApplicationContext();

        if (app.firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(context, "Login First", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, AuthenticationScreen.class);
            startActivity(intent);
        }

        Query query = app.firestore.collection(KeyUtils.firebaseChatCollectionKey).whereArrayContains(Chat.PARTICIPANTS_FIELD, app.firebaseAuth.getCurrentUser().getUid());
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class)
                .build();
        chatPersonRecyclerAdapter = new ChatPersonRecyclerAdapter(options, context, this);
        rvChats.setHasFixedSize(true);
        rvChats.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onStart() {
        super.onStart();
        rvChats.setAdapter(chatPersonRecyclerAdapter);
        chatPersonRecyclerAdapter.startListening();
    }

    @Override
    public void onClickListener(String chatId, String name, String profileUrl) {
        Intent intent = new Intent(requireContext(), ChatActivity.class);
        intent.putExtra(KeyUtils.chatUIDIntentKey, chatId);
        intent.putExtra(KeyUtils.userNameIntentKey, name);
        intent.putExtra(KeyUtils.userProfileUrlIntentKey, profileUrl);
        startActivity(intent);
    }
}