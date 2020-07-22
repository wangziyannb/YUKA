package com.wzy.yuka.ui.about;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.network.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AboutDev extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View about = inflater.inflate(R.layout.about_dev, container, false);

        about.findViewById(R.id.personal_function).setOnClickListener(this);
        about.findViewById(R.id.personal_web).setOnClickListener(this);
        about.findViewById(R.id.personal_update).setOnClickListener(this);

        return about;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_function:
                Navigation.findNavController(getView()).navigate(R.id.action_nav_about_dev_to_nav_about_dev_function);
                break;
            case R.id.personal_web:
                Uri uri = Uri.parse("https://yukacn.xyz/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.personal_update:
                checkUpdate();
                break;
            default:
                break;
        }
    }

    private void checkUpdate() {
        GlobalHandler globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(msg -> {
            switch (msg.what) {
                case 400:
                    Toast.makeText(getContext(), "网络请求失败，请检查服务器或网络！", Toast.LENGTH_SHORT).show();
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
            PackageManager packageManager = getActivity().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
            String versionName = packageInfo.versionName;
            long versionCode = 0;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                versionCode = packageInfo.versionCode;
            } else {
                //P以下不能直接用，会报错
                versionCode = packageInfo.getLongVersionCode();
            }
            JSONObject jsonObject = new JSONObject(res);
            String version_name = jsonObject.getString("version_name");
            String version_code = jsonObject.getString("version_code");
            String version_status = jsonObject.getString("version_status");
            String update_time = jsonObject.getString("update_time");
            String compatible_server_version = jsonObject.getString("compatible_server_version");
            String description = jsonObject.getString("description");
            String download_url = jsonObject.getString("download_url");

            if (Integer.parseInt(version_code) > versionCode) {

                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setTitle("发现更新：Yuka V" + version_name + "-" + version_status);
                String message = "更新时间：" + update_time + "\n" + description + "\n" +
                        "支持的服务器版本：" + compatible_server_version;
                alert.setMessage(message);
                alert.setPositiveButton("更新", (dialog, which) -> {
                    Uri uri = Uri.parse(download_url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                });
                alert.setNegativeButton("下次一定（", (dialog, which) -> {
                });
                alert.show();
            } else {
                Toast.makeText(getContext(), "已经是最新版啦", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "服务器正忙...请稍后重试", Toast.LENGTH_SHORT).show();
        }


    }

}
