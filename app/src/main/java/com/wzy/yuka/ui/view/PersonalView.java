package com.wzy.yuka.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wzy.yuka.R;

public class PersonalView extends RelativeLayout {
    private ImageView leftIcon;//设置左侧图标
    private TextView leftText;//左侧标题文字
    private TextView rightText;//右侧描述文字
    private ImageView bottomLine;//下划线
    private ImageView rightArrow;//右侧小箭头
    private boolean isLeftIcon;//是否显示左侧图标
    private boolean isBottomLine;//是否显示下划线
    private boolean isRightArrow;//是否显示右侧小箭头

    public PersonalView(Context context) {
        this(context, null);
    }

    public PersonalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PersonalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.personal_view, this);
        TypedArray PersonalViewArray = context.obtainStyledAttributes(attrs, R.styleable.PersonalView);
        isBottomLine = PersonalViewArray.getBoolean(R.styleable.PersonalView_show_bottom_line, true);
        isLeftIcon = PersonalViewArray.getBoolean(R.styleable.PersonalView_show_left_icon, true);
        isRightArrow = PersonalViewArray.getBoolean(R.styleable.PersonalView_show_right_arrow, true);

        bottomLine = findViewById(R.id.bottom_line);
        leftIcon = findViewById(R.id.left_icon);
        leftText = findViewById(R.id.left_text);
        rightText = findViewById(R.id.right_text);
        rightArrow = findViewById(R.id.right_arrow);

        leftText.setText(PersonalViewArray.getString(R.styleable.PersonalView_left_text));
        leftIcon.setBackground(PersonalViewArray.getDrawable(R.styleable.PersonalView_left_icon));//设置左侧图标
        rightText.setText(PersonalViewArray.getString(R.styleable.PersonalView_right_text));

        leftIcon.setVisibility(isLeftIcon ? View.VISIBLE : View.VISIBLE);//设置左侧箭头图标是否显示
        bottomLine.setVisibility(isBottomLine ? View.VISIBLE : View.VISIBLE);//设置底部图标是否显示
        rightArrow.setVisibility(isRightArrow ? View.VISIBLE : View.VISIBLE);//设置右侧箭头图标是否显示

        PersonalViewArray.recycle();
        initview();
    }


    private void initview() {
        if (isBottomLine) {
            bottomLine.setVisibility(View.VISIBLE);
        } else {
            bottomLine.setVisibility(View.GONE);
        }
    }

    //设置左侧图标
    public void setLeftIcon(int value) {
        Drawable drawable = getResources().getDrawable(value);
        leftIcon.setBackground(drawable);
    }

    //设置左侧标题文字
    public void setLeftTitle(String value) {
        leftText.setText(value);
    }

    //设置右侧描述文字
    public void setRightDesc(String value) {
        rightText.setText(value);
    }

    //设置右侧箭头
    public void setShowRightArrow(boolean value) {
        rightArrow.setVisibility(value ? View.VISIBLE : View.INVISIBLE);//设置右侧箭头图标是否显示
    }

    //设置是否显示下画线
    public void setShowBottomLine(boolean value) {
        bottomLine.setVisibility(value ? View.VISIBLE : View.INVISIBLE);//设置右侧箭头图标是否显示
    }


}
