package com.wzy.yuka.ui.setting;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.wzy.yuka.R;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class SettingsDetect extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_detect, rootKey);

        ListPreference model = getPreferenceScreen().findPreference("settings_detect_model");
        model.setValue(model.getValue() != null ? model.getValue() : model.getEntryValues()[0] + "");
        model.setSummary(model.getEntry() != null ? model.getEntry() : model.getEntries()[0]);
        model.setOnPreferenceChangeListener(this);
        ListPreference translator = getPreferenceScreen().findPreference("settings_trans_translator");
        translator.setValue(translator.getValue() != null ? translator.getValue() : translator.getEntryValues()[0] + "");
        translator.setSummary(translator.getEntry() != null ? translator.getEntry() : translator.getEntries()[0]);
        translator.setOnPreferenceChangeListener(this);
        preferenceVisibilityChange();
    }

    private void preferenceVisibilityChange() {
        SwitchPreference vertical = getPreferenceScreen().findPreference("settings_detect_vertical");
        SwitchPreference SBCS = getPreferenceScreen().findPreference("settings_baidu_SBCS");
        SwitchPreference punctuation = getPreferenceScreen().findPreference("settings_detect_punctuation");

        ListPreference model = getPreferenceScreen().findPreference("settings_detect_model");
        ListPreference translator = getPreferenceScreen().findPreference("settings_trans_translator");

        if (vertical != null && model != null && punctuation != null) {

            switch (model.getValue()) {
                case "youdao":
                    vertical.setVisible(false);
                    punctuation.setVisible(true);
                    break;
                case "google":
                    vertical.setVisible(true);
                    punctuation.setVisible(false);
                    break;
                case "baidu":
                    vertical.setVisible(true);
                    punctuation.setVisible(true);
                    break;
            }
        }
        if (SBCS != null && translator != null) {
            switch (translator.getValue()) {
                case "youdao":
                case "google":
                    SBCS.setVisible(false);
                    break;
                case "baidu":
                    SBCS.setVisible(true);
                    break;
            }
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

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case "settings_detect_model":
            case "settings_trans_translator":
                ((ListPreference) preference).setValue((String) newValue);
                preference.setSummary(((ListPreference) preference).getEntry());
                break;
        }
        preferenceVisibilityChange();
        return false;
    }
}
