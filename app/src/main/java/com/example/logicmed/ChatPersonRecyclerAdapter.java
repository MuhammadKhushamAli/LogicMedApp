package com.example.logicmed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class ChatPersonRecyclerAdapter extends FirestoreRecyclerAdapter<Chat, ChatPersonRecyclerAdapter.ChatPersonViewHolder> {
    private final MyApplication app;
    private setOnClickListener listener;
    private Context context;

    public interface setOnClickListener {
        void onClickListener(String chatId, String name, String profileUrl);
    }

    public ChatPersonRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Chat> options, Context context, setOnClickListener listener) {
        super(options);
        this.app = (MyApplication) context.getApplicationContext();
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatPersonViewHolder holder, int position, @NonNull Chat model) {
        assert app.firebaseAuth.getCurrentUser() != null : "User not Found";
        String currentUID = app.firebaseAuth.getCurrentUser().getUid();
        ParticipantDetail detail = model.giveOtherOne(currentUID);

        if (detail == null) {
            String errorStr = "Error";
            holder.tvName.setText(errorStr);
            return;
        }

        if(!(detail.getProfileUrl().isEmpty())) {
            Glide.with(holder.itemView)
                    .load(detail.getProfileUrl())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(holder.ivProfile);
        }

        holder.tvName.setText(detail.getName());
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                String chatID = getSnapshots().getSnapshot(pos).getId();
                listener.onClickListener(chatID, detail.getName(), detail.getProfileUrl());
            }
        });
    }

    @NonNull
    @Override
    public ChatPersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_person_layout, parent, false);
        return new ChatPersonViewHolder(view);
    }

    public static class ChatPersonViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProfile;
        private TextView tvName;
        public ChatPersonViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.chat_person_profile_pic);
            tvName = itemView.findViewById(R.id.chat_person_name);
        }
    }
}
