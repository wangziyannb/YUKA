package com.wzy.yuka;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.qw.curtain.lib.CurtainFlow;
import com.qw.curtain.lib.flow.CurtainFlowInterface;
import com.qw.curtain.lib.shape.CircleShape;
import com.qw.curtain.lib.shape.RoundShape;
import com.wzy.yuka.core.user.UserManager;
import com.wzy.yuka.tools.interaction.GuideManager;
import com.wzy.yuka.tools.interaction.LoadingViewManager;
import com.wzy.yuka.tools.message.BaseActivity;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yuka.tools.params.SizeUtil;
import com.wzy.yuka.ui.view.RoundImageView;

import java.lang.reflect.Field;
import java.util.Objects;


public class MainActivity extends BaseActivity implements GlobalHandler.HandleMsgListener {
    private AppBarConfiguration mAppBarConfiguration;
    private GlobalHandler globalHandler;
    public DrawerLayout drawer;
    private NavController navController;
    private SharedPreferencesUtil sharedPreferencesUtil = SharedPreferencesUtil.getInstance();
    private Button login;
    private ImageButton NavButton;
    public NavigationView navigationView;
    private GuideManager guideManager = new GuideManager(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);

        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);

        initToolbar(toolbar, navigationView);

        LinearLayout header = navigationView.getHeaderView(0).findViewById(R.id.line_header);
        login = header.findViewById(R.id.login_nav_header);
        Button logout = header.findViewById(R.id.logout_nav_header);
        login.setOnClickListener((v) -> {
            if (UserManager.checkLogin()) {
                Toast.makeText(this, "您已登陆", Toast.LENGTH_SHORT).show();
            } else {
                navController.navigate(R.id.action_nav_home_to_nav_login);
                drawer.closeDrawers();
            }
        });
        logout.setOnClickListener((v) -> {
            globalHandler.setHandleMsgListener(this);
            if (UserManager.checkLogin()) {
                UserManager.logout();
                drawer.closeDrawers();
                load("登出中...");
            } else {
                Toast.makeText(this, "未登录", Toast.LENGTH_SHORT).show();
            }
        });
        RoundImageView roundImageView = header.findViewById(R.id.user_info).findViewById(R.id.navBarIco);
        roundImageView.setOnClickListener((v) -> {
            if (UserManager.checkLogin()) {
                navController.navigate(R.id.action_nav_home_to_nav_user_service);
                drawer.closeDrawers();
            } else {
                Toast.makeText(this, "未登录", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            Message message = getIntent().getParcelableExtra("msg");
            if (message.what == 100) {
                //需要继续尝试登陆
                UserManager.login();
                load("登录中...");
            }
            globalHandler.sendMessage(message);
        } catch (NullPointerException e) {
            UserManager.login();
            load("登录中...");
        }

    }


    private void initToolbar(Toolbar toolbar, NavigationView navigationView) {
        ActionMenuView amv = findViewById(R.id.toolbar_menu);

        getMenuInflater().inflate(R.menu.main, amv.getMenu());
        MenuItem menuItem = amv.getMenu().getItem(0);

        menuItem.setOnMenuItemClickListener(item -> {
            if (UserManager.checkLogin()) {
                if (Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.nav_user_service) {
                    navController.navigate(R.id.nav_user_service);
                }
            } else {
                Toast.makeText(this, "没登录呢", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        drawer = findViewById(R.id.drawer);
        mAppBarConfiguration = new AppBarConfiguration
                .Builder(R.id.nav_home, R.id.nav_settings, R.id.nav_help, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(toolbar, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        try {
            Class<?> clazz = Toolbar.class;
            Field field = clazz.getDeclaredField("mNavButtonView");
            field.setAccessible(true);
            NavButton = (ImageButton) field.get(toolbar);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }


    }


    private void load(String text) {
        LoadingViewManager
                .with(this)
                .setHintText(text)
                .setAnimationStyle("BallScaleIndicator")
                .setShowInnerRectangle(true)
                .setOutsideAlpha(0.3f)
                .setLoadingContentMargins(50, 50, 50, 50)
                .build();
    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        if (isFinishing()) {
            globalHandler.removeCallbacks(null);
        }
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        //只有app内能用
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 200:
                LoadingViewManager.dismiss();
                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
                break;
            case 201:
                LoadingViewManager.dismiss();
                Toast.makeText(this, "已退出登陆", Toast.LENGTH_SHORT).show();
                break;
            case 601:
                LoadingViewManager.dismiss();
                Toast.makeText(this, "账户不存在", Toast.LENGTH_SHORT).show();
                if ((boolean) sharedPreferencesUtil.getParam(SharedPreferencesUtil.FIRST_OPEN_MainActivity, true)) {
                    showInitGuide_First();
                } else {
                    drawer.openDrawer(GravityCompat.START, true);
                }
                break;
            case 400:
                LoadingViewManager.dismiss();
                Toast.makeText(this, "网络似乎出现了点问题...\n请检查网络或于开发者选项者检查服务器", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private CurtainFlowInterface curtainFlowInterface;
    private DrawerLayout.SimpleDrawerListener listener = new DrawerLayout.SimpleDrawerListener() {
        @Override
        public void onDrawerOpened(View drawerView) {
            curtainFlowInterface.push();
        }
    };

    private void showInitGuide_First() {
        new CurtainFlow.Builder()
                .with(1, guideManager.weaveCurtain(NavButton, new CircleShape(), 0, R.layout.guide_interpret))
                .with(2, guideManager.weaveCurtain(login, new RoundShape(12), 0, R.layout.guide))
                .create()
                .start(new CurtainFlow.CallBack() {
                    @Override
                    public void onProcess(int currentId, CurtainFlowInterface curtainFlow) {
                        curtainFlowInterface = curtainFlow;
                        switch (currentId) {
                            case 1:
                                drawer.addDrawerListener(listener);
                                ConstraintLayout layout = curtainFlow.findViewInCurrentCurtain(R.id.guide_interpret_layout);
                                layout.setOnClickListener(v -> {
                                    drawer.openDrawer(GravityCompat.START, true);
                                    v.setOnClickListener(null);
                                });
                                ImageView img = layout.findViewById(R.id.guide_interpret_img);
                                img.setImageResource(R.drawable.guide_firstopen_menu);
                                img.setScaleType(ImageView.ScaleType.FIT_START);
                                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) img.getLayoutParams();
                                layoutParams.width = SizeUtil.dp2px(MainActivity.this, 350);
                                layoutParams.height = SizeUtil.dp2px(MainActivity.this, 200);
                                ConstraintSet set = new ConstraintSet();
                                set.clone(layout);
                                set.clear(R.id.guide_interpret_img, ConstraintSet.RIGHT);
                                set.clear(R.id.guide_interpret_img, ConstraintSet.BOTTOM);
                                set.applyTo(layout);
                                break;
                            case 2:
                                curtainFlow.findViewInCurrentCurtain(R.id.test_guide1).setOnClickListener(v -> {
                                    drawer.removeDrawerListener(listener);
                                    curtainFlow.finish();
                                });
                                break;
                        }
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(MainActivity.this, "主界面初次进入app注册登陆引导完成", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}


