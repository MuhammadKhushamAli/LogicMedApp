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
import com.google.firebase.firestore.FirebaseFirestoreException;

public class DoctorRecyclerViewAdapter extends FirestoreRecyclerAdapter<Doctor, DoctorRecyclerViewAdapter.DoctorViewHolder> {

    public DoctorRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Doctor> options) {
        super(options);

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
        holder.tvFee.setText(String.valueOf(model.getFee()));
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
