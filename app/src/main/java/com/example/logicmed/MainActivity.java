package com.example.logicmed;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity{
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private MyApplication app;
    private FragmentManager fragmentManager;
    private Boolean isPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_activity_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init(savedInstanceState);
        linkFragsToBottomNavBar();
        linkingBackStackToBottomNavBar();
    }
    private void init(Bundle savedInstanceState) {
        isPrev = false;
        app = (MyApplication) getApplicationContext();
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.main_fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, (v, insets) -> insets);

        if (app.sPrefUser.getString(KeyUtils.rolePrefKey, KeyUtils.patientKey).equals(KeyUtils.doctorKey)) {
            Menu menu = bottomNavigationView.getMenu();
            menu.removeItem(R.id.search_frag);
        }


        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.drawer_nav_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View view = navigationView.getHeaderView(0);
        ImageView ivProfile = view.findViewById(R.id.drawer_nav_profile_icon);
        TextView tvName = view.findViewById(R.id.drawer_nav_profile_name);
        TextView tvEmail = view.findViewById(R.id.drawer_nav_profile_email);

        ImageButton ibLogout = toolbar.findViewById(R.id.logout_btn);
        ibLogout.setOnClickListener(v -> {
            app.firebaseAuth.signOut();
            app.sPrefUserEdit.clear().apply();
            startActivity(
                    new Intent(
                            this,
                            AuthenticationScreen.class
                    )
            );
            finish();
        });

        String cloudinaryProfileURL = app.sPrefUser.getString(KeyUtils.profileUrlPrefKey, "");

        if (!(cloudinaryProfileURL.isEmpty())) {
            Glide.with(this)
                    .load(cloudinaryProfileURL)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(ivProfile);
        }
        tvEmail.setText(app.sPrefUser.getString(KeyUtils.emailPrefKey, ""));
        tvName.setText(app.sPrefUser.getString(KeyUtils.namePrefKey, ""));

        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openDrawerString,
                R.string.closeDrawerString
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
    private void linkFragsToBottomNavBar() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (isPrev) {
                isPrev = false;
                return true;
            }
            int id = item.getItemId();
            Fragment selectedFrag = null;

            if (id == R.id.home_frag) {
                selectedFrag = new HomeFragment();
            }
            else if (id == R.id.search_frag) {
                selectedFrag = new SearchFragment();
            }
            else if (id == R.id.chat_frag) {
                selectedFrag = new ChatFragment();
            }
            else if (id == R.id.ai_chat_frag) {
                startActivity(new Intent(
                        this,
                        AIActivity.class
                ));
            }

            if (selectedFrag != null) {
                fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.main_fragment_container, selectedFrag)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            return false;
        });
    }
    private void linkingBackStackToBottomNavBar() {
        fragmentManager.addOnBackStackChangedListener(() -> {
            Fragment currentFrag = fragmentManager.findFragmentById(R.id.main_fragment_container);
            int id = -1;
            if (currentFrag instanceof HomeFragment) {
                id = R.id.home_frag;
            }
            else if (currentFrag instanceof SearchFragment) {
                id = R.id.search_frag;
            }
            else if (currentFrag instanceof ChatFragment) {
                id = R.id.chat_frag;
            }

            if (id != -1) {
                isPrev = true;
                bottomNavigationView.setSelectedItemId(id);
            }
        });
    }

}