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
public class SettingsDetect extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_detect, rootKey);
        ListPreference listPreference = getPreferenceScreen().findPreference("settings_detect_model");
        listPreference.setSummary(listPreference.getEntry());
        listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            listPreference.setValue((String) newValue);
            preference.setSummary(listPreference.getEntry());
            return false;
        });
        ListPreference listPreference2 = getPreferenceScreen().findPreference("settings_trans_translator");
        listPreference2.setSummary(listPreference2.getEntry());
        listPreference2.setOnPreferenceChangeListener((preference, newValue) -> {
            listPreference2.setValue((String) newValue);
            preference.setSummary(listPreference2.getEntry());
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
