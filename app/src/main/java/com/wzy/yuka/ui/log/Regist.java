package com.wzy.yuka.ui.log;

import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.wzy.yuka.MainViewModel;
import com.wzy.yuka.R;
import com.wzy.yuka.tools.interaction.LoadingViewManager;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.network.HttpRequest;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SizeUtil;
import com.wzy.yuka.yuka.user.UserManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;


public class Regist extends Fragment implements View.OnClickListener, GlobalHandler.HandleMsgListener, TextWatcher {
    private GlobalHandler globalHandler;
    private TableLayout tableLayout;
    private EditText un_t;
    private String[] params;
    private Runnable r = () -> HttpRequest.check_username(un_t.getText(), new okhttp3.Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            Message message = Message.obtain();
            message.what = 400;
            globalHandler.sendMessage(message);
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String res = response.body().string();
            Message message = Message.obtain();
            try {
                JSONObject resultJson = new JSONObject(res);
                String origin = resultJson.getString("origin");
                if (origin.equals("200")) {
                    message.what = 200;
                    Log.e("tag", "onResponse: " + res);
                    globalHandler.sendMessage(message);
                }
                if (origin.equals("605")) {
                    message.what = 605;
                    Log.e("tag", "onResponse: " + res);
                    globalHandler.sendMessage(message);
                }
                if (origin.equals("400")) {
                    onFailure(call, new IOException());
                }
            } catch (JSONException e) {
                Log.e("tag", "onResponse: " + res);
                e.printStackTrace();
                onFailure(call, new IOException());
            }
        }
    });

    private void showDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.policy, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).setCancelable(false).create();
        TextView title = view.findViewById(R.id.policy_appbar).findViewById(R.id.policy_textview1);
        title.setText("Yuka用户协议");
        Button ok = view.findViewById(R.id.policy_ok);
        Button cancel = view.findViewById(R.id.policy_cancel);
        ok.setOnClickListener(v -> {
            dialog.dismiss();
            alert();
        });
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
            NavHostFragment.findNavController(this).navigateUp();
        });
        dialog.show();
        dialog.getWindow().setLayout((GetParams.Screen()[0]), SizeUtil.dp2px(getContext(), 600));
        WebView webView = view.findViewById(R.id.policy_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("https://yukacn.xyz/%E7%94%A8%E6%88%B7%E5%8D%8F%E8%AE%AE.html");
    }

    private void alert() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("注册说明");
        alert.setMessage("感谢使用Yuka注册服务！以下为注册说明：\n" +
                "1、每台设备只能注册一次！\n" +
                "2、本注册界面只有账号密码会提交至服务器！\n" +
                "3、注册账号后将会附送普通模式和自动模式各20次使用次数，后续使用请参考账号信息进行购买激活");
        alert.setPositiveButton("知道惹，我要注册！", (dialog, which) -> {

        });
        alert.setNegativeButton("已有帐号，我要登陆！", (dialog, which) -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
        alert.show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.log_register, container, false);
        tableLayout = root.findViewById(R.id.tableLayout);
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        un_t = tableLayout.findViewById(R.id.username_regist);
        root.findViewById(R.id.register_button).setOnClickListener(this);
        showDialog();
        un_t.addTextChangedListener(this);
        return root;
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
        EditText pwd_t = tableLayout.findViewById(R.id.password_regist);
        if (un_t.getText().length() != un_t.getText().length() || !un_t.getText().toString().equals(un_t.getText() + "")) {
            Toast.makeText(getContext(), "用户名已被注册，请重新输入", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (v.getId()) {
            case R.id.register_button:
                params = new String[3];

                params[0] = un_t.getText() + "";
                params[1] = pwd_t.getText() + "";
                params[2] = UserManager.getUser()[2];

                UserManager.register(params);
                LoadingViewManager
                        .with(getActivity())
                        .setHintText("注册中...")
                        .setAnimationStyle("BallScaleIndicator")
                        .setShowInnerRectangle(true)
                        .setOutsideAlpha(0.3f)
                        .setLoadingContentMargins(50, 50, 50, 50)
                        .build();
                final MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
                final MutableLiveData<String> user_n = (MutableLiveData<String>) mainViewModel.getuser_n();
                final MutableLiveData<String> pwd = (MutableLiveData<String>) mainViewModel.getpwd();
                user_n.setValue(params[0]);
                pwd.setValue(params[1]);
                break;
        }
    }

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 200:
                Toast.makeText(getContext(), "用户名检查通过", Toast.LENGTH_SHORT).show();
                break;
            case 202:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "注册成功", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigateUp();
                break;
            case 600:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "注册失败，用户名与他人相同或本机已注册", Toast.LENGTH_SHORT).show();
                break;
            case 605:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "用户名与他人相同", Toast.LENGTH_SHORT).show();
                break;
            case 400:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "网络似乎出现了点问题...\n请检查网络或于开发者选项者检查服务器", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        globalHandler.removeCallbacks(r);
        globalHandler.postDelayed(r, 2000);
    }
}
