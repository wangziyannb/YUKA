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
        if (FloatWindowManager.floatBall != null) {
            Toast.makeText(getContext(), "没有关闭所有悬浮窗(包括悬浮球)，持续模式设置暂时不可更改", Toast.LENGTH_SHORT).show();
//            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
//                getPreferenceScreen().getPreference(i).setEnabled(false);
//            }
            getPreferenceScreen().findPreference("settings_continuousMode").setEnabled(false);
            getPreferenceScreen().findPreference("settings_continuousMode_interval").setEnabled(false);
        }

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
