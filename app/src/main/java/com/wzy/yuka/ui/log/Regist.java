package com.wzy.yuka.ui.log;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.wzy.yuka.MainViewModel;
import com.wzy.yuka.R;
import com.wzy.yuka.core.user.UserManager;
import com.wzy.yuka.tools.handler.GlobalHandler;
import com.wzy.yuka.tools.interaction.LoadingViewManager;


public class Regist extends Fragment implements View.OnClickListener, GlobalHandler.HandleMsgListener {
    private GlobalHandler globalHandler;
    private TableLayout tableLayout;
    private String[] params;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.register, container, false);
        tableLayout = root.findViewById(R.id.tableLayout);
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        root.findViewById(R.id.register_button).setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        EditText un_t = tableLayout.findViewById(R.id.username_regist);
        EditText pwd_t = tableLayout.findViewById(R.id.password_regist);
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
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 202:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "注册成功", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigateUp();
            case 600:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "注册失败，用户名或账号与他人相同", Toast.LENGTH_SHORT).show();
            case 400:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "网络似乎出现了点问题...\n请检查网络或于开发者选项者检查服务器", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
