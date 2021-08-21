package com.wzy.yuka;

import android.app.Application;
import android.view.accessibility.AccessibilityManager;

import com.lzf.easyfloat.EasyFloat;
import com.wzy.yuka.tools.debug.CrashManager;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka_lite.YukaFloatWindowManager;
import com.wzy.yukalite.YukaLite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyFloat.init(this, false);
        CrashManager crashHandler = new CrashManager(this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        YukaFloatWindowManager.init(this);
        YukaLite.init(this);
        SharedPreferencesUtil.init(this);
        check();
    }

    private void check() {
        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance();
        try {
            AccessibilityManager manager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
            assert manager != null;
            util.saveParam(SharedPreferenceCollection.application_touchExplorationEnabled, manager.isTouchExplorationEnabled());
        } catch (NullPointerException ignored) {
            util.saveParam(SharedPreferenceCollection.application_touchExplorationEnabled, false);
        }
        check_models("models/best/tessdata");
        check_models("models/fast/tessdata");
        this.getExternalFilesDir("logs");
    }

    private void check_models(String path) {
        //检查tess模型是否正常释放
        new Thread(() -> {
            try {
                String[] asset_models = getAssets().list("models");
                if (asset_models.length != 0) {
                    File tessdir = this.getExternalFilesDir(path);
                    if (tessdir.list() != null && tessdir.list().length == 0) {
                        String[] model_names = getAssets().list(path);
                        for (String model : model_names) {
                            InputStream in = getAssets().open(path + "/" + model);
                            File file = new File(tessdir.getAbsolutePath() + "/" + model);
                            if (file.createNewFile()) {
                                byte[] buffer = new byte[1024 * 8];
                                FileOutputStream out = new FileOutputStream(file);
                                int lengthRead;
                                while ((lengthRead = in.read(buffer)) > 0) {
                                    out.write(buffer, 0, lengthRead);
                                    out.flush();
                                }
                                out.close();
                            }
                            in.close();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }
}
