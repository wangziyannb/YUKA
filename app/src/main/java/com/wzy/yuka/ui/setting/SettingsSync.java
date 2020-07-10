package com.wzy.yuka.ui.setting;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SizeUtil;
import com.wzy.yuka.ui.view.SyncCompatibleApp;
import com.wzy.yuka.ui.view.SyncCompatibleAppsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class SettingsSync extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_sync, rootKey);
        getPreferenceScreen().findPreference("settings_sync_findCompatible").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                test();
                //showDialog();
                return false;
            }
        });
    }

    private void test() {
        PackageManager packageManager = getActivity().getPackageManager();
        List<PackageInfo> packages = packageManager
                .getInstalledPackages(PackageManager.GET_ACTIVITIES);
        List<SyncCompatibleApp> syncCompatibleApps = new ArrayList<>();
        try {
            for (PackageInfo info : packages) {
                if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    int targetSdk = info.applicationInfo.targetSdkVersion;
                    if (targetSdk == 29) {
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

        View view = LayoutInflater.from(getContext()).inflate(R.layout.sync_compatible_apps, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).setCancelable(true).create();

        RecyclerView recyclerView = view.findViewById(R.id.sync_c_a_apps);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        SyncCompatibleAppsAdapter appsAdapter = new SyncCompatibleAppsAdapter(syncCompatibleApps);
        recyclerView.setAdapter(appsAdapter);

        dialog.show();
        dialog.getWindow().setLayout((GetParams.Screen()[0]), SizeUtil.dp2px(getContext(), 600));
    }

//    private void showDialog() {
//        View view = LayoutInflater.from(getContext()).inflate(R.layout.policy, null, false);
//        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).setCancelable(false).create();
//        TextView title = view.findViewById(R.id.policy_appbar).findViewById(R.id.policy_textview1);
//        title.setText("Yuka用户协议");
//        Button ok = view.findViewById(R.id.policy_ok);
//        Button cancel = view.findViewById(R.id.policy_cancel);
//        ok.setOnClickListener(v -> {
//            dialog.dismiss();
//            alert();
//        });
//        cancel.setOnClickListener(v -> {
//            dialog.dismiss();
//            NavHostFragment.findNavController(this).navigateUp();
//        });
//        dialog.show();
//        dialog.getWindow().setLayout((GetParams.Screen()[0]), SizeUtil.dp2px(getContext(), 600));
//    }

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
