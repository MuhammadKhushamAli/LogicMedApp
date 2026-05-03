package com.example.logicmed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.UploadRequest;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SignupFragment extends Fragment
        implements DoctorDetailSignUpFragment.setOnSignUpListener, ProfileSetupFragment.setOnClickListener{
    private TextInputEditText teFullName;
    private TextInputEditText teEmail;
    private AutoCompleteTextView atvRole;
    private AutoCompleteTextView atLocation;
    private TextInputEditText tePassword;
    private TextInputEditText teCPassword;
    private MaterialButton btnSignup;
    private ProgressBar progressBar;
    private MyApplication app;
    private FragmentManager fragmentManager;
    private FragmentContainerView fragmentContainerView;
    private ProfileSetupFragment profileSetupFragment;
    private String fullName;
    private String email;
    private String role;
    private String city;
    private String password;
    private String cPassword;
    private ViewModelProvider viewModelProvider;
    private List<String> docCategories;
    private List<Schedule> docTimings;
    private String cloudinaryURL;
    private float fee;
    private float timeSlot;
    private Context context;

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
        list.add(KeyUtils.doctorKey);
        list.add(KeyUtils.patientKey);

        init(view);
        setDropDown(atvRole, list, false);
        fetchCities("Pakistan", "https://countriesnow.space/api/v0.1/countries/cities");

        btnSignup.setOnClickListener(v -> {
            fullName = Objects.requireNonNull(teFullName.getText()).toString();
            email = Objects.requireNonNull(teEmail.getText()).toString();
            role = Objects.requireNonNull(atvRole.getText()).toString();
            city = Objects.requireNonNull(atLocation.getText()).toString();
            password = Objects.requireNonNull(tePassword.getText()).toString();
            cPassword = Objects.requireNonNull(teCPassword.getText()).toString();

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
                View snackbarView = getView();
                assert snackbarView != null : "View is Null for Snack bar";
                Snackbar snackbar = Snackbar.make(snackbarView, message, Snackbar.LENGTH_SHORT);
                TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                textView.setMaxLines(4);
                snackbar.show();
                return;
            }


            if (role.equals(KeyUtils.doctorKey)) {
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.signup_detail_frag, new DoctorDetailSignUpFragment())
                        .commit();
            }
            else {
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.signup_detail_frag, profileSetupFragment)
                        .commit();
            }
            fragmentContainerView.setVisibility(View.VISIBLE);
            viewModelProvider.get(SignupViewModel.class).setCurrentPage(1);
        });
    }

    private void init(View view) {
        context = requireContext();
        teFullName = view.findViewById(R.id.signup_name);
        teEmail = view.findViewById(R.id.signup_email);
        atvRole = view.findViewById(R.id.signup_role);
        atLocation = view.findViewById(R.id.signup_location);
        tePassword = view.findViewById(R.id.signup_pass);
        teCPassword = view.findViewById(R.id.signup_c_pass);
        progressBar = view.findViewById(R.id.signup_progress_bar);
        btnSignup = view.findViewById(R.id.signup_btn);
        app = (MyApplication) requireContext().getApplicationContext();
        fragmentManager = getChildFragmentManager();
        progressBar.setVisibility(View.GONE);
        fragmentContainerView = view.findViewById(R.id.signup_detail_frag);
        profileSetupFragment = new ProfileSetupFragment();
        fragmentContainerView.setVisibility(View.GONE);
        viewModelProvider = new ViewModelProvider(requireActivity());

        String nextText = "Next";
        btnSignup.setText(nextText);
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
                                viewModelProvider.get(SignupViewModel.class).setCurrentPage(1);
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
    private void firebaseAuth(User user) {
        Context context = requireContext();
        Activity activity = requireActivity();



        progressBar.setVisibility(View.VISIBLE);

        app.firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            assert app.firebaseAuth.getCurrentUser() != null;
                            java.lang.String uID = app.firebaseAuth.getCurrentUser().getUid();

                            firebaseDB(uID, user);
                        }
                        else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Unable to Signup";
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
    }
    private void firebaseDB(String uID, User user) {
        Context context = requireContext();
        Activity activity = requireActivity();

        app.firestore.collection(KeyUtils.firebaseUserCollectionKey)
                .document(uID)
                .set(user)
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (
                                    app.sPrefUserEdit
                                            .putBoolean(KeyUtils.isLoggedInPrefKey, true)
                                            .putString(KeyUtils.rolePrefKey, role)
                                            .putString(KeyUtils.namePrefKey, fullName)
                                            .putString(KeyUtils.emailPrefKey, email)
                                            .commit()
                            ) {

                                startActivity(
                                        new Intent(
                                                context,
                                                MainActivity.class
                                        )
                                );
                                app.cities.clear();
                                app.doctorsCategoriesAndSubCategories.clear();
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
    public void setDataOnSignUp(List<String> categoriesOfDoctor, List<Schedule> timingsOfDoctors, float fee, float timeSlot) {
        fragmentManager
                .beginTransaction()
                .replace(R.id.signup_detail_frag, profileSetupFragment)
                .commit();
        viewModelProvider.get(SignupViewModel.class).setCurrentPage(1);
        this.docCategories = categoriesOfDoctor;
        this.docTimings = timingsOfDoctors;
        this.fee = fee;
        this.timeSlot = timeSlot;
    }

    private void inputAndMakeMap() {
        User user = null;
        if (role.equals(KeyUtils.doctorKey)) {
            user = new Doctor(fullName, cloudinaryURL, role, city, fee, timeSlot, docCategories, docTimings);
        }
        else {
            user = new User(fullName, cloudinaryURL, role, city);
        }
        firebaseAuth(user);
    }

    @Override
    public void onClickListener(Bitmap bitmap, Uri uri) {
        fragmentManager.beginTransaction()
                .remove(profileSetupFragment)
                .commit();
        fragmentContainerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        viewModelProvider.get(SignupViewModel.class).setCurrentPage(1);

        if (bitmap != null || uri != null) {
            uploadToCloudinary(bitmap, uri);
        }
        else {
            this.cloudinaryURL = null;
            inputAndMakeMap();
        }
    }
    private void uploadToCloudinary(Bitmap bitmap, Uri uri) {
        MediaManager mediaManager = MediaManager.get();
        UploadRequest<?> uploadRequest = null;
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            uploadRequest = mediaManager.upload(bytes);
        }
        else if (uri != null) {
            uploadRequest = mediaManager.upload(uri);
        }
        if (uploadRequest != null) {
            uploadRequest.unsigned("logicmed")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {

                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {

                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            cloudinaryURL = Objects.requireNonNull(resultData.get("secure_url")).toString();
                            inputAndMakeMap();
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Toast.makeText(requireContext(), "Unable to Upload Image", Toast.LENGTH_LONG).show();
                            return;
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }
                    }).dispatch();
        }

    }
}