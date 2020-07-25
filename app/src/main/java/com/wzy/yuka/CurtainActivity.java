package com.wzy.yuka;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentActivity;

import com.lzf.easyfloat.EasyFloat;
import com.qw.curtain.lib.Curtain;
import com.qw.curtain.lib.CurtainFlow;
import com.qw.curtain.lib.IGuide;
import com.qw.curtain.lib.flow.CurtainFlowInterface;
import com.qw.curtain.lib.shape.CircleShape;
import com.wzy.yuka.tools.interaction.GuideManager;
import com.wzy.yuka.tools.params.GetParams;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.tools.params.SizeUtil;
import com.wzy.yuka.yuka.FloatWindowManager;
import com.wzy.yuka.yuka.floatball.FloatBall;
import com.wzy.yuka.yuka.floatball.FloatBallLayout;
import com.wzy.yuka.yuka.floatwindow.SelectWindow_Auto;
import com.wzy.yuka.yuka.floatwindow.SelectWindow_Normal;
import com.wzy.yuka.yuka.floatwindow.SubtitleWindow;
import com.wzy.yuka.yuka.utils.FloatWindowManagerException;

/**
 * Created by Ziyan on 2020/7/5.
 */
public class CurtainActivity extends FragmentActivity {
    public static final String name = "type";
    public static final String index = "index";
    private Curtain curtain = null;
    private SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();

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
        } else {
            showInitGuide(type, mIndex);
        }
    }

    private void showInitGuide(String type, int i) {
        if (curtain != null) {
            return;
        }
        try {
            GuideManager guideManager = new GuideManager(this);
            FloatWindowManager floatWindowManager = FloatWindowManager.getInstance();
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
                                    img.setContentDescription("左上角关闭悬浮窗，右上角增加悬浮窗和启动翻译，右下角调整大小");
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
                                    img.setContentDescription("左上角关闭悬浮窗，右上角开始或暂停持续翻译，右下角调整大小");
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
                                    img.setContentDescription("左上角关闭悬浮窗，右上角启动自动翻译，右下角调整大小");
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
                                    img.setContentDescription("左上角关闭悬浮窗，右上角切换字幕显示方式和隐藏菜单按钮，右下角开始或暂停同步字幕翻译");

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
                    FloatBall fb = floatWindowManager.get_FloatBall(i);
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

    private void guideFloatBall(FloatBall floatBall) {
        GuideManager guideManager = new GuideManager(this);
        CurtainFlow cf = new CurtainFlow.Builder()
                .with(11, guideManager.weaveCurtain(floatBall.getView(), new CircleShape(), 0, R.layout.guide_interpret).setCancelBackPressed(false))
                .with(12, guideManager.weaveCurtain(floatBall.getView(), (canvas, paint, info) -> {
                }, 0, R.layout.guide_interpret).setCancelBackPressed(false))
                .create();
        cf.start(new CurtainFlow.CallBack() {
            ConstraintLayout layout;
            FloatBallLayout fbl = floatBall.getView().findViewById(R.id.floatball_layout);

            private void setImg(ConstraintLayout layout, int imageResource, int width, int height, int top, int left) {
                ImageView img = layout.findViewById(R.id.guide_interpret_img);
                img.setImageResource(imageResource);
                if(imageResource==R.drawable.guide_floatball_folded){
                    img.setContentDescription("点击悬浮球打开菜单");
                }else if(imageResource==R.drawable.guide_floatball_deployed){
                    img.setContentDescription("最上方为设置按钮，在二级面板下为单或多悬浮窗翻译。主要效果为进入悬浮窗、悬浮球、各种模式的设置页面。\n" +
                            "右上为识别按钮，在二级面板下为初始化持续翻译悬浮窗按钮。主要效果为启动所有已经存在的悬浮窗的识别。\n" +
                            "右下为初始化按钮，在二级面板下为自动识别翻译悬浮窗按钮。主要效果为初始化上一个使用过的类型的悬浮窗。初次打开app时，默认单/多悬浮窗模式。长按此按钮进入二级面板。\n" +
                            "下方为退出按钮，在二级面板下为视频同步字幕悬浮窗按钮。主要效果为退出app。\n"+
                            "点击左中关闭按钮继续。");
                }

                img.setScaleType(ImageView.ScaleType.FIT_START);
                ConstraintLayout.LayoutParams params_img = (ConstraintLayout.LayoutParams) img.getLayoutParams();

                int statusBarHeight = GetParams.Screen()[2];
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
                            public void folded() {
                                ImageButton imageButton = floatBall.getView().findViewById(R.id.floatball_main);
                                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) floatBall.getView().getLayoutParams();
                                int[] size = GetParams.Screen();
                                imageButton.setBackgroundResource(R.drawable.main);
                                imageButton.setContentDescription("Yuka悬浮球");
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
