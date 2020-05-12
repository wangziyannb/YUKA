package com.wzy.yuka;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;
import com.wzy.yuka.core.user.UserManager;
import com.wzy.yuka.tools.handler.GlobalHandler;
import com.wzy.yuka.ui.view.RoundImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GlobalHandler.HandleMsgListener {
    private AppBarConfiguration mAppBarConfiguration;
    private GlobalHandler globalHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration
                .Builder(R.id.nav_home, R.id.nav_settings, R.id.nav_help, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        LinearLayout header = navigationView.getHeaderView(0).findViewById(R.id.line_header);
        Button login = header.findViewById(R.id.login_nav_header);
        Button logout = header.findViewById(R.id.logout_nav_header);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        UserManager.login();
        login.setOnClickListener((v) -> {
            if (preferences.getBoolean("isLogin", false)) {
                Toast.makeText(this, "您已登陆", Toast.LENGTH_SHORT).show();
            } else {
                navController.navigate(R.id.action_nav_home_to_nav_login);
                drawer.closeDrawers();
            }
        });
        logout.setOnClickListener((v) -> {
            globalHandler.setHandleMsgListener(this);
            if (preferences.getBoolean("isLogin", false)) {
                UserManager.logout();
                drawer.closeDrawers();
            } else {
                Toast.makeText(this, "未登录", Toast.LENGTH_SHORT).show();
            }
        });

        RoundImageView roundImageView = header.findViewById(R.id.user_info).findViewById(R.id.navBarIco);
        roundImageView.setOnClickListener((v) -> {
            navController.navigate(R.id.action_nav_home_to_nav_user_profile);
            drawer.closeDrawers();
        });



    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
//        if (FloatWindowManager.floatBall!= null) {
//            FloatWindowManager.dismissAllFloatWindow(false);
//            FloatWindowManager.initFloatWindow(this);
//        }
    }

    @Override
    public void handleMsg(Message msg) {
        Bundle bundle;
        String response;
        String error;
        switch (msg.what) {
            case 200:
                bundle = msg.getData();
                response = bundle.getString("response");
                try {
                    JSONObject resultJson = new JSONObject(response);
                    String origin = resultJson.getString("origin");
                    if (origin.equals("200")) {
                        Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLogin", true);
                        editor.commit();
                    }
                    if (origin.equals("601")) {
                        Toast.makeText(this, "请重新登陆", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 201:
                bundle = msg.getData();
                response = bundle.getString("response");
                try {
                    JSONObject resultJson = new JSONObject(response);
                    String result = resultJson.getString("results");
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(this, "服务器错误", Toast.LENGTH_SHORT).show();
                }
                break;
            case 400:
                Toast.makeText(this, "登陆/登出失败！请检查网络或于开发者选项者检查服务器！", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}


