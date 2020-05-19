package com.wzy.yuka;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.wzy.yuka.core.user.UserManager;
import com.wzy.yuka.tools.interaction.LoadingViewManager;
import com.wzy.yuka.tools.message.BaseActivity;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.ui.view.RoundImageView;

public class MainActivity extends BaseActivity implements GlobalHandler.HandleMsgListener {
    private AppBarConfiguration mAppBarConfiguration;
    private GlobalHandler globalHandler;
    private DrawerLayout drawer;
    private NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);

        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration
                .Builder(R.id.nav_home, R.id.nav_settings, R.id.nav_help, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        LinearLayout header = navigationView.getHeaderView(0).findViewById(R.id.line_header);
        Button login = header.findViewById(R.id.login_nav_header);
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
            globalHandler.sendMessage(message);
        } catch (Exception e) {
            UserManager.login();
            load("登录中...");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.getItem(0);
        menuItem.setOnMenuItemClickListener(item -> {
            if (UserManager.checkLogin()) {
                navController.navigate(R.id.action_nav_home_to_nav_user_service);
            } else {
                Toast.makeText(this, "没登录呢", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
//    private long exitTime;
//    @Override
//    public void onBackPressed() {
//        //todo 调用本方法检查
//        if (navController.getCurrentDestination().getLabel().equals("主页")) {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                //大于2000ms则认为是误操作，使用Toast进行提示
//                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                //并记录下本次点击“返回键”的时刻，以便下次进行判断
//                exitTime = System.currentTimeMillis();
//            } else {
//                //小于2000ms则认为是用户确实希望退出程序
//                super.onBackPressed();
//            }
//        }
//        else{
//            super.onBackPressed();
//        }
//    }

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
//        if (FloatWindowManager.floatBall!= null) {
//            FloatWindowManager.dismissAllFloatWindow(false);
//            FloatWindowManager.initFloatWindow(this);
//        }
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
                drawer.openDrawer(GravityCompat.START, true);
                break;
            case 400:
                LoadingViewManager.dismiss();
                Toast.makeText(this, "网络似乎出现了点问题...\n请检查网络或于开发者选项者检查服务器", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}


