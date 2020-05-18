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
import com.wzy.yuka.core.user.UserManager;
import com.wzy.yuka.tools.interaction.LoadingViewManager;
import com.wzy.yuka.tools.message.GlobalHandler;

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
                //todo: 进入商店
                alert();
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

    private void alert() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("关于购买");
        alert.setMessage("感谢使用Yuka！以下为购买说明：\n" +
                "1、注册账号后将会附送10次翻译次数，请确保已体验足够再进行购买。即使有月卡，也会优先扣除翻译次数！\n" +
                "2、购买月卡的价格是每月3元，后续可能只涨不降。\n" +
                "3、没有网银支付宝微信钱包的学生党可以联系作者或加群781666001，PY获得月卡\n" +
                "4、月卡商店开在微店上，并不需要下载微店app。\n" +
                "5、点击成为超级会员会跳转到商店页面，购买后自动发激活码，填于空位即可续期一个月。\n" +
                "6、请加入qq群781666001，参加Yuka的测试版本");
        alert.setPositiveButton("成为炒鸡会员！", (dialog, which) -> {
            Uri uri = Uri.parse("https://weidian.com/item.html?itemID=3625538574");
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
