package com.wzy.yuka.ui.setting;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.ui.view.SyncCompatibleApp;
import com.wzy.yuka.ui.view.SyncCompatibleAppsAdapter;
import com.wzy.yuka.yuka_lite.utils.SizeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class SettingsSync extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    //api设置
    ListPreference sender_api;
    //yuka的识别器设置
    ListPreference model;
    //other的识别器设置
    ListPreference model_other;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_sync, rootKey);
        getPreferenceScreen().findPreference(SharedPreferenceCollection.sync_findCompatible).setOnPreferenceClickListener(preference -> {
            showCompatibleApps();
            //showDialog();
            return false;
        });
        sender_api = getPreferenceScreen().findPreference(SharedPreferenceCollection.sync_api);
        model = getPreferenceScreen().findPreference(SharedPreferenceCollection.sync_provider);
        model_other = getPreferenceScreen().findPreference(SharedPreferenceCollection.sync_other_provider);
        for (ListPreference l : new ListPreference[]{sender_api, model, model_other}) {
            l.setValue(l.getValue() != null ? l.getValue() : l.getEntryValues()[0] + "");
            l.setSummary(l.getEntry() != null ? l.getEntry() : l.getEntries()[0]);
            l.setOnPreferenceChangeListener(this);
        }
        preferenceVisibilityChange();
    }


    private void preferenceVisibilityChange() {
        PreferenceScreen screen = getPreferenceScreen();
        PreferenceCategory category_model = screen.findPreference(SharedPreferenceCollection.sync);
        PreferenceCategory category_model_other = screen.findPreference(SharedPreferenceCollection.sync_other);
        if (category_model != null && category_model_other != null) {
            if (sender_api.getValue().equals("other")) {
                //自定义
                category_model.setVisible(false);
                category_model_other.setVisible(true);
            } else {
                //yuka_v1
                category_model.setVisible(true);
                category_model_other.setVisible(false);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ((ListPreference) preference).setValue((String) newValue);
        preference.setSummary(((ListPreference) preference).getEntry());
        preferenceVisibilityChange();
        return false;
    }

    private void showCompatibleApps() {
        List<SyncCompatibleApp> syncCompatibleApps = new ArrayList<>();
        SyncCompatibleAppsAdapter appsAdapter = new SyncCompatibleAppsAdapter(syncCompatibleApps);
        GlobalHandler globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(msg -> {
            if (msg.what == 127001) {
                appsAdapter.notifyDataSetChanged();
            }
        });
        new Thread(() -> {
            PackageManager packageManager = getActivity().getPackageManager();
            List<PackageInfo> packages = packageManager
                    .getInstalledPackages(PackageManager.GET_ACTIVITIES);
            try {
                for (PackageInfo info : packages) {
                    if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        int targetSdk = info.applicationInfo.targetSdkVersion;
                        if (targetSdk >= 29) {
                            Drawable drawable = info.applicationInfo.loadIcon(packageManager);
                            String name = info.applicationInfo.loadLabel(packageManager).toString();
                            SyncCompatibleApp syncCompatibleApp = new SyncCompatibleApp(drawable, name);
                            syncCompatibleApps.add(syncCompatibleApp);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Message msg = Message.obtain();
            msg.what = 127001;
            globalHandler.sendMessage(msg);
        }).start();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.sync_compatible_apps, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).setCancelable(true).create();

        RecyclerView recyclerView = view.findViewById(R.id.sync_c_a_apps);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(appsAdapter);

        dialog.show();
        dialog.getWindow().setLayout((SizeUtil.Screen(getContext())[0]), SizeUtil.dp2px(getContext(), 600));
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
