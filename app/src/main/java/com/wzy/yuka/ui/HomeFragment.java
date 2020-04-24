package com.wzy.yuka.ui;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.floatwindow.FloatWindow;

public class HomeFragment extends Fragment implements View.OnClickListener {
    static final String TAG = "HomeFragment";
    public static final int REQUEST_MEDIA_PROJECTION = 0x2893;
    public static Intent data;
    private MediaProjectionManager mMediaProjectionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home, container, false);
        root.findViewById(R.id.startBtn).setOnClickListener(this);
        root.findViewById(R.id.closeBtn).setOnClickListener(this);
        root.findViewById(R.id.exitBtn).setOnClickListener(this);
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
                    FloatWindow.initFloatWindow(getActivity());
                }
                break;
            case R.id.closeBtn:
                FloatWindow.dismissAllFloatWindow(false);
                break;
            case R.id.exitBtn:
                getActivity().finishAffinity();
                android.os.Process.killProcess(android.os.Process.myPid());
            default:
                break;
        }
    }

    //unknown wrong
    @SuppressLint("WrongConstant")
    public void requestScreenShot() {
        Log.d(TAG, "requestScreenShot");
        mMediaProjectionManager = (MediaProjectionManager) getActivity().getSystemService("media_projection");
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
            HomeFragment.data = data;
            FloatWindow.initFloatWindow(getActivity());
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
