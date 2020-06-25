package com.wzy.yuka.ui.log;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.wzy.yuka.MainViewModel;
import com.wzy.yuka.R;
import com.wzy.yuka.core.user.UserManager;
import com.wzy.yuka.tools.interaction.LoadingViewManager;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;


public class Login extends Fragment implements View.OnClickListener, GlobalHandler.HandleMsgListener {
    private GlobalHandler globalHandler;

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 200:
                SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                sharedPreferencesUtil.saveParam(SharedPreferencesUtil.FIRST_LOGIN, true);
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
