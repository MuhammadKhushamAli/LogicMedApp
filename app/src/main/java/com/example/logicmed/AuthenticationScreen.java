package com.example.logicmed;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AuthenticationScreen extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private ValueAnimator valueAnimator;
    private RecyclerView.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authentication_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        setAuthSectionHeight();
    }

    private void init() {
        TabLayout tabLayout = findViewById(R.id.auth_tab_layout);
        viewPager2 = findViewById(R.id.auth_viewpager2);
        viewPager2.setAdapter(
                new AuthViewPagerAdapter(this)
        );
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout,
                viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int i) {
                        switch (i) {
                            case 0:
                                tab.setIcon(R.drawable.login);
                                break;
                            case 1:
                                tab.setIcon(R.drawable.signup);
                                break;
                        }
                    }
                }
        );
        tabLayoutMediator.attach();

    }
    private void setAuthSectionHeight() {
        viewPager2.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);

                        viewPager2.post(() -> {
                            RecyclerView recyclerView = (RecyclerView) viewPager2.getChildAt(0);
                            assert recyclerView.getLayoutManager() != null : "Recycler View Has no Layout Manager";
                            View view  = recyclerView.getLayoutManager().findViewByPosition(position);
                            assert view != null : "View is null in Auth Activity";
                            view.post(() -> {
                                int measureX = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);
                                int measureY = View.MeasureSpec.makeMeasureSpec(view.getHeight(), View.MeasureSpec.UNSPECIFIED);
                                view.measure(measureX, measureY);
                                int measuredHeight = view.getMeasuredHeight();

                                layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
                                authPagerAnimator(view, measuredHeight);
                                valueAnimator.start();
                            });

                        });
                    }
                }
        );
    }
    private void authPagerAnimator(View view, int endHeight) {
        valueAnimator = ValueAnimator.ofInt(view.getHeight(), endHeight);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(animatedValue -> {
            layoutParams.height = (int) animatedValue.getAnimatedValue();
            view.setLayoutParams(layoutParams);
        });
    }
}