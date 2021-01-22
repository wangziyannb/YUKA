package com.wzy.yuka.yuka_lite.floatball;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.wzy.yuka.R;
import com.wzy.yuka.yuka_lite.utils.SizeUtil;


public class FloatBallLayout extends ViewGroup {
    public boolean isDeployed = false;
    private boolean mIsLeft = true;
    private FloatBallLayoutListener mListener;
    private boolean ifAnimate;

    public FloatBallLayout(Context context) {
        this(context, null);
    }

    public FloatBallLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public FloatBallLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray floatBallArray = context.obtainStyledAttributes(attrs, R.styleable.FloatBallLayout);
        ifAnimate = floatBallArray.getBoolean(R.styleable.FloatBallLayout_animateLayoutChanges, false);
        floatBallArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //测量并保存layout的宽高(使用getDefaultSize时，wrap_content和match_perent都是填充屏幕)
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
    }


    public void fold() {
        if (mListener != null) {
            mListener.onFold();
        }
        isDeployed = false;
        if (ifAnimate) {
            performAnimations();
        } else {
            removeViews();
        }
    }

    private void removeViews() {
        do {
            removeViewAt(getChildCount() - 1);
        } while (getChildCount() != 1);
        if (mListener != null) {
            mListener.folded();
        }
        isDeployed = false;
    }

    public void expand(View[] views) {
        if (mListener != null) {
            mListener.onDeploy();
        }
        for (int i = 0; i < 4; i++) {
            addView(views[i]);
        }
    }

    public void setIfAnimate(boolean ifAnimate) {
        this.ifAnimate = ifAnimate;
    }

    public FloatBallLayoutListener getFloatBallLayoutListener() {
        return mListener;
    }

    public void setFloatBallLayoutListener(FloatBallLayoutListener floatBallLayoutListener) {
        this.mListener = floatBallLayoutListener;
    }

    public void removeFloatBallLayoutListener() {
        this.mListener = null;
    }


    public void setIsLeft(boolean isLeft) {
        this.mIsLeft = isLeft;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int[] center = {(right - left) / 2, (bottom - top) / 2};
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int radius = child.getMeasuredWidth() / 2;
            if (count > 1) {
                //有数个view，分情况考虑（第一个view要放到正中间）
                if (mIsLeft) {
                    center[0] = center[0] - child.getWidth() / 2;
                } else {
                    center[0] = center[0] + child.getWidth() / 2;
                }
                if (i == 0) {
                    child.layout(center[0] - radius, center[1] - radius,
                            center[0] + radius, center[1] + radius);
                } else if (ifAnimate) {
                    child.setAlpha(0f);
                    child.layout(center[0] - radius, center[1] - radius,
                            center[0] + radius, center[1] + radius);
                } else {
                    int[] newCoordinate = calculate(center, SizeUtil.dp2px(getContext(), 52), i - 1);
                    child.layout(newCoordinate[0] - radius, newCoordinate[1] - radius,
                            newCoordinate[0] + radius, newCoordinate[1] + radius);
                }
            } else {
                //只有一个View
                child.layout(center[0] - radius, center[1] - radius,
                        center[0] + radius, center[1] + radius);
            }
            //确定子控件的位置，四个参数分别代表（左上右下）点的坐标值
        }
        if (getChildCount() == 5) {
            isDeployed = true;
            if (ifAnimate) {
                performAnimations();
            }
            if (mListener != null) {
                mListener.deployed();
            }

        }
    }

    private void performAnimations() {
        int[] center = {(getRight() - getLeft()) / 2, (getBottom() - getTop()) / 2};
        int count = getChildCount();
        if (isDeployed) {
            for (int i = 1; i < count; i++) {
                View child = getChildAt(i);
                if (mIsLeft) {
                    center[0] = center[0] - child.getWidth() / 2;
                } else {
                    center[0] = center[0] + child.getWidth() / 2;
                }
                int[] newCoordinate = calculate(center, SizeUtil.dp2px(getContext(), 52), i - 1);
                ObjectAnimator animatorX = ObjectAnimator
                        .ofFloat(child, "translationX", 0f, newCoordinate[0] - center[0]);
                ObjectAnimator animatorY = ObjectAnimator
                        .ofFloat(child, "translationY", 0f, newCoordinate[1] - center[1]);
                ObjectAnimator animatorAlpha = ObjectAnimator
                        .ofFloat(child, "alpha", 0f, 1f);
                AnimatorSet xySet = new AnimatorSet();
                xySet.play(animatorX).with(animatorY).with(animatorAlpha);
                xySet.setDuration(200);
                xySet.start();
            }
        } else {
            for (int i = 1; i < count; i++) {
                View child = getChildAt(i);
                if (mIsLeft) {
                    center[0] = center[0] - child.getWidth() / 2;
                } else {
                    center[0] = center[0] + child.getWidth() / 2;
                }
                int[] newCoordinate = calculate(center, SizeUtil.dp2px(getContext(), 52), i - 1);
                ObjectAnimator animatorX = ObjectAnimator
                        .ofFloat(child, "translationX", newCoordinate[0] - center[0], 0f);
                ObjectAnimator animatorY = ObjectAnimator
                        .ofFloat(child, "translationY", newCoordinate[1] - center[1], 0f);
                ObjectAnimator animatorAlpha = ObjectAnimator
                        .ofFloat(child, "alpha", 1f, 0f);
                AnimatorSet xySet = new AnimatorSet();
                xySet.play(animatorX).with(animatorY).with(animatorAlpha);
                xySet.setDuration(200);
                if (i == count - 1) {
                    xySet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            removeViews();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }
                xySet.start();
            }

        }
    }


    private int[] calculate(int[] center, int radius, int index) {
        int[] newCoordinate = new int[2];
        if (mIsLeft) {
            newCoordinate[0] = (int) (center[0] + radius * Math.sin(((float) index / 6) * 2 * Math.PI));
        } else {
            newCoordinate[0] = (int) (center[0] - radius * Math.sin(((float) index / 6) * 2 * Math.PI));
        }
        newCoordinate[1] = (int) (center[1] - radius * Math.cos(((float) index / 6) * 2 * Math.PI));
        return newCoordinate;
    }

    public interface FloatBallLayoutListener {
        void deployed();

        void onDeploy();

        void onFold();

        void folded();
    }
}
