package com.wzy.yuka;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.qw.curtain.lib.Curtain;
import com.qw.curtain.lib.IGuide;
import com.wzy.yuka.tools.interaction.GuideManager;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.tools.params.SizeUtil;
import com.wzy.yuka.yuka.FloatWindowManager;
import com.wzy.yuka.yuka.floatwindow.FloatWindow;
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
        getWindow().setDimAmount(0f);
        setContentView(R.layout.guide_empty);
        Intent intent = getIntent();
        String type = intent.getStringExtra(name);
        int mIndex = intent.getIntExtra(index, 0);
        Log.e("TAG", "CA: type= " + type + ", mIndex= " + mIndex);
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
            FloatWindow floatWindow = floatWindowManager.get_FloatWindow(i);
            switch (type) {
                case "SWN_S":
                    SelectWindow_Normal normal_s = (SelectWindow_Normal) floatWindow;
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
                    SelectWindow_Normal normal_c = (SelectWindow_Normal) floatWindow;
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
                    SelectWindow_Auto auto = (SelectWindow_Auto) floatWindow;
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
                    SubtitleWindow sw = (SubtitleWindow) floatWindow;
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
            }
            curtain.show();
        } catch (FloatWindowManagerException e) {
            e.printStackTrace();
        }


    }
}
