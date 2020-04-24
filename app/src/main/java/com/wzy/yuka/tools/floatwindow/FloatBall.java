package com.wzy.yuka.tools.floatwindow;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.wzy.yuka.tools.params.SizeUtil;

public class FloatBall extends ViewGroup {

    static private boolean isleft = true;

    public FloatBall(Context context) {
        super(context);
    }

    public FloatBall(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatBall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //测量并保存layout的宽高(使用getDefaultSize时，wrap_content和match_perent都是填充屏幕)
        //稍后会重新写这个方法，能达到wrap_content的效果
        if (getChildCount() == 1) {
            setMeasuredDimension(getChildAt(0).getMeasuredWidth(), getChildAt(0).getMeasuredHeight());
        } else {
            setMeasuredDimension(widthMeasureSpec,
                    heightMeasureSpec);
        }

    }

    @Override
    public void addView(View child) {
        final int count = getChildCount();
        super.addView(child, count);
        LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = SizeUtil.dp2px(getContext(), 52 * 2 + 44);
        layoutParams.width = SizeUtil.dp2px(getContext(), (float) (52 / 2 * Math.sqrt(3) + 44));
        setLayoutParams(layoutParams);
        Log.d("TAG", "addView: " + layoutParams.height + layoutParams.width);
    }

    @Override
    public void removeViewAt(int index) {
        super.removeViewAt(index);
    }

    public void addView() {

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d("TAG", "onSizeChanged:" + w + " " + h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int[] center = {(right - left) / 2, (bottom - top) / 2};
        Log.d("TAG", "onLayout: " + center[0] + " " + center[1]);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int radius = child.getMeasuredWidth() / 2;
            if (count > 1) {
                center[0] = center[0] - child.getWidth() / 2;
                if (i == 0) {
                    child.layout(center[0] - radius, center[1] - radius,
                            center[0] + radius, center[1] + radius);
                } else {
                    int[] newCoordinate = calculate(center, SizeUtil.dp2px(getContext(), 52), i - 1);
                    child.layout(newCoordinate[0] - radius, newCoordinate[1] - radius,
                            newCoordinate[0] + radius, newCoordinate[1] + radius);
                }
            } else {
                child.layout(center[0] - radius, center[1] - radius,
                        center[0] + radius, center[1] + radius);
            }
            //确定子控件的位置，四个参数分别代表（左上右下）点的坐标值
        }
    }


    private int[] calculate(int[] center, int radius, int index) {
        int[] newCoordinate = new int[2];
        newCoordinate[0] = (int) (center[0] + radius * Math.sin(((float) index / 6) * 2 * Math.PI));
        newCoordinate[1] = (int) (center[1] - radius * Math.cos(((float) index / 6) * 2 * Math.PI));
        return newCoordinate;
    }

}
