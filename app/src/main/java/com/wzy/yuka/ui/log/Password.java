package com.wzy.yuka.ui.log;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.interaction.LoadingViewManager;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.yuka.user.UserManager;

public class Password extends Fragment implements View.OnClickListener, GlobalHandler.HandleMsgListener {
    private GlobalHandler globalHandler;
    private TableLayout tableLayout;
    private String[] params;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.log_forgetpassword, container, false);
        tableLayout = root.findViewById(R.id.change_password);
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        root.findViewById(R.id.password_commit).setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        EditText username = tableLayout.findViewById(R.id.pwd_user_name);
        EditText password_enter = tableLayout.findViewById(R.id.password_enter);
        EditText password_confirm = tableLayout.findViewById(R.id.password_confirm);

        params = new String[4];

        params[0] = username.getText() + "";
        params[1] = password_enter.getText() + "";
        params[2] = password_confirm.getText() + "";
        params[3] = UserManager.getUser()[2];

        switch (v.getId()) {
            case R.id.password_commit:
                if (params[1].equals(params[2])) {
                    Toast.makeText(getContext(), "两次输入的密码一致", Toast.LENGTH_SHORT).show();
                    UserManager.password(params);
                    LoadingViewManager
                            .with(getActivity())
                            .setHintText("重置密码中...")
                            .setAnimationStyle("BallScaleIndicator")
                            .setShowInnerRectangle(true)
                            .setOutsideAlpha(0.3f)
                            .setLoadingContentMargins(50, 50, 50, 50)
                            .build();
                } else {
                    Toast.makeText(getContext(), "两次输入的密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 202:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "修改密码成功", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigateUp();
                break;
            case 600:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "修改密码失败", Toast.LENGTH_SHORT).show();
                break;
            case 400:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "网络似乎出现了点问题...\n请检查网络或于开发者选项者检查服务器", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
