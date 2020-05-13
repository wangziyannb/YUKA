package com.wzy.yuka.core.screenshot;

import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

class Screenshot {
    private String[] fileName;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int[][] location;
    private Context context;

    Screenshot(Context context, int[][] location) {
        this.location = location;
        fileName = new String[location.length];
        this.context = context;
        Date nowTime = new Date();
        String time = dateFormat.format(nowTime);
        for (int i = 0; i < location.length; i++) {
            fileName[i] = context.getExternalFilesDir("screenshot").getAbsoluteFile()
                    + "/" + time + "_" + "LU" + location[i][0] + "_" + location[i][1] +
                    " " + "RU" + location[i][2] + "_" + location[i][3] + ".jpg";
        }
    }

    void getScreenshot(boolean isGrayscale, int delay, Intent data, Shotter.OnShotListener onShotListener) {
        Shotter shotter = new Shotter(context, -1, data);
        shotter.startScreenShot(onShotListener, fileName, location, isGrayscale, true, delay);
    }

    String[] getFileNames() {
        return fileName;
    }

    void cleanImage() {
        for (String str : fileName) {
            File image = new File(str);
            image.delete();
        }
    }


}

