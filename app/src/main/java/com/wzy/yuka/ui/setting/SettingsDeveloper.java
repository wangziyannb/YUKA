package com.wzy.yuka.ui.setting;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.network.HttpRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettingsDeveloper extends PreferenceFragmentCompat implements GlobalHandler.HandleMsgListener {
    private GlobalHandler globalHandler;

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 900:
                Bundle bundle = msg.getData();
                String error = bundle.getString("error");
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            case 901:
                Bundle bundle1 = msg.getData();
                String response = bundle1.getString("response");
                try {
                    JSONObject resultJson = new JSONObject(response);
                    String version = resultJson.getString("origin");
                    String update = resultJson.getString("results");
                    Toast.makeText(getContext(), version + "\n" + update, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_developer, rootKey);
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        getPreferenceScreen().findPreference("settings_debug_server").setOnPreferenceClickListener(preference -> {
            HttpRequest.yuka(
                    new String[]{getContext().getResources().getStringArray(R.array.mode)[2]},
                    "",
                    new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("settingsFragment", "服务器检查失败");
                            Bundle bundle = new Bundle();
                            bundle.putString("error", e.toString());
                            Message message = Message.obtain();
                            message.what = 900;
                            message.setData(bundle);
                            globalHandler.sendMessage(message);
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Bundle bundle = new Bundle();
                            bundle.putString("response", response.body().string());
                            Message message = Message.obtain();
                            message.what = 901;
                            message.setData(bundle);
                            globalHandler.sendMessage(message);
                        }
                    });
            return false;
        });
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
