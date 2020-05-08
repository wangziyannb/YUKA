package com.wzy.yuka.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.wzy.yuka.R;
import com.wzy.yuka.core.floatwindow.FloatWindowManager;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_MEDIA_PROJECTION = 0x2893;
    private final String TAG = "HomeFragment";
    private Intent data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home, container, false);
        root.findViewById(R.id.startBtn).setOnClickListener(this);
        root.findViewById(R.id.closeBtn).setOnClickListener(this);
        root.findViewById(R.id.exitBtn).setOnClickListener(this);
        root.findViewById(R.id.imageButton).setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startBtn:
                if (data == null) {
                    requestScreenShot();
                }
                if (data != null) {
                    FloatWindowManager.initFloatWindow(getActivity(), data);
                }
                break;
            case R.id.closeBtn:
                FloatWindowManager.dismissAllFloatWindow(false);
                break;
            case R.id.exitBtn:
                getActivity().finishAffinity();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case R.id.imageButton:
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isLogin", false);
                editor.commit();
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

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.scene_open_enter);
        } else {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.scene_close_exit);
        }
    }
}
