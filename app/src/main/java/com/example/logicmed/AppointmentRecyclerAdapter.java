package com.example.logicmed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

public class AppointmentRecyclerAdapter extends FirestoreRecyclerAdapter<Appointment, AppointmentRecyclerAdapter.AppointmentViewHoler> {

    private MyApplication app;
    private setOnClickListener listener;
    private Context context;

    public interface setOnClickListener {
        void onChangeStatus(String apID);
    }
    public AppointmentRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Appointment> options, Context context, setOnClickListener listener) {
        super(options);
        app = (MyApplication) context.getApplicationContext();
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull AppointmentViewHoler holder, int position, @NonNull Appointment model) {
        ParticipantDetail participantDetail;
        holder.tvStatus.setText(model.getStatus());

        if (app.sPrefUser.getString(KeyUtils.rolePrefKey, KeyUtils.patientKey).equals(KeyUtils.doctorKey)) {
            holder.tvClick.setVisibility(View.VISIBLE);
            participantDetail = model.getPatientDetails();

            holder.itemView.setOnClickListener(v -> {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    String apId = getSnapshots().getSnapshot(pos).getId();

                    new AlertDialog.Builder(context)
                            .setTitle("Change the Status")
                            .setPositiveButton("Resolved", (a, b) -> {
                                listener.onChangeStatus(apId);
                            })
                            .setNegativeButton("Not Change", (a, b) -> {

                            }).show();
                }
            });
        }
        else {
            participantDetail = model.getDoctorDetails();
            holder.tvClick.setVisibility(View.GONE);
        }

        holder.tvName.setText(participantDetail.getName());
        String dateText = model.getDate() + " @ " + model.getTimeSlot();
        holder.tvDate.setText(dateText);

        if (participantDetail.getProfileUrl() != null) {
            Glide.with(holder.itemView)
                    .load(participantDetail.getProfileUrl())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(holder.ivProfileImage);
        }
    }

    @NonNull
    @Override
    public AppointmentViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointments_layout, parent, false);
        return new AppointmentViewHoler(view);
    }


    public static class AppointmentViewHoler extends RecyclerView.ViewHolder {
        ImageView ivProfileImage;
        TextView tvName;
        TextView tvStatus;
        TextView tvDate;
        TextView tvClick;
        public AppointmentViewHoler(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.appointment_layout_img);
            tvName = itemView.findViewById(R.id.appointment_layout_name);
            tvStatus = itemView.findViewById(R.id.appointment_layout_status);
            tvDate = itemView.findViewById(R.id.appointment_layout_date);
            tvClick = itemView.findViewById(R.id.appointment_layout_click);
        }
    }
}
