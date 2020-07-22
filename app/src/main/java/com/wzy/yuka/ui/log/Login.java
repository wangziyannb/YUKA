package com.wzy.yuka.ui.log;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.wzy.yuka.MainViewModel;
import com.wzy.yuka.R;
import com.wzy.yuka.tools.interaction.LoadingViewManager;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.tools.params.SizeUtil;
import com.wzy.yuka.yuka.user.UserManager;


public class Login extends Fragment implements View.OnClickListener, GlobalHandler.HandleMsgListener {
    private GlobalHandler globalHandler;
    private CheckBox checkBox;

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 200:
                SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                sharedPreferencesUtil.saveParam(SharedPreferenceCollection.FIRST_LOGIN, true);
                NavHostFragment.findNavController(this).navigateUp();
                break;
            case 601:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
                break;
            case 400:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "网络似乎出现了点问题...\n请检查网络或于开发者选项者检查服务器", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.log_login, container, false);
        TextView un_t = root.findViewById(R.id.user_name);
        TextView pwd_t = root.findViewById(R.id.password);
        String[] params = UserManager.getUser();
        un_t.setText(params[0]);
        pwd_t.setText(params[1]);

        final MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        final MutableLiveData<String> user_n = (MutableLiveData<String>) mainViewModel.getuser_n();
        final MutableLiveData<String> pwd = (MutableLiveData<String>) mainViewModel.getpwd();

        user_n.observe(getViewLifecycleOwner(), un_t::setText);
        pwd.observe(getViewLifecycleOwner(), pwd_t::setText);
        root.findViewById(R.id.login).setOnClickListener(this);

        root.findViewById(R.id.register).setOnClickListener(this);
        View view = root.findViewById(R.id.login_checkboxlayout);

        root.findViewById(R.id.forget_password).setOnClickListener(this);

        view.findViewById(R.id.textView18).setOnClickListener(this);
        checkBox = view.findViewById(R.id.checkBox);

        TextView textView1 = view.findViewById(R.id.textViewx1);

        textView1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        textView1.setOnClickListener(this);
        TextView textView2 = view.findViewById(R.id.textViewx2);
        textView2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        textView2.setOnClickListener(this);
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        View view = (View) v.getParent();
        switch (v.getId()) {
            case R.id.register:
                Navigation.findNavController(getView()).navigate(R.id.action_nav_login_to_nav_register);
                break;
            case R.id.login:
                if (checkBox.isChecked()) {
                    TextView username = view.findViewById(R.id.user_name);
                    TextView password = view.findViewById(R.id.password);
                    UserManager.addUser(username.getText() + "", password.getText() + "", UserManager.getUser()[3]);
                    LoadingViewManager
                            .with(getActivity())
                            .setHintText("登录中...")
                            .setAnimationStyle("BallScaleIndicator")
                            .setShowInnerRectangle(true)
                            .setOutsideAlpha(0.3f)
                            .setLoadingContentMargins(50, 50, 50, 50)
                            .build();
                    UserManager.login();
                } else {
                    Toast.makeText(getContext(), "请勾选同意隐私协议与用户协议后再登陆", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.textView18:
                checkBox.performClick();
                break;
            case R.id.textViewx1:
                showDialog(0);
                break;
            case R.id.textViewx2:
                showDialog(1);
                break;
            case R.id.forget_password:
                Navigation.findNavController(getView()).navigate(R.id.action_nav_login_to_nav_forgetpassword);
                break;
        }
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
        dialog.getWindow().setLayout((GetParams.Screen()[0]), SizeUtil.dp2px(getContext(), 600));
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
