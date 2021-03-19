package com.wzy.yuka.ui.setting;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class SettingsAuto extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

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
        setPreferencesFromResource(R.xml.settings_auto, rootKey);

        sender_api = getPreferenceScreen().findPreference(SharedPreferenceCollection.auto_api);
        model = getPreferenceScreen().findPreference(SharedPreferenceCollection.auto_model);
        translator = getPreferenceScreen().findPreference(SharedPreferenceCollection.trans_translator);
        model_other = getPreferenceScreen().findPreference(SharedPreferenceCollection.auto_other_model);
        translator_other = getPreferenceScreen().findPreference(SharedPreferenceCollection.trans_other_translator);

        for (ListPreference l : new ListPreference[]{sender_api, model, translator, model_other, translator_other}) {
            l.setValue(l.getValue() != null ? l.getValue() : l.getEntryValues()[0] + "");
            l.setSummary(l.getEntry() != null ? l.getEntry() : l.getEntries()[0]);
            l.setOnPreferenceChangeListener(this);
        }
        preferenceVisibilityChange();
    }


    private void preferenceVisibilityChange() {
        PreferenceScreen screen = getPreferenceScreen();
        PreferenceCategory category_model = screen.findPreference(SharedPreferenceCollection.auto);
        PreferenceCategory category_translator = screen.findPreference(SharedPreferenceCollection.translator);
        PreferenceCategory category_model_other = screen.findPreference(SharedPreferenceCollection.auto_other);
        PreferenceCategory category_translator_other = screen.findPreference(SharedPreferenceCollection.translator_other);

        if (category_model != null && category_translator != null && category_model_other != null && category_translator_other != null) {
            if (sender_api.getValue().equals("other")) {
                category_model.setVisible(false);
                category_translator.setVisible(false);
                category_model_other.setVisible(true);
                category_translator_other.setVisible(true);
                EditTextPreference youdao_key = screen.findPreference(SharedPreferenceCollection.auto_other_youdao_key);
                EditTextPreference youdao_sec = screen.findPreference(SharedPreferenceCollection.auto_other_youdao_appsec);
                EditTextPreference baidu_key = screen.findPreference(SharedPreferenceCollection.auto_other_baidu_key);
                EditTextPreference baidu_sec = screen.findPreference(SharedPreferenceCollection.auto_other_baidu_appsec);
                SwitchPreference vertical = screen.findPreference(SharedPreferenceCollection.auto_other_vertical);
                if (youdao_key != null && youdao_sec != null && baidu_key != null && baidu_sec != null && vertical != null) {
                    switch (model_other.getValue()) {
                        case "youdao":
                            youdao_key.setVisible(true);
                            youdao_sec.setVisible(true);
                            baidu_key.setVisible(false);
                            baidu_sec.setVisible(false);
                            vertical.setVisible(false);
                            break;
                        case "baidu":
                            youdao_key.setVisible(false);
                            youdao_sec.setVisible(false);
                            baidu_key.setVisible(true);
                            baidu_sec.setVisible(true);
                            vertical.setVisible(true);
                            break;
                    }
                }
            } else {
                category_model.setVisible(true);
                category_translator.setVisible(true);
                category_model_other.setVisible(false);
                category_translator_other.setVisible(false);
                SwitchPreference vertical = screen.findPreference(SharedPreferenceCollection.auto_vertical);
                SwitchPreference punctuation = screen.findPreference(SharedPreferenceCollection.auto_punctuation);
                SwitchPreference SBCS = screen.findPreference(SharedPreferenceCollection.trans_baidu_SBCS);
                if (vertical != null && punctuation != null) {
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
