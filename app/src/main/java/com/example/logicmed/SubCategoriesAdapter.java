package com.example.logicmed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.SubCategoriesViewHolder> {
    private final Context context;
    private final ArrayList<String> subCategoriesArrayList;

    public SubCategoriesAdapter(Context context, ArrayList<String> subCategoriesArrayList) {
        this.context = context;
        this.subCategoriesArrayList = subCategoriesArrayList;
    }

    @NonNull
    @Override
    public SubCategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sub_categories_layout, parent, false);
        return new SubCategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoriesViewHolder holder, int position) {
        String subCategoryTitle = subCategoriesArrayList.get(position);
        holder.ctvTitle.setText(subCategoryTitle);

        holder.ctvTitle.setOnClickListener(v -> {
                holder.ctvTitle.toggle();
        });
    }

    @Override
    public int getItemCount() {
        return subCategoriesArrayList.size();
    }

    public static class SubCategoriesViewHolder extends RecyclerView.ViewHolder {
        private final CheckedTextView ctvTitle;

        public SubCategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            ctvTitle = itemView.findViewById(R.id.sub_categories_title);
        }
    }
}
