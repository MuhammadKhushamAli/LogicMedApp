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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AppointmentRecyclerAdapter extends FirestoreRecyclerAdapter<Appointment, AppointmentRecyclerAdapter.AppointmentViewHoler> {

    private MyApplication app;
    private setOnClickListener listener;
    private Context context;
    private SimpleDateFormat simpleDateFormat;

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
        Date currentDate = null;
        Date appDate = null;
        Date currentTime = null;
        Date endTime = null;
        try {
            Date date = new Date();
            simpleDateFormat = new SimpleDateFormat(KeyUtils.dateFormate, Locale.US);
            currentDate = simpleDateFormat.parse(simpleDateFormat.format(date));
            appDate = simpleDateFormat.parse(model.getDate());

            simpleDateFormat = new SimpleDateFormat(KeyUtils.timeFormate, Locale.US);
            currentTime = simpleDateFormat.parse(simpleDateFormat.format(date));
            endTime = simpleDateFormat.parse(model.getTimeSlot().split(" - ")[1]);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (Objects.requireNonNull(appDate).before(currentDate)) {
            holder.itemView.setVisibility(View.GONE);
        }
        else if (Objects.requireNonNull(appDate).equals(currentDate) && Objects.requireNonNull(endTime).before(currentTime)) {
            holder.itemView.setVisibility(View.GONE);
        }
        else {
            holder.itemView.setVisibility(View.VISIBLE);
        }
        ParticipantDetail participantDetail;
        holder.tvStatus.setText(model.getStatus());
        holder.tvClick.setVisibility(View.VISIBLE);

        if (app.sPrefUser.getString(KeyUtils.rolePrefKey, KeyUtils.patientKey).equals(KeyUtils.doctorKey)) {
            participantDetail = model.getPatientDetails();
        }
        else {
            participantDetail = model.getDoctorDetails();

            if (model.getStatus().equals(Appointment.PENDING_STATUS) && model.getCheckUpFeedBack() == null) {
                holder.tvClick.setVisibility(View.GONE);
            }
         }
        String tvClickMsg;
        if (model.getStatus().equals(Appointment.RESOLVED_STATUS) && model.getCheckUpFeedBack() != null && (!model.getCheckUpFeedBack().isEmpty())) {
            tvClickMsg = "Click To View Status";
        }
        else {
            tvClickMsg = "Click To Change Status";
        }
        holder.tvClick.setText(tvClickMsg);

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                if (model.getStatus().equals(Appointment.RESOLVED_STATUS) && model.getCheckUpFeedBack() != null && (!model.getCheckUpFeedBack().isEmpty())) {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Your Appointment Feedback")
                            .setMessage(model.getCheckUpFeedBack())
                            .setPositiveButton("OK", (a, b) -> {})
                            .show();
                }
                else {
                    if (app.sPrefUser.getString(KeyUtils.rolePrefKey, KeyUtils.patientKey).equals(KeyUtils.doctorKey)) {
                        String apId = getSnapshots().getSnapshot(pos).getId();

                        listener.onChangeStatus(apId);
                    }
                }
            }
        });

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
