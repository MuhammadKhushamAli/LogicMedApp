package com.example.logicmed;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DoctorDetailSignUpFragment extends Fragment implements SubCategoriesAdapter.setOnclickListener{

    private setOnSignUpListener listener;
    private ChipGroup cgDaysOfDuties;
    private RecyclerView rvCategories;
    private Context context;
    private MaterialButton btnSubmit;
    private TextInputEditText etFee;
    private TextInputEditText etSlot;
    private List<String> categoriesOfDoctor;
    private List<Schedule> timingsOfDoctors;
    private float fee;
    private float timeSlot;

    public interface setOnSignUpListener {
        void setDataOnSignUp(List<String> categoriesOfDoctor, List<Schedule> timingsOfDoctors, float fee, float timeSlot);
    }

    public DoctorDetailSignUpFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.listener = (setOnSignUpListener) getParentFragment();
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_detail_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        addDaysOfDuties();
        setCategoriesAndSubCategories();

        btnSubmit.setOnClickListener(v -> {
            try {
                fee = Float.parseFloat(Objects.requireNonNull(etFee.getText()).toString());
                timeSlot = Float.parseFloat(Objects.requireNonNull(etSlot.getText()).toString());
            }
            catch (Exception exception) {
                Toast.makeText(context, "Enter Valid Number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fee <= 0.0f || timeSlot <= 0.0f || categoriesOfDoctor.isEmpty() || timingsOfDoctors.isEmpty()) {
                Toast.makeText(context, "All Fields Are Required", Toast.LENGTH_SHORT).show();
                return;
            }

            listener.setDataOnSignUp(categoriesOfDoctor, timingsOfDoctors, fee, timeSlot);
        });

    }
    private void init(View view) {
        cgDaysOfDuties = view.findViewById(R.id.doctor_days_of_duty_chip_group);
        rvCategories = view.findViewById(R.id.doctor_data_fields_recycler_view);
        btnSubmit = view.findViewById(R.id.doctor_data_submit_button);
        etFee = view.findViewById(R.id.doctor_detail_fee);
        etSlot = view.findViewById(R.id.doctor_detail_appointment_slot);
        categoriesOfDoctor = new ArrayList<>();
        timingsOfDoctors = new ArrayList<>();
        fee = -1.0f;
        timeSlot = -1.0f;
    }
    private void addDaysOfDuties() {
        MyApplication app = (MyApplication) context.getApplicationContext();
        String[] daysOfWeek = app.daysOfWeek;

        for (String dayOfWeek : daysOfWeek) {
            Chip chip = new Chip(context);
            chip.setText(dayOfWeek);
            chip.setCheckable(true);
            chip.setChipDrawable(ChipDrawable.createFromAttributes(context, null, 0,
                    com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter));

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    formAndToTImePicker(chip, dayOfWeek);
                }
                else {
                    int prevObjIndex = (int) chip.getTag();
                    timingsOfDoctors.remove(prevObjIndex);
                    chip.setText(dayOfWeek);
                    chip.setTag(null);
                }
                chip.setChecked(isChecked);
            });

            cgDaysOfDuties.addView(chip);
        }

    }
    private void formAndToTImePicker(Chip chip, String dayOfWeek) {
        TimePickerDialog fromTimePickerDialogue = new TimePickerDialog(context, (view, hour, minutes) -> {
            String fromTime = hour + ":" + minutes;

            TimePickerDialog toTimePickerDialogue = new TimePickerDialog(context, (view2, hourEnd, minutesEnd) -> {
                String endTime = hourEnd + ":" + minutesEnd;

                Schedule schedule = new Schedule(dayOfWeek, fromTime, endTime);
                String textForClickableChip = dayOfWeek + " ( " + fromTime + " - " + endTime + " ) ";
                chip.setText(textForClickableChip);
                timingsOfDoctors.add(schedule);
                chip.setTag(timingsOfDoctors.indexOf(schedule));
            }, 5, 0, false);

            toTimePickerDialogue.setTitle("To");
            toTimePickerDialogue.show();
            toTimePickerDialogue.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);

        }, 9, 0, false);

        fromTimePickerDialogue.setTitle("From");
        fromTimePickerDialogue.show();
        fromTimePickerDialogue.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
    }
    private void setCategoriesAndSubCategories() {
        MyApplication app = (MyApplication) context.getApplicationContext();
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(context, app.doctorsCategoriesAndSubCategories, this);
        rvCategories.setHasFixedSize(true);
        rvCategories.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rvCategories.setAdapter(categoriesAdapter);
    }
    @Override
    public void addSubCategory(String subCategory) {
        categoriesOfDoctor.add(subCategory);
    }
    @Override
    public void removeSubCategory(String subCategory) {
        categoriesOfDoctor.remove(subCategory);
    }
}