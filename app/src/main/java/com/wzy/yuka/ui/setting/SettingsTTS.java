package com.wzy.yuka.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;


public class SettingsTTS extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_tts, rootKey);
        Preference settings = getPreferenceScreen().findPreference(SharedPreferenceCollection.tts_goto);
        if (settings != null) {
            settings.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Context context = getContext();
                if (context != null) {
                    startActivity(intent);
                }
                return true;
            });
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
