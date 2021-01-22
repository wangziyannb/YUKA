package com.wzy.yuka.yuka_lite.floatwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class ScaleImageView extends ImageView {
    private float touchDownX = 0f;
    private float touchDownY = 0f;

    private OnScaledListener onScaledListener;

    public ScaleImageView(Context context) {
        super(context);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScaledListener(OnScaledListener onScaledListener) {
        this.onScaledListener = onScaledListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return super.onTouchEvent(event);
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = event.getX();
                touchDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (onScaledListener != null) {
                    onScaledListener.onScaled(event.getX() - touchDownX, event.getY() - touchDownY, event);
                }
                break;
            default:
                break;
        }
        return true;
    }

    public interface OnScaledListener {
        void onScaled(float x, float y, MotionEvent event);
    }
}
