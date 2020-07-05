package com.wzy.yuka.ui.view;

/*
 * Modified by Ziyan on 2020/5/30.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.wzy.yuka.R;


public class SubtitleFlowView extends TextSwitcher implements
        ViewSwitcher.ViewFactory {
    private float mHeight;
    private Context mContext;
    private String mFirstText;
    private int mtextAlignment;
    private int mtextColor;
    //mInUp,mOutUp分离构成向下翻页的进出动画
    private Rotate3dAnimation mInUp;
    private Rotate3dAnimation mOutUp;

    //mInDown,mOutDown分离构成向下翻页的进出动画
    private Rotate3dAnimation mInDown;
    private Rotate3dAnimation mOutDown;

    public SubtitleFlowView(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    public SubtitleFlowView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // TODO Auto-generated constructor stub
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SubtitleFlowView);
        mHeight = a.getInteger(R.styleable.SubtitleFlowView_textSize, 15);
        mFirstText = a.getString(R.styleable.SubtitleFlowView_firstText);
        mtextAlignment = a.getInteger(R.styleable.SubtitleFlowView_textAlignment, 0);

        mtextColor = a.getColor(R.styleable.SubtitleFlowView_textColor, getResources().getColor(R.color.text_color_DarkBg, null));

        a.recycle();
        mContext = context;
        init();
    }

    private void init() {
        // TODO Auto-generated method stub
        setFactory(this);
        mInUp = createAnim(-90, 0, true, true);
        mOutUp = createAnim(0, 90, false, true);
        mInDown = createAnim(90, 0, true, false);
        mOutDown = createAnim(0, -90, false, false);
        //TextSwitcher重要用于文件切换，比如 从文字A 切换到 文字 B，
        //setInAnimation()后，A将执行inAnimation，
        //setOutAnimation()后，B将执行OutAnimation
        setInAnimation(mInUp);
        setOutAnimation(mOutUp);

        super.setCurrentText(mFirstText);
    }

    private Rotate3dAnimation createAnim(float start, float end, boolean turnIn, boolean turnUp) {
        final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end, turnIn, turnUp);
        rotation.setDuration(800);
        rotation.setFillAfter(false);
        rotation.setInterpolator(new AccelerateInterpolator());
        return rotation;
    }

    //这里返回的TextView，就是我们看到的View
    @Override
    public View makeView() {
        // TODO Auto-generated method stub
        TextView t = new TextView(mContext);
        t.setGravity(Gravity.CENTER);
        t.setTextSize(mHeight);
        t.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        t.setMaxLines(3);
        t.setTextAlignment(mtextAlignment);
        t.setTextColor(mtextColor);
        return t;
    }

    @Override
    public void setBackgroundResource(int resid) {
        mtextColor = resid;
    }

    public void setText(CharSequence text, boolean next) {
        TextView textView = (TextView) getCurrentView();
        TextPaint textPaint = textView.getPaint();
        float mWidth = textView.getWidth();
        float[] charWidths = new float[text.length()];
        textPaint.getTextWidths(text.toString(), charWidths);
        float total_width = 0f;
        Log.d("TAG", "setText: " + mWidth);
        for (int i = 0; i < charWidths.length; i++) {
            total_width += charWidths[i];
            Log.d("TAG", "setText: " + total_width);
            if (total_width > mWidth * 3) {
                text = text.subSequence(i, text.length());
                setText(text, true);
                return;
            }
        }
        if (next) {
            super.setText(text);
        } else {
            super.setCurrentText(text);
        }
    }

    @Override
    public void setText(CharSequence text) {
        setText(text, false);
    }

    //定义动作，向下滚动翻页
    public void previous() {
        if (getInAnimation() != mInDown) {
            setInAnimation(mInDown);
        }
        if (getOutAnimation() != mOutDown) {
            setOutAnimation(mOutDown);
        }
    }

    //定义动作，向上滚动翻页
    public void next() {
        if (getInAnimation() != mInUp) {
            setInAnimation(mInUp);
        }
        if (getOutAnimation() != mOutUp) {
            setOutAnimation(mOutUp);
        }
    }

    class Rotate3dAnimation extends Animation {
        private final float mFromDegrees; //初始值
        private final float mToDegrees; //最终值
        private final boolean mTurnIn; //进
        private final boolean mTurnUp; //出
        private float mCenterX;
        private float mCenterY;
        private Camera mCamera;

        Rotate3dAnimation(float fromDegrees, float toDegrees, boolean turnIn, boolean turnUp) {

            mFromDegrees = fromDegrees;
            mToDegrees = toDegrees;
            mTurnIn = turnIn;
            mTurnUp = turnUp;
        }

        //初始化动作
        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            mCamera = new Camera();

            mCenterY = height / 2;
            mCenterX = width / 2;
        }

        //定义动画效果
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final float fromDegrees = mFromDegrees;
            //当前值 = 初始值 + （最终值 - 初始值） * interpolatedTime;
            float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

            final float centerX = mCenterX;
            final float centerY = mCenterY;
            final Camera camera = mCamera;
            final int derection = mTurnUp ? 1 : -1;

            //通过camera进行一些矩阵操作，最后对matrix进行变化
            final Matrix matrix = t.getMatrix();

            camera.save();
            if (mTurnIn) {
                camera.translate(0.0f, derection * mCenterY * (interpolatedTime - 1.0f), 0.0f);
            } else {
                camera.translate(0.0f, derection * mCenterY * (interpolatedTime), 0.0f);
            }
            camera.rotateX(degrees);
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }
}