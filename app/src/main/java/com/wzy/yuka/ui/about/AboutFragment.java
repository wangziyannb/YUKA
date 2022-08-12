package com.wzy.yuka.ui.about;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.yuka_lite.utils.SizeUtil;
import com.wzy.yukalite.YukaLite;
import com.wzy.yukalite.YukaUserManagerException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AboutFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, GlobalHandler.HandleMsgListener {
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
        setPreferencesFromResource(R.xml.about, rootKey);
        globalHandler = GlobalHandler.getInstance();

        try {
            PackageManager packageManager = getActivity().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
            String version = packageInfo.versionName;
            getPreferenceScreen().findPreference("about_about_version").setSummary(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        getPreferenceScreen().findPreference("about_about_policy0").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("about_about_policy1").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("about_about_dev").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("about_about_version").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("about_about_repository").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("about_thanks_open_source").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("about_thanks_reference").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("about_about_server").setOnPreferenceClickListener(this);
        getPreferenceScreen().findPreference("about_about_dev").setOnPreferenceClickListener(this);
        String uuid = "UUID获取失败，未知错误";
        try {
            uuid = YukaLite.getId();
        } catch (YukaUserManagerException e) {
            e.printStackTrace();
        }
        Preference preference = getPreferenceScreen().findPreference("about_this_1");
        preference.setSummary(uuid + "\n\n" + preference.getSummary());
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "about_about_policy0":
                showDialog(0);
                break;
            case "about_about_policy1":
                showDialog(1);
                break;
            case "about_about_dev":
                Navigation.findNavController(getView()).navigate(R.id.action_nav_about_to_nav_about_dev);
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
            case "about_about_server":
                globalHandler.setHandleMsgListener(this);
                YukaLite.yuka(new Callback() {
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
            default:
                break;
        }
        return false;
    }

    private void showDialog(int mode) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.policy, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).setCancelable(true).create();
        Button ok = view.findViewById(R.id.policy_ok);
        ok.setVisibility(View.GONE);
        Button cancel = view.findViewById(R.id.policy_cancel);

        cancel.setVisibility(View.GONE);
        TextView title = view.findViewById(R.id.policy_appbar).findViewById(R.id.policy_textview1);

        dialog.show();
        dialog.getWindow().setLayout((SizeUtil.Screen(getContext())[0]), SizeUtil.dp2px(getContext(), 600));
        WebView webView = view.findViewById(R.id.policy_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        switch (mode) {
            case 0:
                title.setText("Yuka隐私协议");
                webView.loadUrl("https://yukacn.xyz/%E9%9A%90%E7%A7%81%E5%8D%8F%E8%AE%AE.html");
                break;
            case 1:
                title.setText("Yuka用户协议");
                webView.loadUrl("https://yukacn.xyz/%E7%94%A8%E6%88%B7%E5%8D%8F%E8%AE%AE.html");
                break;
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