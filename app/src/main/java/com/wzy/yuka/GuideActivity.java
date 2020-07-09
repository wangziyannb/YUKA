package com.wzy.yuka;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.tools.params.SizeUtil;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener {
    private SharedPreferencesUtil sharedPreferencesUtil;
    private List<View> mViewList;
    private ImageView[] mDotList;
    private int mLastPosition;
    private int[] idots = {R.id.idot1, R.id.idot2, R.id.idot3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
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


    private void setCurrentDotPosition(int position) {
        for (int i = 0; i < idots.length; i++) {
            if (position == i) {
                mDotList[i].setImageResource(R.drawable.radio_dots_checked);
            } else {
                mDotList[i].setImageResource(R.drawable.radio_dots_unchecked);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void startHomeActivity() {
        Intent intent = new Intent(GuideActivity.this, MainActivity.class);
        startActivity(intent);
        sharedPreferencesUtil.saveParam(SharedPreferenceCollection.FIRST_GuideActivity, false);
        finish();
    }

    private void showDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.policy, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).setCancelable(false).create();
        Button ok = view.findViewById(R.id.policy_ok);
        Button cancel = view.findViewById(R.id.policy_cancel);
        ok.setOnClickListener(v -> {
            dialog.dismiss();
            startHomeActivity();
        });
        cancel.setOnClickListener(v -> {
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage("不同意本协议无法使用本软件。确定要退出吗？");
            alert.setPositiveButton("退出", (d, which) -> {
                dialog.dismiss();
                this.finishAffinity();
                this.finish();
                System.exit(0);
            });
            alert.setNegativeButton("重新阅读", (d, which) -> {
            });
            alert.show();
        });
        dialog.show();
        dialog.getWindow().setLayout((GetParams.Screen()[0]), SizeUtil.dp2px(this, 600));
        WebView webView = view.findViewById(R.id.policy_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("https://yukacn.xyz/%E9%9A%90%E7%A7%81%E5%8D%8F%E8%AE%AE.html");
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
                        button.setOnClickListener(v -> {
                            showDialog();
                        });
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
