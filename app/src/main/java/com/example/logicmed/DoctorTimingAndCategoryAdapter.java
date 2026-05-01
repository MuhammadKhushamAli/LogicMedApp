package com.example.logicmed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DoctorTimingAndCategoryAdapter extends RecyclerView.Adapter<DoctorTimingAndCategoryAdapter.DoctorTimingAndCategoryHolder> {
    Context context;
    List<String> timingsOrCategories;

    public DoctorTimingAndCategoryAdapter(Context context, List<String> timingsOrCategories) {
        this.context = context;
        this.timingsOrCategories = timingsOrCategories;
    }

    @NonNull
    @Override
    public DoctorTimingAndCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doctor_timing_category_layout, parent, false);
        return new DoctorTimingAndCategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorTimingAndCategoryHolder holder, int position) {
        String timingOrCategory = timingsOrCategories.get(position);
        holder.tvTimingOrCategory.setText(timingOrCategory);
    }

    @Override
    public int getItemCount() {
        return timingsOrCategories.size();
    }

    public static class DoctorTimingAndCategoryHolder extends RecyclerView.ViewHolder {
        private TextView tvTimingOrCategory;
        public DoctorTimingAndCategoryHolder(@NonNull View itemView) {
            super(itemView);
            tvTimingOrCategory = itemView.findViewById(R.id.doctor_timing_and_category_text);
        }
    }
}
