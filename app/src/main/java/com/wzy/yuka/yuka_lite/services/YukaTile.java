package com.wzy.yuka.yuka_lite.services;

import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.wzy.yuka.CurtainActivity;
import com.wzy.yuka.SplashActivity;
import com.wzy.yuka.yuka_lite.YukaFloatWindowManager;
import com.wzy.yukalite.YukaLite;

@RequiresApi(api = Build.VERSION_CODES.N)
public class YukaTile extends TileService {
    YukaFloatWindowManager floatWindowManager = YukaFloatWindowManager.getInstance(getApplication());


    @Override
    public void onClick() {
        Tile tile = getQsTile();
        if (tile == null) {
            return;
        }
        switch (tile.getState()) {
            case Tile.STATE_ACTIVE:
                tile.setState(Tile.STATE_INACTIVE);
                tile.updateTile();
                if (floatWindowManager.getNumOfFloatBalls() != 0) {
                    floatWindowManager.remove_AllFloatBall();
                }
                break;
            case Tile.STATE_INACTIVE:
                tile.setState(Tile.STATE_ACTIVE);
                tile.updateTile();
                if (YukaLite.isLogin()) {
                    if (floatWindowManager.getData() == null) {
                        Intent intent = new Intent(getApplicationContext(), CurtainActivity.class);
                        intent.putExtra(CurtainActivity.name, CurtainActivity.permission);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        if (floatWindowManager.getNumOfFloatBalls() == 0) {
                            floatWindowManager.addFloatBall("mainFloatBall");
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "没登录呢", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                break;
        }
    }

}