package com.wzy.yuka.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.ActionMenuView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lzf.easyfloat.permission.PermissionUtils;
import com.qw.curtain.lib.CurtainFlow;
import com.qw.curtain.lib.flow.CurtainFlowInterface;
import com.qw.curtain.lib.shape.CircleShape;
import com.wzy.yuka.MainActivity;
import com.wzy.yuka.R;
import com.wzy.yuka.tools.interaction.GuideManager;
import com.wzy.yuka.tools.message.BaseFragment;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka_lite.YukaFloatWindowManager;
import com.wzy.yuka.yuka_lite.utils.SizeUtil;
import com.wzy.yukafloatwindows.FloatWindowManagerException;
import com.wzy.yukalite.YukaLite;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class HomeFragment extends BaseFragment implements View.OnClickListener {
    private static final int REQUEST_MEDIA_PROJECTION = 0x2893;
    private Intent data;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private Button button;
    private final SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
    private GuideManager guideManager;
    private YukaFloatWindowManager floatWindowManager;
    private MainActivity mainActivity;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bot_nav_start:
                if (YukaLite.isLogin()) {
                    if (!PermissionUtils.checkPermission(getContext())) {
                        PermissionUtils.requestPermission(getActivity(), b -> {
                            if (!b) {
                                Toast.makeText(getContext(), "用户未授权悬浮窗权限", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    if (data == null && floatWindowManager.getData() == null) {
                        requestPermission();
                    } else if (data != null || floatWindowManager.getData() != null) {
                        if (floatWindowManager.getNumOfFloatBalls() == 0) {
                            floatWindowManager.addFloatBall("mainFloatBall");
                            v.setBackgroundResource(R.drawable.nav_start_checked);
                        } else {
                            floatWindowManager.remove_AllFloatBall();
                            floatWindowManager.remove_AllFloatWindow();
                            v.setBackgroundResource(R.drawable.nav_start_unchecked);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "没登录呢", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.bot_nav_home:
                if (bottomNavigationView.getSelectedItemId() != R.id.bot_nav_home) {
                    bottomNavigationView.getMenu().getItem(0).setIcon(R.drawable.nav_home_checked);
                    bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.nav_history_unchecked);
                    navController.navigate(R.id.action_home_boutique_to_home_main);
                    bottomNavigationView.setSelectedItemId(R.id.bot_nav_home);
                }
                break;
            case R.id.bot_nav_history:
                if (bottomNavigationView.getSelectedItemId() != R.id.bot_nav_history) {
                    navController.navigate(R.id.action_home_main_to_home_boutique);
                    bottomNavigationView.getMenu().getItem(2).setIcon(R.drawable.nav_history_checked);
                    bottomNavigationView.getMenu().getItem(0).setIcon(R.drawable.nav_home_unchecked);
                    bottomNavigationView.setSelectedItemId(R.id.bot_nav_history);
                }
                break;
        }
    }

    @AfterPermissionGranted(233)
    @SuppressLint("WrongConstant")
    private void requestPermission() {
        String[] perms = {Manifest.permission.RECORD_AUDIO};
        if (!EasyPermissions.hasPermissions(getContext(), perms)) {
            EasyPermissions.requestPermissions(this, "拒绝了录音权限，内录同步字幕将会无法使用", 233, perms);
        } else {
            MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager) getActivity().getSystemService("media_projection");
            Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private long exitTime;

    @Override
    public boolean onBackPressed() {
        //当onBackPressed返回true时，证明子fragment有人响应事件
        if (!super.onBackPressed()) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(getContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                exitTime = System.currentTimeMillis();
                return true;
            } else {
                //小于2000ms则认为是用户确实希望退出程序
                return false;
            }
        } else {
            //此时return true和return super.onBackPressed()一样
            return true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);

        mainActivity = (MainActivity) view.getContext();
        guideManager = new GuideManager(this);
        button = bottomNavigationView.findViewById(R.id.bot_nav_start);
        button.setOnClickListener(this);
        View nav_home = bottomNavigationView.findViewById(R.id.bot_nav_home);
        nav_home.setOnClickListener(this);
        View nav_history = bottomNavigationView.findViewById(R.id.bot_nav_history);
        nav_history.setOnClickListener(this);
        try {
            floatWindowManager = YukaFloatWindowManager.getInstance();
            if (floatWindowManager.getData() != null) {
                this.data = floatWindowManager.getData();
                button.setBackgroundResource(R.drawable.nav_start_checked);
            }
        } catch (FloatWindowManagerException e) {
            e.printStackTrace();
        }
        navController = Navigation.findNavController(view.findViewById(R.id.fragment));
        showInitGuide();
        super.onViewCreated(view, savedInstanceState);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String TAG = "HomeFragment";
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.e(TAG, "User cancel");
        } else {
            //未知错误 java.lang.NoSuchMethodError: No interface method getCurrentWindowMetrics
            //确实是分版本了呀..?
//            try {
//                DisplayMetrics metrics = new DisplayMetrics();
//                WindowManager mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
//                if(Build.VERSION.SDK_INT> Build.VERSION_CODES.Q){
//                    mWindowManager.getCurrentWindowMetrics();
//                }else{
//                    mWindowManager.getDefaultDisplay().getMetrics(metrics);
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "MediaProjection error");
//                return;
//            }
            this.data = data;
            button.setBackgroundResource(R.drawable.nav_start_checked);
            if (!PermissionUtils.checkPermission(getContext())) {
                PermissionUtils.requestPermission(getActivity(), b -> {
                    if (!b) {
                        Toast.makeText(getContext(), "用户未授权悬浮窗权限", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            floatWindowManager.setData(data);
            floatWindowManager.addFloatBall("mainFloatBall");
        }
    }

    private void showInitGuide() {
        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.FIRST_LOGIN, true) && YukaLite.isLogin()) {
            ActionMenuView amv = mainActivity.findViewById(R.id.toolbar_menu);
            ActionMenuItemView amiv = (ActionMenuItemView) amv.getChildAt(0);
            if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.application_touchExplorationEnabled, false)) {
                new CurtainFlow.Builder()
                        .with(3, guideManager.weaveCurtain(button, new CircleShape(), 32, R.layout.guide_touchexploration))
                        .with(4, guideManager.weaveCurtain(amiv, new CircleShape(), 32, R.layout.guide_touchexploration))
                        .create().start(new CurtainFlow.CallBack() {
                    @Override
                    public void onProcess(int currentId, CurtainFlowInterface curtainFlow) {
                        ConstraintLayout layout = curtainFlow.findViewInCurrentCurtain(R.id.guide_te_layout);
                        TextView textView = layout.findViewById(R.id.guide_te_t1);
                        Button b = layout.findViewById(R.id.guide_te_b1);
                        switch (currentId) {
                            case 3:
                                textView.setText(R.string.guide_te_string4);
                                b.setOnClickListener(v -> {
                                    curtainFlow.push();
                                });
                                break;
                            case 4:
                                Toast.makeText(getContext(), R.string.guide_te_string5, Toast.LENGTH_SHORT).show();
                                textView.setText(R.string.guide_te_string5);
                                b.setOnClickListener(v -> {
                                    curtainFlow.finish();
                                });
                                break;
                        }
                    }

                    @Override
                    public void onFinish() {
                        sharedPreferencesUtil.saveParam(SharedPreferenceCollection.FIRST_LOGIN, false);
                    }
                });
            } else {
                new CurtainFlow.Builder()
                        .with(3, guideManager.weaveCurtain(button, new CircleShape(), 32, R.layout.guide_interpret))
                        .with(4, guideManager.weaveCurtain(amiv, new CircleShape(), 32, R.layout.guide_interpret))
                        .create().start(new CurtainFlow.CallBack() {
                    @Override
                    public void onProcess(int currentId, CurtainFlowInterface curtainFlow) {
                        ConstraintLayout layout = curtainFlow.findViewInCurrentCurtain(R.id.guide_interpret_layout);
                        ImageView img = layout.findViewById(R.id.guide_interpret_img);
                        ConstraintLayout.LayoutParams params_img = (ConstraintLayout.LayoutParams) img.getLayoutParams();
                        ConstraintSet set = new ConstraintSet();
                        switch (currentId) {
                            case 3:
                                layout.setOnClickListener(v -> {
                                    curtainFlow.push();
                                    layout.setOnClickListener(null);
                                });

                                img.setImageResource(R.drawable.guide_afterlogin_main);
                                img.setContentDescription("点击屏幕下方中间的启动按钮，启动悬浮球。双击以继续");
                                img.setScaleType(ImageView.ScaleType.FIT_END);

                                params_img.width = SizeUtil.dp2px(requireContext(), 180);
                                params_img.height = SizeUtil.dp2px(requireContext(), 180);
                                params_img.leftMargin = SizeUtil.dp2px(requireContext(), 30);

                                img.setLayoutParams(params_img);

                                set.clone(layout);
                                set.clear(R.id.guide_interpret_img, ConstraintSet.TOP);
                                set.applyTo(layout);
                                break;
                            case 4:
                                layout.setOnClickListener(v -> {
                                    curtainFlow.finish();
                                    layout.setOnClickListener(null);
                                });

                                img.setImageResource(R.drawable.guide_afterlogin_charge);
                                img.setContentDescription("点击屏幕右上角钱包按钮，进入充值页面。双击以结束引导");
                                img.setScaleType(ImageView.ScaleType.FIT_START);

                                params_img.width = SizeUtil.dp2px(requireContext(), 230);
                                params_img.height = SizeUtil.dp2px(requireContext(), 240);

                                img.setLayoutParams(params_img);
                                set.clone(layout);
                                set.clear(R.id.guide_interpret_img, ConstraintSet.BOTTOM);
                                set.clear(R.id.guide_interpret_img, ConstraintSet.LEFT);
                                set.applyTo(layout);
                                break;
                        }
                    }

                    @Override
                    public void onFinish() {
                        sharedPreferencesUtil.saveParam(SharedPreferenceCollection.FIRST_LOGIN, false);
                    }
                });
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
