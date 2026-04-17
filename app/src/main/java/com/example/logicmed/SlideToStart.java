package com.example.logicmed;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;

public class SlideToStart extends RelativeLayout {
    private SliderButton btnSlider;
    private RelativeLayout lSliderPath;
    onSlideListener listener;

    @Override
    public boolean requestChildRectangleOnScreen(@NonNull View child, @NonNull Rect rectangle, boolean immediate, int source) {
        return super.requestChildRectangleOnScreen(child, rectangle, immediate, source);
    }


    public interface onSlideListener {
        void onSlideComplete();
    }

    public SlideToStart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.slide_to_start, this);
        btnSlider = findViewById(R.id.slider_btn);
        lSliderPath = findViewById(R.id.slider_path);
        TextView tvSliderText = findViewById(R.id.slider_text);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideToStart);
            try {
                String sliderText = typedArray.getString(R.styleable.SlideToStart_slider_text);
                tvSliderText.setText(sliderText);

            } finally {
                typedArray.recycle();
            }
        }


        slideHandler();
    }

    private void slideHandler() {
        btnSlider.setOnTouchListener(new OnTouchListener() {
            private float initialRelativeX;
            private float initialAbsX;
            private float initialRelXPreserve = 0.0f;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialRelativeX = view.getX();
                        initialAbsX = motionEvent.getRawX();
                        if (initialRelXPreserve == 0.0f) {
                            initialRelXPreserve = view.getX();
                        }
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float distMoveX = motionEvent.getRawX() - initialAbsX;
                        float newX = initialRelativeX + distMoveX;

                        float maxTranslationX = lSliderPath.getWidth() - btnSlider.getWidth() - 40;

                        if (distMoveX > 0 && newX <= maxTranslationX) {
                            view.setX(newX);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        float currentRelX = view.getX();
                        float maxTransX = lSliderPath.getWidth() - btnSlider.getWidth() - 40;
                        if (currentRelX >= (maxTransX * 0.8)) {
                            view.animate().x(maxTransX).setDuration(2000).start();
                            view.performClick();
                        }
                        else {
                            view.animate().x(initialRelXPreserve).setDuration(2000).start();
                        }
                        return true;
                }
                return false;
            }
        });
    }
    public void setSliderSliderListener(onSlideListener listener) {
        this.listener = listener;
    }
}
