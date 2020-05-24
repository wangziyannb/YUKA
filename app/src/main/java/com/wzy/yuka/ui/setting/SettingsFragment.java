package com.wzy.yuka.ui.setting;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.wzy.yuka.R;
import com.wzy.yuka.core.floatwindow.FloatWindowManager;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        if (FloatWindowManager.getNumOfFloatWindows() > 1) {
            Toast.makeText(getContext(), "有较多悬浮窗活动中，持续模式设置暂时不可更改", Toast.LENGTH_SHORT).show();
            getPreferenceScreen().findPreference("settings_continuousMode").setEnabled(false);
            getPreferenceScreen().findPreference("settings_continuousMode_interval").setEnabled(false);
        }
        getPreferenceScreen().findPreference("settings_continuousMode").setOnPreferenceChangeListener((preference, newValue) -> {
            if (FloatWindowManager.getNumOfFloatWindows() > 1) {
                getPreferenceScreen().findPreference("settings_continuousMode").setEnabled(false);
                return false;
            } else {
                return true;
            }
        });
        getPreferenceScreen().findPreference("settings_developer").setOnPreferenceClickListener(this);
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
