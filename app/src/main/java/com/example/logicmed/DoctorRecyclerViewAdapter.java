package com.example.logicmed;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestoreException;

public class DoctorRecyclerViewAdapter extends FirestoreRecyclerAdapter<Doctor, DoctorRecyclerViewAdapter.DoctorViewHolder> {

    private setOnClickListener listener;
    public interface setOnClickListener {
        void onClickListener(String uID);
    }

    public DoctorRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Doctor> options, setOnClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull DoctorViewHolder holder, int position, @NonNull Doctor model) {
        if (model.getProfileImageUrl() != null) {
            Glide.with(holder.itemView)
                    .load(model.getProfileImageUrl())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(holder.ivProfileImage);
        }
        holder.tvName.setText(model.getFullName());

        String feeStr = "Fee: " + model.getFee();
        holder.tvFee.setText(feeStr);

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();

            if (pos != RecyclerView.NO_POSITION) {
                Context context = v.getContext();

                String doctorUID = getSnapshots().getSnapshot(pos).getId();
                listener.onClickListener(doctorUID);
            }
        });
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_layout, parent, false);
        return new DoctorViewHolder(view);
    }


    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage;
        TextView tvName;
        TextView tvFee;
        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.doctor_layout_img);
            tvName = itemView.findViewById(R.id.doctor_layout_name);
            tvFee = itemView.findViewById(R.id.doctor_layout_fee);
        }
    }
}
