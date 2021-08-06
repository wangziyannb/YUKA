package com.wzy.yuka.ui.user;

import android.content.Intent;
import android.net.Uri;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.interaction.LoadingViewManager;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.yuka_lite.Users;
import com.wzy.yukalite.YukaLite;
import com.wzy.yukalite.YukaUserManagerException;

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
                globalHandler.setHandleMsgListener(this);
                Users.refreshInfo();
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
        TextView textView1 = member_card.findViewById(R.id.member_id);
        try {
            textView.setText(YukaLite.getUser()[0]);
            textView1.setText(YukaLite.getUser()[2]);
        } catch (YukaUserManagerException e) {
            e.printStackTrace();
        }


//        if(!UserManager.getUser()[3].equals("")){
//            Button button=root.findViewById(R.id.personal_button3);
//            button.setVisibility(View.VISIBLE);
//            button.setOnClickListener(this);
//        }
        Users.refreshInfo();

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
        String expiry = bundle.getString("time");
        int remain = (int) bundle.getDouble("remain");
        int remain_advancetimes = (int) bundle.getDouble("remain_advancetimes");
        int sync_time = (int) bundle.getDouble("sync_time");
        TextView textView = member_card.findViewById(R.id.member_expiry);
        textView.setText(expiry);
        TextView textView1 = member_card.findViewById(R.id.member_times);
        String last = remain + "次";
        textView1.setText(last);
        TextView textView2 = member_card.findViewById(R.id.member_times_auto);
        last = remain_advancetimes + "次";
        textView2.setText(last);
        TextView textView3 = member_card.findViewById(R.id.member_times_sync);
        last = sync_time + "秒";
        textView3.setText(last);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_button1:
                //todo: 进入商店
                alert();
                break;
            case R.id.personal_button2:
                //todo: 输入cdkey
                globalHandler.setHandleMsgListener(this);
                LoadingViewManager
                        .with(getActivity())
                        .setHintText("激活中...")
                        .setAnimationStyle("BallScaleIndicator")
                        .setShowInnerRectangle(true)
                        .setOutsideAlpha(0.3f)
                        .setLoadingContentMargins(50, 50, 50, 50)
                        .build();
                try {
                    Users.activate(cdkey_et.getText() + "");
                } catch (YukaUserManagerException e) {
                    Toast.makeText(getContext(), "没有可用的用户", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                break;
            case R.id.personal_button3:
                NavHostFragment.findNavController(this).navigate(R.id.action_nav_user_service_to_nav_user_profile);
        }
    }

    private void alert() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("关于购买");
        alert.setMessage("感谢使用Yuka！以下为购买说明：\n" +
                "1、注册账号后将会分别附送20次普通翻译和自动翻译次数，请确保已体验足够再进行购买。即使有月卡，也会优先翻译次数！\n" +
                "2、购买月卡的价格是每月4元，后续可能改变价格，购买后将本月内每天使用300次普通和自动翻译。系统内录视频或游戏同传单次最多1小时，一小时14元\n" +
                "3、点击起飞会跳转到商店页面，购买后自动发激活码，填于空位即可根据购买的类型获得充值。购买并不需要下载微店app！\n" +
                "4、加群781666001，PY获得月卡。加群参与测试版本将不定期发放各种福利（不包括女装）\n");
        alert.setPositiveButton("起飞！", (dialog, which) -> {
            Uri uri = Uri.parse("https://weidian.com/?userid=1871665924");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        alert.setNegativeButton("别急，等等再说！", (dialog, which) -> {

        });
        alert.show();
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
