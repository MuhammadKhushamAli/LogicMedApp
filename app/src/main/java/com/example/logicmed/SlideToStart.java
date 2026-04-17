package com.example.logicmed;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
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
    private View vSliderCover;
    onSlideListener listener;
    RelativeLayout.LayoutParams layoutParams;
    ValueAnimator valueAnimator;

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
        vSliderCover = findViewById(R.id.slider_cover);
        layoutParams = (RelativeLayout.LayoutParams) vSliderCover.getLayoutParams();

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideToStart);
            try {
                String sliderText = typedArray.getString(R.styleable.SlideToStart_slider_text);
                tvSliderText.setText(sliderText);
                int color = typedArray.getColor(R.styleable.SlideToStart_slider_cover_color, Color.BLACK);
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setCornerRadius(60f);
                drawable.setColor(color);
                vSliderCover.setBackground(drawable);


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
                final float sliderBtnWidth = btnSlider.getWidth();
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

                        float maxTranslationX = lSliderPath.getWidth() - sliderBtnWidth - 40;

                        if (distMoveX > 0 && newX <= maxTranslationX) {
                            view.setX(newX);
                            Toast.makeText(getContext(), "" + sliderBtnWidth, Toast.LENGTH_LONG).show();
                            layoutParams.width = (int) (newX + sliderBtnWidth - 50);
                            vSliderCover.setLayoutParams(layoutParams);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        float currentRelX = view.getX();
                        float maxTransX = lSliderPath.getWidth() - btnSlider.getWidth() - 40;
                        if (currentRelX >= (maxTransX * 0.8)) {
                            coverWidthAnimator((int) (maxTransX + sliderBtnWidth - 50));
                            view.animate().x(maxTransX).setDuration(2000).start();
                            valueAnimator.start();
                            listener.onSlideComplete();
                            view.performClick();
                        }
                        else {
                            coverWidthAnimator(0);
                            view.animate().x(initialRelXPreserve).setDuration(2000).start();
                            valueAnimator.start();
                        }
                        return true;
                }
                return false;
            }
        });
    }
    private void coverWidthAnimator(int endWidth) {
        valueAnimator = ValueAnimator.ofInt(vSliderCover.getWidth(), endWidth);
        valueAnimator.setDuration(2500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                layoutParams.width = (int) valueAnimator.getAnimatedValue();
                vSliderCover.setLayoutParams(layoutParams);
            }
        });
    }
    public void setSliderSliderListener(onSlideListener listener) {
        this.listener = listener;
    }
}
