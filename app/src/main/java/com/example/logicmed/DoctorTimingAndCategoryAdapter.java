package com.example.logicmed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

public class DoctorTimingAndCategoryAdapter extends RecyclerView.Adapter<DoctorTimingAndCategoryAdapter.DoctorTimingAndCategoryHolder> {
    Context context;
    List<Schedule> timings;
    List<String> categories;

    public DoctorTimingAndCategoryAdapter(Context context, List<Schedule> timings, List<String> categories) {
        this.context = context;
        this.timings = timings;
        this.categories = categories;
    }

    @NonNull
    @Override
    public DoctorTimingAndCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doctor_timing_category_layout, parent, false);
        return new DoctorTimingAndCategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorTimingAndCategoryHolder holder, int position) {
        String categoryOrTiming = null;
        if (categories != null) {
            categoryOrTiming = categories.get(position);
        }
        else if (timings != null) {
            Schedule schedule = timings.get(position);
            categoryOrTiming = schedule.getDay() + " ( " + schedule.getFromTime() + " - " + schedule.getEndTime() + " ) ";
        }
        if (categoryOrTiming != null) {
            holder.tvTimingOrCategory.setText(categoryOrTiming);
        }

    }

    @Override
    public int getItemCount() {
        if (categories != null) {
            return categories.size();
        }
        else if (timings != null) {
            return timings.size();
        }
        else {
            return 0;
        }
    }

    public static class DoctorTimingAndCategoryHolder extends RecyclerView.ViewHolder {
        private TextView tvTimingOrCategory;
        public DoctorTimingAndCategoryHolder(@NonNull View itemView) {
            super(itemView);
            tvTimingOrCategory = itemView.findViewById(R.id.doctor_timing_and_category_text);
        }
    }
}
