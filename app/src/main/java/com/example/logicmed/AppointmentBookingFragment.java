package com.example.logicmed;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firestore.v1.TargetOrBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AppointmentBookingFragment extends Fragment
        implements AppointmentSlotsAdapter.setOnClickListener {

    private static final String DOCTOR_ID_PARAM = "doctor_id";
    private static final String DOCTOR_NAME_PARAM = "doctor_name";
    private static final String DOCTOR_PROFILE_URL_PARAM = "doctor_profile_url";
    private static final String LIST_OF_AVAILABLE_SLOTS = "list_of_available_slots";
    private static final String LIST_OF_AVAILABLE_APPOINTMENTS = "list_of_available_appointments";

    private String doctor_id;
    private String doctor_name;
    private String doctor_profile_url;
    private List<SlotsOfDay> list_of_available_slots;
    private List<Appointment> list_of_available_appointments;

    private MyApplication app;
    private Context context;
    private setOnClickListener listener;
    private ImageButton ibClose;
    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvSetDate;
    private TextView tvSlotsTitle;
    private RecyclerView rvSlots;
    private MaterialButton btnSubmit;
    private CalendarConstraints.DateValidator dateValidator;
    private CalendarConstraints calendarConstraints;
    private MaterialDatePicker<Long> materialDatePicker;
    private AppointmentSlotsAdapter appointmentSlotsAdapter;
    private String currentSelectedSlot;
    private String currentSelectedDay;
    private String currentSelectedDate;
    private SimpleDateFormat simpleDateFormat;

    public interface setOnClickListener {
        void onClose();
    }

    public AppointmentBookingFragment() {
    }

    public static AppointmentBookingFragment newInstance(String doctor_id, String doctor_name, String doctor_profile_url, List<SlotsOfDay> slotsOfDayList, List<Appointment> appointmentList) {
        AppointmentBookingFragment fragment = new AppointmentBookingFragment();
        Bundle args = new Bundle();
        args.putString(DOCTOR_ID_PARAM, doctor_id);
        args.putString(DOCTOR_NAME_PARAM, doctor_name);
        args.putString(DOCTOR_PROFILE_URL_PARAM, doctor_profile_url);
        args.putParcelableArrayList(LIST_OF_AVAILABLE_SLOTS, (ArrayList<? extends Parcelable>) slotsOfDayList);
        args.putParcelableArrayList(LIST_OF_AVAILABLE_APPOINTMENTS, (ArrayList<? extends Parcelable>) appointmentList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            doctor_id = getArguments().getString(DOCTOR_ID_PARAM);
            doctor_name = getArguments().getString(DOCTOR_NAME_PARAM);
            doctor_profile_url = getArguments().getString(DOCTOR_PROFILE_URL_PARAM);
            list_of_available_slots = getArguments().getParcelableArrayList(LIST_OF_AVAILABLE_SLOTS);
            list_of_available_appointments = getArguments().getParcelableArrayList(LIST_OF_AVAILABLE_APPOINTMENTS);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (setOnClickListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointment_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        onDateSelected();

        ibClose.setOnClickListener(v -> {
            listener.onClose();
        });

        tvSetDate.setOnClickListener(v -> {
            materialDatePicker.show(getChildFragmentManager(), "APPOINTMENT_DATES");
        });
        btnSubmit.setOnClickListener(v -> {
            getAppointment();
        });

    }

    private void init(View view) {
        context = requireContext();
        app = (MyApplication) context.getApplicationContext();
        ibClose = view.findViewById(R.id.appointment_booking_go_back_btn);
        ivProfile = view.findViewById(R.id.appointment_booking_doctor_img);
        tvName = view.findViewById(R.id.appointment_booking_doctor_name);
        tvSetDate = view.findViewById(R.id.appointment_booking_select_date);
        rvSlots = view.findViewById(R.id.appointment_booking_time_slots_rv);
        tvSlotsTitle = view.findViewById(R.id.appointment_booking_timeslots_title);
        btnSubmit = view.findViewById(R.id.appointment_booking_submit_button);
        simpleDateFormat = new SimpleDateFormat(KeyUtils.dateFormate, Locale.US);
        tvSlotsTitle.setVisibility(View.INVISIBLE);
        rvSlots.setHasFixedSize(true);
        currentSelectedSlot = null;
        currentSelectedDay = null;
        currentSelectedDate = null;
        rvSlots.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        if (doctor_profile_url != null && !(doctor_profile_url.isEmpty())) {
            Glide.with(view)
                    .load(doctor_profile_url)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(ivProfile);
        }
        tvName.setText(doctor_name);

        dateValidator = new CalendarConstraints.DateValidator() {
            @Override
            public boolean isValid(long l) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(l);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                for (SlotsOfDay slotsOfDay : list_of_available_slots) {

                    if (!(slotsOfDay.getSlots().isEmpty())) {

                        int dayOfAppointment = calenderDayNumberFinder(slotsOfDay.getDay());
                        if (dayOfWeek == dayOfAppointment) {
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(@NonNull Parcel parcel, int i) {

            }
        };

        List<CalendarConstraints.DateValidator> validators = new ArrayList<>();
        validators.add(DateValidatorPointForward.now());
        validators.add(dateValidator);

        CalendarConstraints.DateValidator combinedValidator = CompositeDateValidator.allOf(validators);

        // Start of Calender
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long startOfCalender = calendar.getTimeInMillis();

        // End of Calender
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        long endOfCalender = calendar.getTimeInMillis();

        calendarConstraints = new CalendarConstraints.Builder()
                .setValidator(combinedValidator)
                .setStart(startOfCalender)
                .setEnd(endOfCalender)
                .build();
        materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Appointment Date")
                .setCalendarConstraints(calendarConstraints)
                .build();

    }
    private int calenderDayNumberFinder(String day) {
        String[] daysOfWeek = app.daysOfWeek;
        if (day.equals(daysOfWeek[0]))
            return Calendar.MONDAY;
        else if (day.equals(daysOfWeek[1]))
            return Calendar.TUESDAY;
        else if (day.equals(daysOfWeek[2]))
            return Calendar.WEDNESDAY;
        else if (day.equals(daysOfWeek[3]))
            return Calendar.THURSDAY;
        else if (day.equals(daysOfWeek[4]))
            return Calendar.FRIDAY;
        else if (day.equals(daysOfWeek[5]))
            return Calendar.SATURDAY;
        else if (day.equals(daysOfWeek[6]))
            return Calendar.SUNDAY;
        else
            return -1;

    }
    private void onDateSelected() {
        materialDatePicker.addOnPositiveButtonClickListener(utcDate -> {
           Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
           calendar.setTimeInMillis(utcDate);

           int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

           for (SlotsOfDay slotsOfDay : list_of_available_slots) {
               int dayOfAppointment = calenderDayNumberFinder(slotsOfDay.getDay());
               if (dayOfWeek == dayOfAppointment) {
                   currentSelectedDay = slotsOfDay.getDay();
                   Date date = calendar.getTime();
                   currentSelectedDate = simpleDateFormat.format(date);
                   List<String> finalSlots = new ArrayList<>(slotsOfDay.getSlots());
                   for (Appointment appointment: list_of_available_appointments) {
                       if (appointment.getDate().equals(currentSelectedDate) && appointment.getDay().equals(currentSelectedDay)) {
                           finalSlots.remove(appointment.getTimeSlot());
                       }
                   }

                   String slotsTitle = "Available Slots for " + slotsOfDay.getDay();
                   tvSlotsTitle.setText(slotsTitle);
                   tvSlotsTitle.setVisibility(View.VISIBLE);
                   appointmentSlotsAdapter = new AppointmentSlotsAdapter(finalSlots, this);
                   rvSlots.setAdapter(appointmentSlotsAdapter);
               }
           }
        });
    }

    @Override
    public void setOnClick(String slot) {
        currentSelectedSlot = slot;
    }

    private void getAppointment() {
        if ((currentSelectedDay == null || currentSelectedDay.isEmpty()) && (currentSelectedDate == null || currentSelectedDate.isEmpty())) {
            Toast.makeText(context, "Select the Date First", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentSelectedSlot == null || currentSelectedSlot.isEmpty()) {
            Toast.makeText(context, "Select the Slot First", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = app.firebaseAuth.getCurrentUser();
        String name = app.sPrefUser.getString(KeyUtils.namePrefKey, "");
        String profileUrl = app.sPrefUser.getString(KeyUtils.profileUrlPrefKey, "");

        if(user == null || name.isEmpty()) {
            Toast.makeText(context, "User Not Found", Toast.LENGTH_SHORT).show();
            return;
        }

        Appointment appointment = new Appointment(user.getUid(),
                doctor_id,
                currentSelectedDate,
                currentSelectedDay,
                currentSelectedSlot,
                new ParticipantDetail(
                        user.getUid(),
                        name,
                        profileUrl
                ),
                new ParticipantDetail(
                        doctor_id,
                        doctor_name,
                        doctor_profile_url
                )
        );
        app.firestore.collection(KeyUtils.firebaseAppointmentCollectionKey)
                .add(appointment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        listener.onClose();
    }
}