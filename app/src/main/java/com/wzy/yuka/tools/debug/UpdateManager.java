package com.wzy.yuka.tools.debug;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.network.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Ziyan on 2020/7/22.
 */
public class UpdateManager {
    private final WeakReference<Context> contextWR;

    public UpdateManager(Context context) {
        contextWR = new WeakReference<>(context);
    }

    public void findUpdate() {
        getUpdateList();
    }

    private void getUpdateList() {
        GlobalHandler globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(msg -> {
            switch (msg.what) {
                case 400:
                    Toast.makeText(contextWR.get(), "网络请求失败，请检查服务器或网络！", Toast.LENGTH_SHORT).show();
                    break;
                case 200:
                    showUpdate(msg.getData().getString("response"));
                    break;
            }
        });
        HttpRequest.checkUpdate(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Message message = Message.obtain();
                message.what = 400;
                globalHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                Bundle bundle = new Bundle();
                bundle.putString("response", res);
                Message message = Message.obtain();
                message.what = 200;
                message.setData(bundle);
                globalHandler.sendMessage(message);
            }
        });
    }

    private void showUpdate(String res) {
        try {
            PackageManager packageManager = contextWR.get().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(contextWR.get().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            long versionCode = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                versionCode = packageInfo.versionCode;
            } else {
                //P以下不能直接用，会报错
                versionCode = packageInfo.getLongVersionCode();
            }
            Log.d("update", "showUpdate: " + res);
            JSONObject jsonObject = new JSONObject(res);
            String version_code = jsonObject.getString("version_code");
            if (Integer.parseInt(version_code) > versionCode) {
                String version_name = jsonObject.getString("version_name");

                String version_status = jsonObject.getString("version_status");
                String update_time = jsonObject.getString("update_time");
                String compatible_server_version = jsonObject.getString("compatible_server_version");
                String description = jsonObject.getString("description");
                String download_url = jsonObject.getString("download_url");
                final AlertDialog.Builder alert = new AlertDialog.Builder(contextWR.get());

                alert.setTitle("发现更新：Yuka V" + version_name + "-" + version_status);
                String message = "更新时间：" + update_time + "\n" + description + "\n" +
                        "支持的服务器版本：" + compatible_server_version;
                alert.setMessage(message);
                alert.setPositiveButton("去酷安看看", (dialog, which) -> {
                    if (!marketUpdate("com.coolapk.market")) {
                        Toast.makeText(contextWR.get(), "未检测到可用的市场，使用浏览器更新", Toast.LENGTH_SHORT).show();
                        if (!browserUpdate(download_url)) {
                            Toast.makeText(contextWR.get(), "未检测到可用的浏览器，先去下一个吧", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alert.setNeutralButton("备用更新地址", (dialog, which) -> {
                    if (!browserUpdate(download_url)) {
                        Toast.makeText(contextWR.get(), "未检测到可用的浏览器，先去下一个吧", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setNegativeButton("下次一定（", (dialog, which) -> {
                });
                alert.show();
            } else {
                Toast.makeText(contextWR.get(), "已经是最新版啦", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(contextWR.get(), "服务器正忙...请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean marketUpdate(String marketPkg) {
        Uri uri = Uri.parse("market://details?id=" + contextWR.get().getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (marketPkg != null) {
            // 如果没给市场的包名，则系统会弹出市场的列表让你进行选择。
            intent.setPackage(marketPkg);
        }
        try {
            contextWR.get().startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean browserUpdate(String download_url) {
        Uri uri = Uri.parse(download_url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            contextWR.get().startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
