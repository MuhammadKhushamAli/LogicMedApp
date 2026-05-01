package com.example.logicmed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.Objects;

public class LoginFragment extends Fragment {
    TextInputEditText teEmail;
    TextInputEditText tePassword;
    MaterialButton btnLogin;
    ProgressBar progressBar;
    MyApplication app;

    public LoginFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        btnLogin.setOnClickListener(v -> {
            String email = Objects.requireNonNull(teEmail.getText()).toString();
            String password = Objects.requireNonNull(tePassword.getText()).toString();
            firebaseAuthLogin(email, password);
        });

    }
    private void init(View view) {
        teEmail = view.findViewById(R.id.login_email);
        tePassword = view.findViewById(R.id.login_pass);
        btnLogin = view.findViewById(R.id.login_btn);
        progressBar = view.findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.GONE);
        app = (MyApplication) requireContext().getApplicationContext();
    }
    private void firebaseAuthLogin(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        Context context = requireContext();
        Activity activity = requireActivity();
        app.firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                                app.firestore.collection(KeyUtils.firebaseUserCollectionKey)
                                                .document(Objects.requireNonNull(task.getResult().getUser()).getUid())
                                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    User user = document.toObject(User.class);

                                                    if (user == null) {
                                                        Toast.makeText(context, "Unable to get Data", Toast.LENGTH_LONG).show();
                                                        return;
                                                    }

                                                    if (
                                                            app.sPrefUserEdit
                                                                    .putBoolean(KeyUtils.isLoggedInPrefKey, true)
                                                                    .putString(KeyUtils.rolePrefKey, user.getRole())
                                                                    .putString(KeyUtils.namePrefKey, user.getFullName())
                                                                    .putString(KeyUtils.emailPrefKey, email)
                                                                    .putString(KeyUtils.profileUrlPrefKey, user.getProfileImageUrl())
                                                                    .commit()
                                                    ) {

                                                        startActivity(
                                                                new Intent(
                                                                        context,
                                                                        MainActivity.class
                                                                )
                                                        );
                                                        activity.finish();
                                                    }
                                                    else {
                                                        Toast.makeText(context, "Unable to Update Login State", Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            }
                                        });
                        }
                        else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Unable to Login";
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}