package com.example.logicmed;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class onBoardScreen extends AppCompatActivity {
    private TextView tvTitle;
    private RelativeLayout lMainImagesSection;
    private LinearLayout lGetStartSection;
    Animation topToCurrent;
    Animation rotateToCurrent;
    Animation bottomToCurrent;
    Animation invisibleToVisible;
    TextView tvTermsAgreement;
    Button btnetStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_board_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        setAnimations();
    }

    private void init() {
        tvTitle = findViewById(R.id.on_board_title);
        tvTermsAgreement = findViewById(R.id.on_board_term_agreement);
        btnetStart = findViewById(R.id.on_board_get_start_slider);
        lMainImagesSection = findViewById(R.id.on_board_main_img_section);
        lGetStartSection = findViewById(R.id.on_board_get_start_section);
        lMainImagesSection.setVisibility(View.INVISIBLE);
        tvTermsAgreement.setVisibility(View.INVISIBLE);
        btnetStart.setVisibility(View.INVISIBLE);
        lMainImagesSection.setRotation(90);

        topToCurrent = AnimationUtils.loadAnimation(this, R.anim.top_to_current);
        bottomToCurrent = AnimationUtils.loadAnimation(this, R.anim.bottom_to_current);
        rotateToCurrent = AnimationUtils.loadAnimation(this, R.anim.rotate_to_current);
        invisibleToVisible = AnimationUtils.loadAnimation(this, R.anim.invisible_to_visible);
    }

    private void setAnimations() {
        tvTitle.setAnimation(topToCurrent);
        lGetStartSection.setAnimation(bottomToCurrent);

        lMainImagesSection.postDelayed(() -> {
            lMainImagesSection.setVisibility(View.VISIBLE);
            lMainImagesSection.setAnimation(invisibleToVisible);
        }, 2000);

        lMainImagesSection.postDelayed(() -> {
            lMainImagesSection.setRotation(0);
            lMainImagesSection.startAnimation(rotateToCurrent);
        }, 4000);

        tvTermsAgreement.postDelayed(() -> {
            tvTermsAgreement.setVisibility(View.VISIBLE);
            tvTermsAgreement.startAnimation(invisibleToVisible);
        }, 6000);

        btnetStart.postDelayed(() -> {
            btnetStart.setVisibility(View.VISIBLE);
            btnetStart.startAnimation(invisibleToVisible);
        }, 6000);
    }
}