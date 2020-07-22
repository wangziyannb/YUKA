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
    private TableLayout tableLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.log_forgetpassword, container, false);
        tableLayout = root.findViewById(R.id.change_password);
        GlobalHandler globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        root.findViewById(R.id.password_commit).setOnClickListener(this);
        return root;
    }


    @Override
    public void onClick(View v) {
        EditText username = tableLayout.findViewById(R.id.pwd_user_name);
        EditText password_enter = tableLayout.findViewById(R.id.password_enter);
        EditText password_confirm = tableLayout.findViewById(R.id.password_confirm);
        String[] params = new String[3];

        params[0] = username.getText() + "";
        params[1] = password_enter.getText() + "";
        params[2] = UserManager.getUser()[2];

        switch (v.getId()) {
            case R.id.password_commit:
                if (params[1].equals(password_confirm.getText() + "")) {
                    Toast.makeText(getContext(), "两次输入的密码一致", Toast.LENGTH_SHORT).show();
                    UserManager.forget_password(params);
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
        Bundle bundle;
        switch (msg.what) {
            case 200:
                LoadingViewManager.dismiss();
                bundle = msg.getData();
                Toast.makeText(getContext(), bundle.getString("results"), Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigateUp();
                break;
            case 601:
                LoadingViewManager.dismiss();
                bundle = msg.getData();
                Toast.makeText(getContext(), bundle.getString("results"), Toast.LENGTH_SHORT).show();
                break;
            case 400:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "网络似乎出现了点问题...\n请检查网络或于开发者选项者检查服务器", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
