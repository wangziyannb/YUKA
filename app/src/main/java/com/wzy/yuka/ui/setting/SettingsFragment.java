package com.wzy.yuka.ui.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.floatwindow.FloatWindowManager;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        if (FloatWindowManager.getNumOfFloatWindows() > 0) {
            Toast.makeText(getContext(), "没有关闭所有悬浮窗（包括启动按钮），设置暂时不可用", Toast.LENGTH_SHORT).show();
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
                getPreferenceScreen().getPreference(i).setEnabled(false);
            }
        }

        getPreferenceScreen().findPreference("settings_developer").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("settings_window_multiple").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.equals(true)) {
                    if (getPreferenceScreen().findPreference("settings_continuousMode").isEnabled()) {
                        if (getPreferenceScreen().findPreference("settings_continuousMode")
                                .getSharedPreferences().getBoolean("settings_continuousMode", false)) {
                            getPreferenceScreen().findPreference("settings_continuousMode").performClick();
                        }
                        getPreferenceScreen().findPreference("settings_continuousMode").setEnabled(false);
                    }
                } else {
                    getPreferenceScreen().findPreference("settings_continuousMode").setEnabled(true);
                }
                return true;
            }
        });

        //复数窗口启动时，持续模式禁用
        if (getPreferenceScreen().findPreference("settings_window_multiple")
                .getSharedPreferences().getBoolean("settings_window_multiple", false)) {
            if (getPreferenceScreen().findPreference("settings_continuousMode")
                    .getSharedPreferences().getBoolean("settings_continuousMode", false)) {
                getPreferenceScreen().findPreference("settings_continuousMode").performClick();
            }
            getPreferenceScreen().findPreference("settings_continuousMode").setEnabled(false);
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


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "settings_developer":
                Navigation.findNavController(getView()).navigate(R.id.action_nav_settings_to_nav_settings_developer);
                break;
        }

        return false;
    }
}
