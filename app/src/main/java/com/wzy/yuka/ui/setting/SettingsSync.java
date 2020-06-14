package com.wzy.yuka.ui.setting;

import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.wzy.yuka.R;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class SettingsSync extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_sync, rootKey);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            getPreferenceScreen().findPreference("settings_trans_syncMode").setEnabled(false);
            Toast.makeText(getContext(), "需要安卓10才可使用视频同传", Toast.LENGTH_SHORT).show();
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
