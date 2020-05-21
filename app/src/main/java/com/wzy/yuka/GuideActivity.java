package com.wzy.yuka;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener {

    private List<View> mViewList;
    private ImageView[] mDotList;
    private int mLastPosition;
    private int[] idots = {R.id.idot1, R.id.idot2, R.id.idot3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        initView();
        initViewPager();
        initDots();
    }

    private void initDots() {
        LinearLayout dotsLayout = findViewById(R.id.point_layout);
        mDotList = new ImageView[mViewList.size()];
        for (int i = 0; i < mViewList.size(); i++) {
            mDotList[i] = (ImageView) dotsLayout.getChildAt(i);
            mDotList[i].setEnabled(false);
        }
        mLastPosition = 0;
        mDotList[0].setEnabled(true);
    }

    private void initViewPager() {
        ViewPager mViewPager = findViewById(R.id.viewpager);
        MyPagerAdapter adapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(this);
    }

    @SuppressLint("InflateParams")
    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        mViewList = new ArrayList<>();
        mViewList.add(inflater.inflate(R.layout.splashfirst, null));
        mViewList.add(inflater.inflate(R.layout.splashsecond, null));
        mViewList.add(inflater.inflate(R.layout.splashthird, null));

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentDotPosition(position);
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean("first_open", false);
            editor.commit();
        }
        super.onPause();

    }

    private void setCurrentDotPosition(int position) {
        for (int i = 0; i < idots.length; i++) {
            if (position == i) {
                mDotList[i].setImageResource(R.drawable.ic_radio_button_checked);
            } else {
                mDotList[i].setImageResource(R.drawable.ic_radio_button_unchecked);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void startHomeActivity() {
        Intent intent = new Intent(GuideActivity.this, MainActivity.class);
        startActivity(intent);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putBoolean("first_open", false);
        editor.commit();
        finish();
    }

    private class MyPagerAdapter extends PagerAdapter {

        private List<View> mImageViewList;

        MyPagerAdapter(List<View> list) {
            super();
            mImageViewList = list;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            if (mImageViewList != null) {
                if (mImageViewList.size() > 0) {
                    container.addView(mImageViewList.get(position));
                    if (position == mImageViewList.size() - 1) {
                        Button button = mImageViewList.get(position).findViewById(R.id.start_now);
                        button.setOnClickListener(v -> startHomeActivity());
                    }
                    return mImageViewList.get(position);
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            if (mImageViewList != null) {
                return mImageViewList.size();
            }
            return 0;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (mImageViewList != null) {
                if (mImageViewList.size() > 0) {
                    container.removeView(mImageViewList.get(position));
                }
            }
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}
