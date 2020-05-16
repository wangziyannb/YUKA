package com.wzy.yuka.ui.user;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.wzy.yuka.R;
import com.wzy.yuka.core.user.UserManager;
import com.wzy.yuka.tools.handler.GlobalHandler;
import com.wzy.yuka.tools.interaction.LoadingViewManager;

/**
 * Created by Ziyan on 2020/5/15.
 */
public class PersonalInfo extends Fragment implements View.OnClickListener, GlobalHandler.HandleMsgListener {
    private EditText cdkey_et;
    private RelativeLayout member_card;
    private GlobalHandler globalHandler;

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 200:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "成功激活", Toast.LENGTH_SHORT).show();
                UserManager.refreshInfo();
                break;
            case 201:
                refresh_info(msg.getData());
                LoadingViewManager.dismiss();

                break;
            case 603:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "CDKEY无效", Toast.LENGTH_SHORT).show();
                break;
            case 400:
                LoadingViewManager.dismiss();
                Toast.makeText(getContext(), "网络似乎出现了点问题...\n请检查网络或于开发者选项者检查服务器", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.personal_service, container, false);

        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);

        LinearLayout linearLayout = root.findViewById(R.id.personal_input);
        member_card = root.findViewById(R.id.member_card);
        cdkey_et = linearLayout.findViewById(R.id.personal_cdkey);

        linearLayout.findViewById(R.id.personal_button1).setOnClickListener(this);
        root.findViewById(R.id.personal_button2).setOnClickListener(this);

        TextView textView = member_card.findViewById(R.id.member_username);
        textView.setText(UserManager.getUser()[0]);
        TextView textView1 = member_card.findViewById(R.id.member_id);
        textView1.setText(UserManager.getUser()[3]);

//        if(!UserManager.getUser()[3].equals("")){
//            Button button=root.findViewById(R.id.personal_button3);
//            button.setVisibility(View.VISIBLE);
//            button.setOnClickListener(this);
//        }
        UserManager.refreshInfo();

        LoadingViewManager
                .with(getActivity())
                .setHintText("刷新中...")
                .setAnimationStyle("BallScaleIndicator")
                .setShowInnerRectangle(true)
                .setOutsideAlpha(0.3f)
                .setLoadingContentMargins(50, 50, 50, 50)
                .build();
        return root;
    }

    private void refresh_info(Bundle bundle) {
        String expiry = bundle.getString("expiry");
        int times = (int) bundle.getDouble("times");
        TextView textView = member_card.findViewById(R.id.member_expiry);
        textView.setText(expiry);
        TextView textView1 = member_card.findViewById(R.id.member_times);
        String last = times + "次";
        textView1.setText(last);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_button1:
                //todo: 进入淘宝
                break;
            case R.id.personal_button2:
                //todo: 输入cdkey
                LoadingViewManager
                        .with(getActivity())
                        .setHintText("激活中...")
                        .setAnimationStyle("BallScaleIndicator")
                        .setShowInnerRectangle(true)
                        .setOutsideAlpha(0.3f)
                        .setLoadingContentMargins(50, 50, 50, 50)
                        .build();
                UserManager.activate(cdkey_et.getText() + "");
                break;
            case R.id.personal_button3:
                NavHostFragment.findNavController(this).navigate(R.id.action_nav_user_service_to_nav_user_profile);
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
