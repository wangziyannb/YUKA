package com.wzy.yuka.ui.home;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wzy.yuka.R;
import com.wzy.yuka.core.floatwindow.FloatWindowManager;
import com.wzy.yuka.core.user.UserManager;
import com.wzy.yuka.tools.message.BaseFragment;

public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private static final int REQUEST_MEDIA_PROJECTION = 0x2893;
    private final String TAG = "HomeFragment";
    private Intent data;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    static int flag_home;
    static int flag_history;
    static int flag_start;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home, container, false);

        bottomNavigationView = root.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.findViewById(R.id.bot_nav_start).setOnClickListener(this);
        bottomNavigationView.findViewById(R.id.bot_nav_home).setOnClickListener(this);
        bottomNavigationView.findViewById(R.id.bot_nav_history).setOnClickListener(this);
        navController = Navigation.findNavController(root.findViewById(R.id.fragment));
        //  bottomNavigationView.setItemIconTintList(null);

        return root;
    }


    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        refreshItemIcon();
        switch (v.getId()) {
            case R.id.bot_nav_start:
                if (UserManager.checkLogin()) {
                    if (data == null) {
                        requestScreenShot();
                    }
                    if (data != null) {
                        if (FloatWindowManager.floatBall == null) {
                            FloatWindowManager.initFloatWindow(getActivity(), data);
                            bottomNavigationView.findViewById(R.id.bot_nav_start).setBackgroundResource(R.drawable.nav_start_checked);
                        } else {
                            FloatWindowManager.dismissAllFloatWindow(false);
                            bottomNavigationView.findViewById(R.id.bot_nav_start).setBackgroundResource(R.drawable.nav_start_unchecked);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "没登录呢", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bot_nav_home:
                if (bottomNavigationView.getSelectedItemId() != R.id.bot_nav_home) {
                    navController.navigate(R.id.action_home_boutique_to_home_main);
                    bottomNavigationView.setSelectedItemId(R.id.bot_nav_home);
                    if (flag_home == 0) {
                        bottomNavigationView.getMenu().getItem(0).setIcon(R.drawable.nav_home_checked);
                        flag_home = 1;
                    } else if (flag_home == 1) {
                        bottomNavigationView.getMenu().getItem(0).setIcon(R.drawable.nav_home_unchecked);
                        flag_home = 0;
                    }
                }
                break;
            case R.id.bot_nav_history:
                if (bottomNavigationView.getSelectedItemId() != R.id.bot_nav_history) {
                    navController.navigate(R.id.action_home_main_to_home_boutique);
                    bottomNavigationView.setSelectedItemId(R.id.bot_nav_history);
                    if (flag_history == 0) {
                        bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.nav_history_checked);
                        flag_history = 1;
                    } else if (flag_history == 1) {
                        bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.nav_history_unchecked);
                        flag_history = 0;
                    }


                }
                break;
        }
    }


    //unknown wrong
    @SuppressLint("WrongConstant")
    private void requestScreenShot() {
        Log.d(TAG, "requestScreenShot");
        MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager) getActivity().getSystemService("media_projection");
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION);
    }

    /**
     * 未选中时加载默认的图片
     */
    public void refreshItemIcon() {
        bottomNavigationView.getMenu().getItem(0).setIcon(R.drawable.nav_home_unchecked);
        flag_home = 0;
        bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.nav_history_unchecked);
        flag_history = 0;
        bottomNavigationView.findViewById(R.id.bot_nav_start).setBackgroundResource(R.drawable.nav_start_unchecked);
        flag_start = 0;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.e(TAG, "User cancel");
        } else {
            try {
                WindowManager mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics metrics = new DisplayMetrics();
                mWindowManager.getDefaultDisplay().getMetrics(metrics);
            } catch (Exception e) {
                Log.e(TAG, "MediaProjection error");
                return;
            }
            this.data = data;
            FloatWindowManager.initFloatWindow(getActivity(), data);
        }
    }

    private long exitTime;

    @Override
    public boolean onBackPressed() {
        //当onBackPressed返回true时，证明子fragment有人响应事件
        if (!super.onBackPressed()) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(getContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                exitTime = System.currentTimeMillis();
                return true;
            } else {
                //小于2000ms则认为是用户确实希望退出程序
                return false;
            }
        } else {
            //此时return true和return super.onBackPressed()一样
            return true;
        }
    }

}
