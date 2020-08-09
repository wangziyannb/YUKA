package com.wzy.yuka.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.wzy.yuka.R;

import java.text.DecimalFormat;

/**
 * <p>Title: CircleTickView</p>
 * <p>Description: 自定义成功转圈打勾动画</p>
 * <p>Copyright: Xi An BestTop Technologies, ltd. Copyright(c) 2018/p>
 */
public class CircleTickView extends View {
    private final Context mContext;
    //格式化小数，不足的补0
    DecimalFormat df = new DecimalFormat("0.0");
    private Path path;    //圆半径
    private float mCircleRadiu;    //画笔宽度（弧宽度）
    private float mStrokeWidth;
    private TypedArray typedArray;    //画圈颜色 （透明度从 0 - 1）
    private int mCircleColor;       //    private int mSelectHookColor;    //钩子的颜色
    private int mHookColor;    //圆弧（圆）画笔
    private Paint mCirclePaint;    //钩子画笔
    private Paint mHookPaint;
    private float mCircleStartX;
    private float mCircleStartY;
    private float[] index;
    private int sweepAngle;
    private boolean isDrawArc;    //画弧结束
    public boolean drawArcEnd;
    private boolean cherry;    //设置圆切头部尾部实现圆圆感觉
    private Paint mMinCirclePaint;
    private int[] mMinColors = {R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary, R.color.colorPrimary};
    private double maxCount = 100;

    /**
     * 填充色主要参数：
     * mMinColors[]
     * positions[]
     * 即每个position定义一个color值，注意position是一个相对位置，其值应该在0.0到1.0之间。
     * 0, 18.5, 45.0, 75.0, 100.0
     */
    private float[] positions = new float[mMinColors.length];
    //分割区域的数值
    private double[] position_line = new double[]{0, 18.5, 45.0, 75.0, 100.0};

    public CircleTickView(Context context) {
        this(context, null);
    }


    public CircleTickView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initAttr(attrs);
        init();
        resetPaint();

    }

    /**
     * @return void
     * @methodName initAttr
     * @description 自定义控件属性   颜色大小等
     */
    private void initAttr(AttributeSet attrs) {
        typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.TickView);
        mCircleRadiu = typedArray.getDimension(R.styleable.TickView_circleRadius, 20);
        mStrokeWidth = typedArray.getDimension(R.styleable.TickView_circle_width, 1);
        if (mStrokeWidth == 0) {
            mStrokeWidth = 2;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mCircleColor = typedArray.getColor(R.styleable.TickView_circle_color, getResources().getColor(R.color.colorPrimary));
            mHookColor = typedArray.getColor(R.styleable.TickView_circle_color, getResources().getColor(R.color.colorPrimary));
        } else {
            mCircleColor = typedArray.getColor(R.styleable.TickView_circle_color, getResources().getColor(R.color.colorPrimary, null));
            mHookColor = typedArray.getColor(R.styleable.TickView_circle_color, getResources().getColor(R.color.colorPrimary, null));
        }


    }

    public void setmCircleRadiu(int v) {
        mCircleRadiu = v;
    }

    public void setmStrokeWidth(int v) {
        mStrokeWidth = v;
    }


    /**
     * @return void
     * @methodName init
     * @description 初始化画笔属性 已经钩子的位置效果
     */
    private void init() {
        mCirclePaint = new Paint();
        mHookPaint = new Paint();
//        //一个逐渐缩小的圆
//        paint = new Paint();

        //钩子路径
        path = new Path();

        //初始化钩子三个点的坐标
        index = new float[6];
        //第一个点 x 大约在 1/3 圆直径的位置
        index[0] = mCircleRadiu * 3 / 5;
        //第一个点 y 大约在 1/2 圆直径的位置
        index[1] = (float) (mCircleRadiu * 1.15);
        //第二个点 x 大约在 1/2 圆直径的位置
        index[2] = mCircleRadiu;
        //第二个点 y 大约在圆直径的 1/2 偏下一点位置
        index[3] = (float) (1.45 * mCircleRadiu);
        //第三个点 x 大约在 2/3 圆直径的位置
        index[4] = (float) (mCircleRadiu * 1.5);
        //第三个点 y 大约在圆直径 1/2 偏上一点的位置
        index[5] = (float) (0.7 * mCircleRadiu);//        setOnClickListener(this);
    }

    /**
     * @return void
     * @methodName resetPaint
     * @description 画笔初始化  以及  钩子的位置移动位置初始化
     */
    public void resetPaint() {
        mCirclePaint.reset();
        mHookPaint.reset();
        path.reset();

        if (drawArcEnd) {
            //填充内容并描边
            mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            //描边
            mCirclePaint.setStyle(Paint.Style.STROKE);
            mCirclePaint.setStrokeWidth(mStrokeWidth);
        }

        mCirclePaint.setColor(mCircleColor);
        mHookPaint.setColor(mHookColor);

        mHookPaint.setStyle(Paint.Style.STROKE);
        mHookPaint.setStrokeWidth(mStrokeWidth);

        mCirclePaint.setAntiAlias(true);
        mHookPaint.setAntiAlias(true);

//        paint.setStyle(Paint.Style.FILL_AND_STROKE);
//        paint.setColor(getResources().getColor(R.color.common_white));
//        paint.setAntiAlias(true);

        //从第一个点开始画圈
        path.moveTo(index[0], index[1]);
        path.lineTo(index[2], index[3]);
        path.lineTo(index[4], index[5]);

//        //定义头部画笔
//        mMinCirclePaint = new Paint();
//        mMinCirclePaint.setColor(Color.BLUE);
//        mMinCirclePaint.setAntiAlias(true);
//        positions[0] = Float.parseFloat(df.format(position_line[0] / maxCount));
//        positions[1] = Float.parseFloat(df.format(position_line[1] / maxCount));
//        positions[2] = Float.parseFloat(df.format(position_line[2] / maxCount));
//        positions[3] = Float.parseFloat(df.format(position_line[3] / maxCount));
//        positions[4] = Float.parseFloat(df.format(position_line[4] / maxCount));
//
//        /**
//         * static final Shader.TileMode CLAMP: 边缘拉伸.
//         static final Shader.TileMode MIRROR：在水平方向和垂直方向交替景象, 两个相邻图像间没有缝隙.
//         Static final Shader.TillMode REPETA：在水平方向和垂直方向重复摆放,两个相邻图像间有缝隙缝隙.
//         */
//        //渐变颜色
//        LinearGradient lg = new LinearGradient(mCircleRadiu * 2, mCircleRadiu, mCircleRadiu, 0, mMinColors, positions, Shader.TileMode.MIRROR);
//        // 创建SweepGradient对象
//        // 第一个,第二个参数中心坐标
//        // 后面的参数与线性渲染相同
//
//        SweepGradient sweepGradient = new SweepGradient(mCircleRadiu, mCircleRadiu, mMinColors, positions);
//        Matrix matrix = new Matrix();
//        //加上旋转还是很有必要的，每次最右边总是有一部分多余了,不太美观,也可以不加
//        matrix.setRotate(130, mCircleRadiu, mCircleRadiu);
//        sweepGradient.setLocalMatrix(matrix);
//        lg.setLocalMatrix(matrix);
//        mCirclePaint.setShader(lg);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    /**
     * @return int
     * @methodName measure
     * @description 测量宽高
     */
    private int measure(int origin, boolean isWidth) {
        int result = (int) (mCircleRadiu * 2);        //得到模式
        int specMode = MeasureSpec.getMode(origin);        //得到尺寸
        int specSize = MeasureSpec.getSize(origin);
        switch (specMode) {
            //EXACTLY是精确尺寸，当我们将控件的layout_width或layout_height指定为具体数值时如andorid:layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
            case MeasureSpec.EXACTLY:
                //AT_MOST是最大尺寸，当控件的layout_width或layout_height指定为WRAP_CONTENT时，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
            case MeasureSpec.AT_MOST:
                result = specSize;
                if (isWidth) {
//                    widthForUnspecified = result;
                } else {
//                    heightForUnspecified = result;
                }
                break;
            //UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，通过measure方法传入的模式。
            case MeasureSpec.UNSPECIFIED:
            default:
                result = Math.min(result, specSize);
                if (isWidth) {
                    //宽或高未指定的情况下，可以由另一端推算出来 - -如果两边都没指定就用默认值
//                    result = (int) (heightForUnspecified * BODY_WIDTH_HEIGHT_SCALE);
                } else {
//                    result = (int) (widthForUnspecified / BODY_WIDTH_HEIGHT_SCALE);
                }
                if (result == 0) {
                    result = (int) (mStrokeWidth + mCircleRadiu * 2);
                }
                break;
        }

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //圆弧动态绘制结束（在这里是蓝色渐变色的圆环）
        if (drawArcEnd) {
            canvas.drawArc(new RectF(mStrokeWidth, mStrokeWidth, mCircleRadiu * 2, mCircleRadiu * 2), 0, 360, false, mCirclePaint);
            if (cherry) {
                //绘制钩子
                canvas.drawPath(path, mHookPaint);
            }
        } else {
            //动态绘制选中状态 时圆环
            canvas.drawArc(new RectF(mStrokeWidth, mStrokeWidth, mCircleRadiu * 2, mCircleRadiu * 2), 0, sweepAngle, false, mCirclePaint);
        }

    }

    /**
     * @return void
     * @methodName animation
     * @description 设置动画效果
     */
    public void animation() {

        //圆弧动画（蓝色渐变色圆环）  创建了一个值从0到300的动画，动画时长是1s
        ValueAnimator animator = ValueAnimator.ofInt(0, 360);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());        //监听ValueAnimator的动画过程来自己对控件做操作
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sweepAngle = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        //圆弧（圆环）绘制结束后，开始后面的绘制
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                drawArcEnd = true;
                cherry = true;

            }
        });
        animator.start();
    }

}
