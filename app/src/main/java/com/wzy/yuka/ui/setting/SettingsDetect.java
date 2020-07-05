package com.wzy.yuka.ui.setting;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.wzy.yuka.R;
import com.wzy.yuka.yuka.FloatWindowManager;
import com.wzy.yuka.yuka.utils.FloatWindowManagerException;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class SettingsDetect extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_detect, rootKey);
        try {
            if (FloatWindowManager.getInstance().getNumOfFloatWindows() > 1) {
                Toast.makeText(getContext(), "有较多悬浮窗活动中，持续模式设置暂时不可更改", Toast.LENGTH_SHORT).show();
                getPreferenceScreen().findPreference("settings_continuousMode").setEnabled(false);
                getPreferenceScreen().findPreference("settings_continuousMode_interval").setEnabled(false);
            }
            getPreferenceScreen().findPreference("settings_continuousMode").setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    if (FloatWindowManager.getInstance().getNumOfFloatWindows() > 1) {
                        getPreferenceScreen().findPreference("settings_continuousMode").setEnabled(false);
                        return false;
                    } else {
                        return true;
                    }
                } catch (FloatWindowManagerException e) {
                    e.printStackTrace();
                }
                return false;
            });
        } catch (FloatWindowManagerException e) {
            e.printStackTrace();
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
