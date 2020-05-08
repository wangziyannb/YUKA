package com.wzy.yuka;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.widget.Button;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.wzy.yuka.tools.handler.GlobalHandler;
import com.wzy.yuka.tools.network.HttpRequest;
import com.wzy.yuka.tools.params.GetParams;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements GlobalHandler.HandleMsgListener {
    private AppBarConfiguration mAppBarConfiguration;
    private GlobalHandler globalHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
//        checkUUID();
        login();
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
        Button login = navigationView.getHeaderView(0).findViewById(R.id.line_header).findViewById(R.id.login_nav_header);
        Button logout = navigationView.getHeaderView(0).findViewById(R.id.line_header).findViewById(R.id.logout_nav_header);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        login.setOnClickListener((v) -> {
            if (preferences.getBoolean("isLogin", false)) {
                Toast.makeText(this, "您已登陆", Toast.LENGTH_SHORT).show();
            } else {
                navController.navigate(R.id.action_nav_home_to_nav_login);
                drawer.closeDrawers();
            }

        });
        logout.setOnClickListener((v) -> {
            if (preferences.getBoolean("isLogin", false)) {
                String response = HttpRequest.Logout();
                if (response.equals("")) {
                    Toast.makeText(this, "网络或服务器错误", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject resultJson = new JSONObject(response);
                        String origin = resultJson.getString("origin");
                        String result = resultJson.getString("results");
                        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLogin", false);
                        editor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                drawer.closeDrawers();
            } else {
                Toast.makeText(this, "未登录", Toast.LENGTH_SHORT).show();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController1 = Navigation.findNavController(this, R.id.fragment);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(navController1.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController1, configuration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController1);
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
            globalHandler = null;
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

//    private void checkUUID() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        if (preferences.getString("uuid", "").equals("")) {
//            String uuid = UUID.randomUUID().toString();
//            Log.d("MainActivity", "初次安装,UUID:" + uuid);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putString("uuid", uuid);
//            editor.commit();
//        }
//    }

    private void login() {
        HttpRequest.Login(GetParams.Account(this), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Bundle bundle = new Bundle();
                bundle.putString("error", e.toString());
                Message message = Message.obtain();
                message.what = 400;
                message.setData(bundle);
                globalHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Bundle bundle = new Bundle();
                bundle.putString("response", response.body().string());
                Message message = Message.obtain();
                message.what = 200;
                message.setData(bundle);
                globalHandler.sendMessage(message);
            }
        });
    }

    @Override
    public void handleMsg(Message msg) {
        switch (msg.what) {
            case 200:
                responseProcess(msg);
                break;
            case 400:
                Toast.makeText(this, "登陆失败！请检查网络或于开发者选项种检查服务器！", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void responseProcess(Message message) {
        Bundle bundle = message.getData();
        String response = bundle.getString("response");
        try {
            JSONObject resultJson = new JSONObject(response);
            String origin = resultJson.getString("origin");
            String result = resultJson.getString("results");
            double time = resultJson.getDouble("time");
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

    }
}


