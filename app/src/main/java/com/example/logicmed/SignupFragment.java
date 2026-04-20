package com.example.logicmed;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Trace;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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


public class SignupFragment extends Fragment {
    private TextInputEditText teFullName;
    private TextInputEditText teEmail;
    private AutoCompleteTextView atvRole;
    private AutoCompleteTextView atLocation;
    private TextInputEditText tePassword;
    private TextInputEditText teCPassword;
    private ProgressBar progressBar;
    MyApplication app;
    Boolean isFetching;


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
    }

    private void init(View view) {
        teFullName = view.findViewById(R.id.signup_name);
        teEmail = view.findViewById(R.id.signup_email);
        atvRole = view.findViewById(R.id.signup_role);
        atLocation = view.findViewById(R.id.signup_location);
        tePassword = view.findViewById(R.id.signup_pass);
        teCPassword = view.findViewById(R.id.signup_c_pass);
        progressBar = view.findViewById(R.id.signup_progress_bar);
        app = (MyApplication) requireContext().getApplicationContext();

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

        isFetching = Boolean.TRUE;
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
}