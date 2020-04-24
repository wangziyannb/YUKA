package com.wzy.yuka.ui.help;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.wzy.yuka.R;

public class HelpFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.help, rootKey);
        getPreferenceScreen().findPreference("help_instruction_QA").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("help_instruction_example_translate").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("help_instruction_advance").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("help_instruction_future").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "help_instruction_QA":
                Navigation.findNavController(getView()).navigate(R.id.action_nav_help_to_nav_help_QA);
                break;
            case "help_instruction_example_translate":
                Navigation.findNavController(getView()).navigate(R.id.action_nav_help_to_nav_help_Example);
                break;
            case "help_instruction_advance":
                Toast.makeText(getContext(), "我还没想好", Toast.LENGTH_SHORT).show();
//                Navigation.findNavController(getView()).navigate(R.id.action_nav_help_to_nav_help_Advance);
                break;
            case "help_instruction_future":
                Navigation.findNavController(getView()).navigate(R.id.action_nav_help_to_nav_help_Future);
                break;
            default:
                break;
        }
        return false;
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
