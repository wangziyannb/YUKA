package com.wzy.yuka;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentActivity;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.permission.PermissionUtils;
import com.qw.curtain.lib.Curtain;
import com.qw.curtain.lib.CurtainFlow;
import com.qw.curtain.lib.IGuide;
import com.qw.curtain.lib.flow.CurtainFlowInterface;
import com.qw.curtain.lib.shape.CircleShape;
import com.wzy.yuka.tools.interaction.GuideManager;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.yuka_lite.YukaFloatWindowManager;
import com.wzy.yuka.yuka_lite.floatball.FloatBallLayout;
import com.wzy.yuka.yuka_lite.floatball.MainFloatBall;
import com.wzy.yuka.yuka_lite.floatwindow.SelectWindow_Auto;
import com.wzy.yuka.yuka_lite.floatwindow.SelectWindow_Normal;
import com.wzy.yuka.yuka_lite.floatwindow.SubtitleWindow;
import com.wzy.yuka.yuka_lite.utils.SizeUtil;
import com.wzy.yukafloatwindows.FloatWindowManagerException;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Ziyan on 2020/7/5.
 */
public class CurtainActivity extends FragmentActivity {
    public static final String name = "type";
    public static final String index = "index";
    public static final String permission = "Permission";
    private Curtain curtain = null;
    private static final int REQUEST_MEDIA_PROJECTION = 0x2893;
    private final SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();

    private void showInitGuide(String type, int i) {
        if (curtain != null) {
            return;
        }
        try {
            GuideManager guideManager = new GuideManager(this);
            YukaFloatWindowManager floatWindowManager = YukaFloatWindowManager.getInstance(getApplication());
            switch (type) {
                case "SWN_S":
                    SelectWindow_Normal normal_s = (SelectWindow_Normal) floatWindowManager.get_FloatWindow(i);
                    curtain = guideManager.weaveCurtain(findViewById(R.id.guide_empty_layout), (canvas, paint, info) -> {
                    }, 0, R.layout.guide_interpret)
                            .setCallBack(new Curtain.CallBack() {
                                @Override
                                public void onShow(IGuide iGuide) {
                                    normal_s.hide();
                                    ConstraintLayout layout = iGuide.findViewByIdInTopView(R.id.guide_interpret_layout);
                                    layout.setOnClickListener(v -> {
                                        iGuide.dismissGuide();
                                        v.setOnClickListener(null);
                                    });
                                    ImageView img = layout.findViewById(R.id.guide_interpret_img);
                                    img.setImageResource(R.drawable.guide_floatwindow_normal);
                                    img.setContentDescription("??????????????????????????????????????????????????????????????????????????????????????????");
                                    ConstraintLayout.LayoutParams params_img = (ConstraintLayout.LayoutParams) img.getLayoutParams();
                                    params_img.width = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 335);
                                    params_img.height = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 242);
                                    params_img.topMargin = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 10);
                                    params_img.rightMargin = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 10);
                                    img.setLayoutParams(params_img);
                                }

                                @Override
                                public void onDismiss(IGuide iGuide) {
                                    sharedPreferencesUtil.saveParam(SharedPreferenceCollection.FIRST_SelectWindow_N_1, false);
                                    normal_s.show();
                                    normal_s.isContinue = false;
                                    finish();
                                }
                            });
                    break;
                case "SWN_C":
                    SelectWindow_Normal normal_c = (SelectWindow_Normal) floatWindowManager.get_FloatWindow(i);
                    curtain = guideManager.weaveCurtain(findViewById(R.id.guide_empty_layout), (canvas, paint, info) -> {
                    }, 0, R.layout.guide_interpret)
                            .setCallBack(new Curtain.CallBack() {
                                @Override
                                public void onShow(IGuide iGuide) {
                                    normal_c.hide();
                                    ConstraintLayout layout = iGuide.findViewByIdInTopView(R.id.guide_interpret_layout);
                                    layout.setOnClickListener(v -> {
                                        iGuide.dismissGuide();
                                        v.setOnClickListener(null);
                                    });
                                    ImageView img = layout.findViewById(R.id.guide_interpret_img);
                                    img.setImageResource(R.drawable.guide_floatwindow_continue);
                                    img.setContentDescription("???????????????????????????????????????????????????????????????????????????????????????");
                                    ConstraintLayout.LayoutParams params_img = (ConstraintLayout.LayoutParams) img.getLayoutParams();
                                    params_img.width = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 335);
                                    params_img.height = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 242);
                                    params_img.topMargin = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 10);
                                    params_img.rightMargin = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 10);
                                    img.setLayoutParams(params_img);
                                }

                                @Override
                                public void onDismiss(IGuide iGuide) {
                                    sharedPreferencesUtil.saveParam(SharedPreferenceCollection.FIRST_SelectWindow_N_2, false);
                                    normal_c.show();
                                    normal_c.isContinue = false;
                                    finish();
                                }
                            });
                    break;
                case "SWA":
                    SelectWindow_Auto auto = (SelectWindow_Auto) floatWindowManager.get_FloatWindow(i);
                    curtain = guideManager.weaveCurtain(findViewById(R.id.guide_empty_layout), (canvas, paint, info) -> {
                    }, 0, R.layout.guide_interpret)
                            .setCallBack(new Curtain.CallBack() {
                                @Override
                                public void onShow(IGuide iGuide) {
                                    auto.hide();
                                    ConstraintLayout layout = iGuide.findViewByIdInTopView(R.id.guide_interpret_layout);
                                    layout.setOnClickListener(v -> {
                                        iGuide.dismissGuide();
                                        v.setOnClickListener(null);
                                    });
                                    ImageView img = layout.findViewById(R.id.guide_interpret_img);
                                    img.setImageResource(R.drawable.guide_floatwindow_auto);
                                    img.setContentDescription("??????????????????????????????????????????????????????????????????????????????");
                                    ConstraintLayout.LayoutParams params_img = (ConstraintLayout.LayoutParams) img.getLayoutParams();

                                    params_img.width = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 335);
                                    params_img.height = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 242);

                                    params_img.topMargin = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 10);
                                    params_img.rightMargin = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 10);
                                    img.setLayoutParams(params_img);
                                }

                                @Override
                                public void onDismiss(IGuide iGuide) {
                                    sharedPreferencesUtil.saveParam(SharedPreferenceCollection.FIRST_SelectWindow_A, false);
                                    finish();
                                    auto.shows();
                                }
                            });
                    break;
                case "SBW":
                    SubtitleWindow sw = (SubtitleWindow) floatWindowManager.get_FloatWindow(i);
                    curtain = guideManager.weaveCurtain(findViewById(R.id.guide_empty_layout), (canvas, paint, info) -> {
                    }, 0, R.layout.guide_interpret)
                            .setCallBack(new Curtain.CallBack() {
                                @Override
                                public void onShow(IGuide iGuide) {
                                    sw.hide();
                                    ConstraintLayout layout = iGuide.findViewByIdInTopView(R.id.guide_interpret_layout);
                                    layout.setOnClickListener(v -> {
                                        iGuide.dismissGuide();
                                        v.setOnClickListener(null);
                                    });
                                    ImageView img = layout.findViewById(R.id.guide_interpret_img);
                                    img.setImageResource(R.drawable.guide_floatwindow_subtitle);
                                    img.setContentDescription("??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");

                                    ConstraintLayout.LayoutParams params_img = (ConstraintLayout.LayoutParams) img.getLayoutParams();

                                    params_img.width = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 335);
                                    params_img.height = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 242);

                                    params_img.topMargin = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 10);
                                    params_img.rightMargin = SizeUtil.dp2px(floatWindowManager.getApplicationWeakReference().get(), 10);
                                    img.setLayoutParams(params_img);
                                }

                                @Override
                                public void onDismiss(IGuide iGuide) {
                                    sharedPreferencesUtil.saveParam(SharedPreferenceCollection.FIRST_SubtitleWindow, false);
                                    finish();
                                    sw.show();
                                }
                            });
                    break;
                case "FB":
                    MainFloatBall fb = (MainFloatBall) floatWindowManager.get_FloatBall(i);
                    guideFloatBall(fb);
                    break;
            }
            if (curtain != null) {
                curtain.show();
            }
        } catch (FloatWindowManagerException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.guide_empty);
        Intent intent = getIntent();
        String type = intent.getStringExtra(name);
        int mIndex = intent.getIntExtra(index, 0);
        if (type == null) {
            finish();
        } else if (type.equals(permission)) {
            requestPermission();
        } else {
            showInitGuide(type, mIndex);
        }
    }

    private void guideFloatBall(MainFloatBall floatBall) {
        GuideManager guideManager = new GuideManager(this);
        if ((boolean) sharedPreferencesUtil.getParam(SharedPreferenceCollection.application_touchExplorationEnabled, false)) {
            CurtainFlow cf = new CurtainFlow.Builder()
                    .with(11, guideManager.weaveCurtain(floatBall.getView(), new CircleShape(), 0, R.layout.guide_touchexploration).setCancelBackPressed(false))
                    .with(12, guideManager.weaveCurtain(floatBall.getView(), (canvas, paint, info) -> {
                    }, 0, R.layout.guide_touchexploration).setCancelBackPressed(false))
                    .create();
            cf.start(new CurtainFlow.CallBack() {
                ConstraintLayout layout;
                TextView textView;
                Button button;
                final FloatBallLayout fbl = floatBall.getView().findViewById(R.id.floatball_layout);

                @Override
                public void onProcess(int currentId, CurtainFlowInterface curtainFlow) {
                    switch (currentId) {
                        case 11:
                            layout = curtainFlow.findViewInCurrentCurtain(R.id.guide_te_layout);
                            EasyFloat.appFloatDragEnable(false, floatBall.getTag());
                            button = layout.findViewById(R.id.guide_te_b1);
                            textView = layout.findViewById(R.id.guide_te_t1);
                            textView.setText(R.string.guide_te_string6);
                            button.setOnClickListener(v1 -> {
                                fbl.findViewById(R.id.floatball_main).performClick();
                            });
                            fbl.setFloatBallLayoutListener(new FloatBallLayout.FloatBallLayoutListener() {
                                @Override
                                public void deployed() {
                                    textView.setText(R.string.guide_te_string7);
                                    Toast.makeText(CurtainActivity.this, R.string.guide_te_string7, Toast.LENGTH_SHORT).show();
                                    Resources resources = getResources();
                                    ImageButton top = fbl.findViewById(R.id.floatball_top);
                                    top.setContentDescription(resources.getString(R.string.guide_te_string8));
                                    ImageButton right1 = fbl.findViewById(R.id.floatball_mid1);
                                    right1.setContentDescription(resources.getString(R.string.guide_te_string9));
                                    ImageButton right2 = fbl.findViewById(R.id.floatball_mid2);
                                    right2.setContentDescription(resources.getString(R.string.guide_te_string10));
                                    ImageButton bottom = fbl.findViewById(R.id.floatball_bottom);
                                    bottom.setContentDescription(resources.getString(R.string.guide_te_string11));
                                    ImageButton mid = fbl.findViewById(R.id.floatball_main);
                                    mid.setContentDescription(resources.getString(R.string.guide_te_string12));
                                    floatBall.setMainOnClickListeners();
                                }

                                @Override
                                public void onDeploy() {

                                }

                                @Override
                                public void onFold() {

                                }

                                @Override
                                public void folded() {
                                    ImageButton imageButton = floatBall.getView().findViewById(R.id.floatball_main);
                                    WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) floatBall.getView().getLayoutParams();
                                    int[] size = SizeUtil.Screen(getApplicationContext());
                                    imageButton.setBackgroundResource(R.drawable.main);
                                    imageButton.setContentDescription("Yuka?????????");
                                    layoutParams.y = layoutParams.y + SizeUtil.dp2px(floatBall.getView().getContext(), 52);
                                    if (layoutParams.x > size[0] / 2) {
                                        layoutParams.x = layoutParams.x + SizeUtil.dp2px(floatBall.getView().getContext(), (float) (52 / 2 * Math.sqrt(3)));
                                    }
                                    getWindowManager().updateViewLayout(floatBall.getView(), layoutParams);
                                    floatBall.setMainOnClickListeners();
                                    curtainFlow.push();
                                }
                            });
                            break;
                        case 12:
                            layout = curtainFlow.findViewInCurrentCurtain(R.id.guide_te_layout);
                            textView = layout.findViewById(R.id.guide_te_t1);
                            textView.setText(R.string.guide_te_string13);
                            Toast.makeText(CurtainActivity.this, R.string.guide_te_string13, Toast.LENGTH_SHORT).show();
                            fbl.removeFloatBallLayoutListener();
                            floatBall.removeOnClickListeners();
                            button = layout.findViewById(R.id.guide_te_b1);
                            button.setOnClickListener(v -> {
                                curtainFlow.finish();
                            });
                            break;
                    }
                }

                @Override
                public void onFinish() {
                    EasyFloat.appFloatDragEnable(true, floatBall.getTag());
                    floatBall.setMainOnClickListeners();
                    floatBall.isInGuiding = false;
                    sharedPreferencesUtil.saveParam(SharedPreferenceCollection.FIRST_FloatBall, false);
                    finish();
                }
            });
        } else {
            CurtainFlow cf = new CurtainFlow.Builder()
                    .with(11, guideManager.weaveCurtain(floatBall.getView(), new CircleShape(), 0, R.layout.guide_interpret).setCancelBackPressed(false))
                    .with(12, guideManager.weaveCurtain(floatBall.getView(), (canvas, paint, info) -> {
                    }, 0, R.layout.guide_interpret).setCancelBackPressed(false))
                    .create();
            cf.start(new CurtainFlow.CallBack() {
                ConstraintLayout layout;
                final FloatBallLayout fbl = floatBall.getView().findViewById(R.id.floatball_layout);

                private void setImg(ConstraintLayout layout, int imageResource, int width, int height, int top, int left) {
                    ImageView img = layout.findViewById(R.id.guide_interpret_img);
                    img.setImageResource(imageResource);
                    if (imageResource == R.drawable.guide_floatball_folded) {
                        img.setContentDescription("???????????????????????????");
                    } else if (imageResource == R.drawable.guide_floatball_deployed) {
                        Resources resources = getResources();
                        img.setContentDescription(resources.getString(R.string.guide_te_string7)
                                + resources.getString(R.string.guide_te_string8)
                                + resources.getString(R.string.guide_te_string9)
                                + resources.getString(R.string.guide_te_string10)
                                + resources.getString(R.string.guide_te_string11)
                                + resources.getString(R.string.guide_te_string12));
                    }

                    img.setScaleType(ImageView.ScaleType.FIT_START);
                    ConstraintLayout.LayoutParams params_img = (ConstraintLayout.LayoutParams) img.getLayoutParams();

                    int statusBarHeight = SizeUtil.Screen(getApplicationContext())[2];
                    int[] params_floatBall = new int[2];
                    floatBall.getView().getLocationOnScreen(params_floatBall);
                    params_floatBall[1] -= statusBarHeight;
                    params_img.width = SizeUtil.dp2px(getApplicationContext(), width);
                    params_img.height = SizeUtil.dp2px(getApplicationContext(), height);
                    params_img.topMargin = SizeUtil.dp2px(getApplicationContext(), top) + params_floatBall[1];
                    params_img.leftMargin = SizeUtil.dp2px(getApplicationContext(), left) + params_floatBall[0];

                    img.setLayoutParams(params_img);

                    ConstraintSet set = new ConstraintSet();
                    set.clone(layout);
                    set.clear(R.id.guide_interpret_img, ConstraintSet.RIGHT);
                    set.clear(R.id.guide_interpret_img, ConstraintSet.BOTTOM);
                    set.applyTo(layout);
                }

                @Override
                public void onProcess(int currentId, CurtainFlowInterface curtainFlow) {
                    switch (currentId) {
                        case 11:
                            layout = curtainFlow.findViewInCurrentCurtain(R.id.guide_interpret_layout);
                            EasyFloat.appFloatDragEnable(false, floatBall.getTag());
                            fbl.setFloatBallLayoutListener(new FloatBallLayout.FloatBallLayoutListener() {
                                @Override
                                public void deployed() {
                                    floatBall.setMainOnClickListeners();
                                    curtainFlow.push();
                                }

                                @Override
                                public void onDeploy() {

                                }

                                @Override
                                public void onFold() {

                                }

                                @Override
                                public void folded() {
                                    ImageButton imageButton = floatBall.getView().findViewById(R.id.floatball_main);
                                    WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) floatBall.getView().getLayoutParams();
                                    int[] size = SizeUtil.Screen(getApplicationContext());
                                    imageButton.setBackgroundResource(R.drawable.main);
                                    imageButton.setContentDescription("Yuka?????????");
                                    layoutParams.y = layoutParams.y + SizeUtil.dp2px(floatBall.getView().getContext(), 52);
                                    if (layoutParams.x > size[0] / 2) {
                                        layoutParams.x = layoutParams.x + SizeUtil.dp2px(floatBall.getView().getContext(), (float) (52 / 2 * Math.sqrt(3)));
                                    }
                                    getWindowManager().updateViewLayout(floatBall.getView(), layoutParams);
                                    floatBall.setMainOnClickListeners();
                                    EasyFloat.appFloatDragEnable(true, floatBall.getTag());
                                    curtainFlow.finish();
                                }
                            });
                            setImg(layout, R.drawable.guide_floatball_folded, 192, 27, 44, 44);
                            break;
                        case 12:
                            layout = curtainFlow.findViewInCurrentCurtain(R.id.guide_interpret_layout);
                            setImg(layout, R.drawable.guide_floatball_deployed, 320, 340, -42, -1);
                            break;
                    }
                }

                @Override
                public void onFinish() {
                    EasyFloat.appFloatDragEnable(true, floatBall.getTag());
                    fbl.removeFloatBallLayoutListener();
                    floatBall.isInGuiding = false;
                    sharedPreferencesUtil.saveParam(SharedPreferenceCollection.FIRST_FloatBall, false);
                    finish();
                }
            });
        }


    }

    @AfterPermissionGranted(233)
    @SuppressLint("WrongConstant")
    private void requestPermission() {
        String[] perms = {Manifest.permission.RECORD_AUDIO};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "????????????????????????????????????????????????????????????", 233, perms);
        } else {
            MediaProjectionManager mMediaProjectionManager = (MediaProjectionManager) this.getSystemService("media_projection");
            Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String TAG = "CurtainActivity";
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.e(TAG, "User cancel");
        } else {
            //???????????? java.lang.NoSuchMethodError: No interface method getCurrentWindowMetrics
            //????????????????????????..?
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
            if (!PermissionUtils.checkPermission(this)) {
                PermissionUtils.requestPermission(this, b -> {
                    if (!b) {
                        Toast.makeText(this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        YukaFloatWindowManager manager = YukaFloatWindowManager.getInstance(getApplication());
                        manager.setData(data);
                        if (manager.getNumOfFloatBalls() == 0) {
                            manager.addFloatBall("mainFloatBall");
                        }
                    }
                });
            } else {
                YukaFloatWindowManager manager = YukaFloatWindowManager.getInstance(getApplication());
                manager.setData(data);
                if (manager.getNumOfFloatBalls() == 0) {
                    manager.addFloatBall("mainFloatBall");
                }
            }
        }
        finish();
    }
}
