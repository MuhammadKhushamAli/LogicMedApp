package com.example.logicmed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Trace;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;


public class SignupFragment extends Fragment implements
DoctorDetailSignUpFragment.setOnSignUpListener{
    private TextInputEditText teFullName;
    private TextInputEditText teEmail;
    private AutoCompleteTextView atvRole;
    private AutoCompleteTextView atLocation;
    private TextInputEditText tePassword;
    private TextInputEditText teCPassword;
    private MaterialButton btnSignup;
    private ProgressBar progressBar;
    private MyApplication app;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FragmentManager fragmentManager;
    private DoctorDetailSignUpFragment doctorDetailSignUpFragment;


    public SignupFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<String> list = new ArrayList<>();
        list.add("Doctor");
        list.add("Patient");

        init(view);
        setDropDown(atvRole, list, false);
        fetchCities("Pakistan", "https://countriesnow.space/api/v0.1/countries/cities");

        btnSignup.setOnClickListener(v -> {
            String fullName = Objects.requireNonNull(teFullName.getText()).toString();
            String email = Objects.requireNonNull(teEmail.getText()).toString();
            String role = Objects.requireNonNull(atvRole.getText()).toString();
            String city = Objects.requireNonNull(atLocation.getText()).toString();
            String password = Objects.requireNonNull(tePassword.getText()).toString();
            String cPassword = Objects.requireNonNull(teCPassword.getText()).toString();

            if (role.equals("Doctor")) {
                doctorDetailSignUpFragment = new DoctorDetailSignUpFragment();
                fragmentManager
                        .beginTransaction()
                        .show(doctorDetailSignUpFragment)
                        .addToBackStack(null)
                        .commit();
            }
            else {
                firebaseAuth(fullName, email, role, city, password, cPassword);
            }
        });
    }

    private void init(View view) {
        teFullName = view.findViewById(R.id.signup_name);
        teEmail = view.findViewById(R.id.signup_email);
        atvRole = view.findViewById(R.id.signup_role);
        atLocation = view.findViewById(R.id.signup_location);
        tePassword = view.findViewById(R.id.signup_pass);
        teCPassword = view.findViewById(R.id.signup_c_pass);
        progressBar = view.findViewById(R.id.signup_progress_bar);
        btnSignup = view.findViewById(R.id.signup_btn);
        app = (MyApplication) requireContext().getApplicationContext();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        fragmentManager = getChildFragmentManager();
        progressBar.setVisibility(View.GONE);
    }

    private void setDropDown(AutoCompleteTextView autoCompleteTextView, List<String> list, Boolean isKeyListener) {
        if (isKeyListener == false) {
            autoCompleteTextView.setKeyListener(null);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                list
        );

        autoCompleteTextView.setAdapter(adapter);
    }
    private void fetchCities(String country, String url) {
        progressBar.setVisibility(View.VISIBLE);

        String reqJson = "{\"country\": \"" + country + "\"}";

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(reqJson, KeyUtils.JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Activity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        app.cities = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            app.cities.add(jsonArray.getString(i));
                        }
                        Activity activity = getActivity();
                        if (activity != null) {
                            activity.runOnUiThread(() -> {
                                setDropDown(atLocation, app.cities, true);
                                progressBar.setVisibility(View.GONE);
                                new ViewModelProvider(requireActivity()).get(SignupViewModel.class).setCurrentPage(1);
                            });
                        }
                    } catch (JSONException e) {
                        Activity activity = getActivity();
                        if (activity != null) {
                            activity.runOnUiThread(() -> {
                                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }
            }
        });
    }
    private void firebaseAuth(String fullName, String email, String role, String city, String password, String cPassword) {
        Context context = requireContext();
        Activity activity = requireActivity();
        String message = null;

        if (fullName.isEmpty() || email.isEmpty() || role.isEmpty() || city.isEmpty() || password.isEmpty() || cPassword.isEmpty()) {
            message = "All Fields are Required!";
        }
        else if (!(email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))) {
            message = "Email Must be in Correct Formate!";
        }
        else if (!(password.equals(cPassword))) {
            message =  "Password Must be Equal to Confirm Password!";
        }
        else if (!(password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*.?&])[A-Za-z\\d@$!.%*?&]{8,}$"))) {
            message = "Password must contain:\n" +
                    "• At least 8 characters\n" +
                    "• One uppercase & one lowercase letter\n" +
                    "• One number & one special character";
        }
        if (message != null) {
            View view = getView();
            assert view != null : "View is Null for Snack bar";
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
            TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setMaxLines(4);
            snackbar.show();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            assert firebaseAuth.getCurrentUser() != null;
                            java.lang.String uID = firebaseAuth.getCurrentUser().getUid();
                            Map<String, String> data = new HashMap<>();
                            data.put("fullName", fullName);
                            data.put("role", role);
                            data.put("city", city);

                            firebaseDB(uID, data);
                        }
                        else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Unable to Signup";
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
    private void firebaseDB(String uID, Map<String, String> data) {
        Context context = requireContext();
        Activity activity = requireActivity();

        firestore.collection(KeyUtils.firebaseUserCollectionKey)
                .document(uID)
                .set(data)
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (
                                    context.getSharedPreferences(KeyUtils.userPrefFileKey, Context.MODE_PRIVATE)
                                            .edit()
                                            .putBoolean(KeyUtils.isLoggedInPrefKey, true)
                                            .commit()
                            ) {

                                startActivity(
                                        new Intent(
                                                context,
                                                MainActivity.class
                                        )
                                );
                            }
                            else {
                                Toast.makeText(context, "Unable to Maintain Login Status", Toast.LENGTH_SHORT).show();
                            }
                            activity.finish();
                        }
                        else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Unable to Maintain DB";
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

    @Override
    public void setDataOnSignUp(List<String> categoriesOfDoctor, List<String> timingsOfDoctors) {
        fragmentManager
                .beginTransaction()
                .hide(doctorDetailSignUpFragment)
                .commit();

    }
}