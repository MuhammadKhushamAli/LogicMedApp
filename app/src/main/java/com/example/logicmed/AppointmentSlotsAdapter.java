package com.example.logicmed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointmentSlotsAdapter extends RecyclerView.Adapter<AppointmentSlotsAdapter.AppointmentSlotViewHoler> {
    private List<String> slots;
    private int currentSelectedIndex;
    private setOnClickListener listener;

    public interface setOnClickListener {
        void setOnClick(String slot);
    }

    public AppointmentSlotsAdapter(List<String> slots, setOnClickListener listener) {
        this.slots = slots;
        this.listener = listener;
        currentSelectedIndex = -1;
    }

    @NonNull
    @Override
    public AppointmentSlotViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_categories_layout, parent, false);
        return new AppointmentSlotViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentSlotViewHoler holder, int position) {
        String slot = slots.get(position);
        holder.checkedTextView.setText(slot);

        holder.checkedTextView.setChecked(position == currentSelectedIndex);

        holder.checkedTextView.setOnClickListener(v -> {
            int prev = currentSelectedIndex;
            currentSelectedIndex = holder.getBindingAdapterPosition();
            holder.checkedTextView.toggle();
            notifyItemChanged(prev);
            if (holder.checkedTextView.isChecked()) {
                if (currentSelectedIndex != RecyclerView.NO_POSITION) {
                    listener.setOnClick(slots.get(currentSelectedIndex));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    public static class AppointmentSlotViewHoler extends RecyclerView.ViewHolder {
        private CheckedTextView checkedTextView;
        public AppointmentSlotViewHoler(@NonNull View itemView) {
            super(itemView);
            checkedTextView = itemView.findViewById(R.id.sub_categories_title);
        }
    }
}
