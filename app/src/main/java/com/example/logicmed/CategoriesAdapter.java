package com.example.logicmed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriyViewHolder> {
    private Context context;
    private ArrayList<DoctorCategoriesAndSubCategories> doctorCategoriesAndSubCategories;
    private SubCategoriesAdapter.setOnclickListener listener;

    public CategoriesAdapter(Context context, ArrayList<DoctorCategoriesAndSubCategories> doctorCategoriesAndSubCategories,
                             SubCategoriesAdapter.setOnclickListener listener) {
        this.context = context;
        this.doctorCategoriesAndSubCategories = doctorCategoriesAndSubCategories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoriyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.catagories_layout, parent, false);
        return new CategoriyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriyViewHolder holder, int position) {
        DoctorCategoriesAndSubCategories doctorCategoriesAndSubCategoriesObj = doctorCategoriesAndSubCategories.get(position);
        holder.tvTitle.setText(doctorCategoriesAndSubCategoriesObj.getCategory());
        holder.rvSubCategories.setHasFixedSize(true);
        holder.rvSubCategories.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        holder.rvSubCategories.setAdapter(
                new SubCategoriesAdapter(context, doctorCategoriesAndSubCategoriesObj.getSubCategories(), listener)
        );
    }

    @Override
    public int getItemCount() {
        return doctorCategoriesAndSubCategories.size();
    }

    public static class CategoriyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final RecyclerView rvSubCategories;

        public CategoriyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.category_title);
            rvSubCategories = itemView.findViewById(R.id.categories_recycler_view);
        }
    }
}
