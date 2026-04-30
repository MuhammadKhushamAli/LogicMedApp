package com.example.logicmed;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private MyApplication app;

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

        init();
    }
    private void init() {
        app = (MyApplication) getApplicationContext();

        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, (v, insets) -> insets);

        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.drawer_nav_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View view = navigationView.getHeaderView(0);
        ImageView ivProfile = view.findViewById(R.id.drawer_nav_profile_icon);
        TextView tvName = view.findViewById(R.id.drawer_nav_profile_name);
        TextView tvEmail = view.findViewById(R.id.drawer_nav_profile_email);

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
}