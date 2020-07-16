package com.wzy.yuka.ui.setting;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.wzy.yuka.R;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class SettingsAuto extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_auto, rootKey);
        ListPreference listPreference = getPreferenceScreen().findPreference("settings_auto_model");
        listPreference.setSummary(listPreference.getEntry());
        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            listPreference.setValue((String) newValue);
            preference.setSummary(listPreference.getEntry());
            return false;
        });
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
