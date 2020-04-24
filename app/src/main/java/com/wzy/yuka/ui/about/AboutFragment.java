package com.wzy.yuka.ui.about;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.wzy.yuka.R;

public class AboutFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.about, rootKey);
        try {
            PackageManager packageManager = getActivity().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
            String version = packageInfo.versionName;
            getPreferenceScreen().findPreference("about_about_version").setSummary(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        getPreferenceScreen().findPreference("about_about_dev").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("about_about_donate").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("about_about_version").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("about_about_repository").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("about_thanks_open_source").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("about_thanks_reference").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "about_about_dev":
                Navigation.findNavController(getView()).navigate(R.id.action_nav_about_to_nav_about_dev);
                break;
            case "about_about_donate":

                break;
            case "about_about_version":
                // TODO: 2020/4/10  检查更新
                break;
            case "about_about_repository":
                Uri uri = Uri.parse("https://github.com/wangziyannb/Yuka");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case "about_thanks_open_source":
                Navigation.findNavController(getView()).navigate(R.id.action_nav_about_to_nav_about_opensource);
                break;
            case "about_thanks_reference":
                Navigation.findNavController(getView()).navigate(R.id.action_nav_about_to_nav_about_reference);
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