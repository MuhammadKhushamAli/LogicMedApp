package com.example.logicmed;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchFragment extends Fragment implements DoctorRecyclerViewAdapter.setOnClickListener {
    private SearchView searchView;
    private RecyclerView rvDoctorsList;

    private setOnClickListener listener;

    public interface setOnClickListener {
        void onClickListener(String uID);
    }

    public SearchFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (setOnClickListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

    }
    private void init(View view) {
        Context context = requireContext();
        searchView = view.findViewById(R.id.search_search_view);
        rvDoctorsList = view.findViewById(R.id.search_doctors_list_rv);
        MyApplication app = (MyApplication) context.getApplicationContext();

        Query query = app.firestore.collection(KeyUtils.firebaseUserCollectionKey).whereEqualTo(User.ROLE_FIELD, KeyUtils.doctorKey);
        FirestoreRecyclerOptions<Doctor> options = new FirestoreRecyclerOptions.Builder<Doctor>()
                .setQuery(query, Doctor.class)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();

        DoctorRecyclerViewAdapter doctorRecyclerView = new DoctorRecyclerViewAdapter(options, this);
        rvDoctorsList.setHasFixedSize(true);
        rvDoctorsList.setLayoutManager(new GridLayoutManager(context, 2));
        rvDoctorsList.setAdapter(doctorRecyclerView);
    }
    @Override
    public void onClickListener(String uID) {
        listener.onClickListener(uID);
    }

}