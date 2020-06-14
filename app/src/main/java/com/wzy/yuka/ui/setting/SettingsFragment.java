package com.wzy.yuka.ui.setting;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.wzy.yuka.R;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        getPreferenceScreen().findPreference("settings_detect").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("settings_auto").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("settings_sync").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("settings_appearance").setOnPreferenceClickListener(this);
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
            case "settings_detect":
                Navigation.findNavController(getView()).navigate(R.id.action_nav_settings_to_nav_settings_detect);
                break;
            case "settings_developer":
                Navigation.findNavController(getView()).navigate(R.id.action_nav_settings_to_nav_settings_developer);
                break;
            case "settings_auto":
                Navigation.findNavController(getView()).navigate(R.id.action_nav_settings_to_nav_settings_auto);
                break;
            case "settings_sync":
                Navigation.findNavController(getView()).navigate(R.id.action_nav_settings_to_nav_settings_sync);
                break;
            case "settings_appearance":
                Navigation.findNavController(getView()).navigate(R.id.action_nav_settings_to_nav_settings_appearance);
                break;
        }

        return false;
    }
}
