package com.wzy.yuka.ui.setting;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class SettingsDetect extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    //api设置
    ListPreference sender_api;
    //yuka的识别器设置
    ListPreference model;
    //yuka的翻译器设置
    ListPreference translator;
    //other的识别器设置
    ListPreference model_other;
    //other的翻译器设置
    ListPreference translator_other;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_detect, rootKey);
        sender_api = getPreferenceScreen().findPreference(SharedPreferenceCollection.detect_api);

        model = getPreferenceScreen().findPreference(SharedPreferenceCollection.detect_model);
        translator = getPreferenceScreen().findPreference(SharedPreferenceCollection.trans_translator);

        model_other = getPreferenceScreen().findPreference(SharedPreferenceCollection.detect_other_model);
        translator_other = getPreferenceScreen().findPreference(SharedPreferenceCollection.trans_other_translator);

        for (ListPreference l : new ListPreference[]{sender_api, model, translator, model_other, translator_other}) {
            l.setValue(l.getValue() != null ? l.getValue() : l.getEntryValues()[0] + "");
            l.setSummary(l.getEntry() != null ? l.getEntry() : l.getEntries()[0]);
            l.setOnPreferenceChangeListener(this);
        }

        preferenceVisibilityChange();
    }

    private void preferenceVisibilityChange() {
        PreferenceCategory category_model = getPreferenceScreen().findPreference(SharedPreferenceCollection.detect);
        PreferenceCategory category_translator = getPreferenceScreen().findPreference(SharedPreferenceCollection.translator);
        PreferenceCategory category_model_other = getPreferenceScreen().findPreference(SharedPreferenceCollection.detect_other);
        PreferenceCategory category_translator_other = getPreferenceScreen().findPreference(SharedPreferenceCollection.translator_other);

        SwitchPreference vertical = getPreferenceScreen().findPreference(SharedPreferenceCollection.detect_vertical);
        SwitchPreference punctuation = getPreferenceScreen().findPreference(SharedPreferenceCollection.detect_punctuation);

        SwitchPreference SBCS = getPreferenceScreen().findPreference(SharedPreferenceCollection.trans_baidu_SBCS);
        if (category_model != null && category_translator != null && category_model_other != null && category_translator_other != null) {
            if (sender_api.getValue().equals("other")) {
                category_model.setVisible(false);
                category_translator.setVisible(false);
                category_model_other.setVisible(true);
                category_translator_other.setVisible(true);

            } else {
                category_model.setVisible(true);
                category_translator.setVisible(true);
                category_model_other.setVisible(false);
                category_translator_other.setVisible(false);
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
                    if (SBCS != null && translator != null) {

                        switch (translator.getValue()) {
                            case "youdao":
                            case "google":
                            case "tencent":
                                SBCS.setVisible(false);
                                break;
                            case "baidu":
                                SBCS.setVisible(true);
                                break;
                        }
                    }
                }
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
        ((ListPreference) preference).setValue((String) newValue);
        preference.setSummary(((ListPreference) preference).getEntry());
        preferenceVisibilityChange();
        return false;
    }
}
