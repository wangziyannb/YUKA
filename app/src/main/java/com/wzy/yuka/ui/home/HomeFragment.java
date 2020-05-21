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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home, container, false);
        bottomNavigationView = root.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.findViewById(R.id.nav_start).setOnClickListener(this);
        bottomNavigationView.findViewById(R.id.nav_home).setOnClickListener(this);
        bottomNavigationView.findViewById(R.id.nav_boutique).setOnClickListener(this);
        navController = Navigation.findNavController(root.findViewById(R.id.fragment));
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_start:
                if (UserManager.checkLogin()) {
                    if (data == null) {
                        requestScreenShot();
                    }
                    if (data != null) {
                        if (FloatWindowManager.floatBall == null) {
                            FloatWindowManager.initFloatWindow(getActivity(), data);
                        } else {
                            FloatWindowManager.dismissAllFloatWindow(false);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "没登录呢", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_home:

                if (bottomNavigationView.getSelectedItemId() != R.id.nav_home) {
                    navController.navigate(R.id.action_home_boutique_to_home_main);
                    bottomNavigationView.setSelectedItemId(R.id.nav_home);
                }
                break;
            case R.id.nav_boutique:
                if (bottomNavigationView.getSelectedItemId() != R.id.nav_boutique) {
                    navController.navigate(R.id.action_home_main_to_home_boutique);
                    bottomNavigationView.setSelectedItemId(R.id.nav_boutique);
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

    @TargetApi(Build.VERSION_CODES.O)
    @Override
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
