package com.example.logicmed;

import android.content.pm.LabeledIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AIChatRecyclerAdapter extends RecyclerView.Adapter<AIChatRecyclerAdapter.AIChatViewHolder> {
    private ArrayList<AiChat> aiChats;

    public AIChatRecyclerAdapter(ArrayList<AiChat> aiChats) {
        this.aiChats = aiChats;
    }

    @NonNull
    @Override
    public AIChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ai_chat_layout, parent, false);
        return new AIChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AIChatViewHolder holder, int position) {
        AiChat chat = aiChats.get(position);
        holder.tvName.setText(chat.getSenderName());
        holder.tvMessage.setText(chat.getMessage());
    }

    @Override
    public int getItemCount() {
        return aiChats.size();
    }

    public static class AIChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvMessage;
        public AIChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.ai_message_sender_name);
            tvMessage = itemView.findViewById(R.id.ai_message_text_content);
        }
    }
}
