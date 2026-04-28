package com.example.logicmed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.SubCategoriesViewHolder> {
    private final Context context;
    private final ArrayList<String> subCategoriesArrayList;
    private final setOnclickListener listener;

    public interface setOnclickListener {
        void addSubCategory(String subCategory);
        void removeSubCategory(String subCategory);
    }

    public SubCategoriesAdapter(Context context, ArrayList<String> subCategoriesArrayList,
                                setOnclickListener listener) {
        this.context = context;
        this.subCategoriesArrayList = subCategoriesArrayList;
        this.listener = listener;
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
            if (holder.ctvTitle.isChecked()) {
                listener.addSubCategory(holder.ctvTitle.getText().toString());
            }
            else {
                listener.removeSubCategory(holder.ctvTitle.getText().toString());
            }
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
