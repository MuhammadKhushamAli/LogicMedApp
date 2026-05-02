package com.example.logicmed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MessageRecyclerAdapter extends FirestoreRecyclerAdapter<Message, MessageRecyclerAdapter.MessageViewHolder> {
    private MyApplication app;

    public MessageRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Message> options, Context context) {
        super(options);
        app = (MyApplication) context.getApplicationContext();
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {
        assert app.firebaseAuth.getCurrentUser() != null : "Current User not Available";
        String uId = app.firebaseAuth.getCurrentUser().getUid();
        holder.tvName.setText(model.getSenderName());
        holder.tvMessage.setVisibility(View.GONE);
        holder.ivImage.setVisibility(View.GONE);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone((ConstraintLayout) holder.itemView);

        if (model.getSenderId().equals(uId)) {
            constraintSet.setHorizontalBias(R.id.message_content_section, 1.0f);
        }
        else {
            constraintSet.setHorizontalBias(R.id.message_content_section, 0.0f);
        }

        if (model.getIsImage()) {
            Glide.with(holder.itemView)
                    .load(model.getMessage())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(holder.ivImage);
            holder.ivImage.setVisibility(View.VISIBLE);
        }
        else {
            holder.tvMessage.setText(model.getMessage());
            holder.tvMessage.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent, false);
        return new MessageViewHolder(view);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvMessage;
        private ImageView ivImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.message_sender_name);
            tvMessage = itemView.findViewById(R.id.message_text_content);
            ivImage = itemView.findViewById(R.id.message_img_content);
        }
    }
}
