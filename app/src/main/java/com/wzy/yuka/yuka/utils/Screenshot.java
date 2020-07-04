package com.wzy.yuka.yuka.utils;

import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Screenshot {
    private String[] fullFileName;
    private int[][] location;
    private String filePath;
    private WeakReference<Context> mContext;

    public Screenshot(Context context, int[][] location) {
        this.location = location;
        fullFileName = new String[location.length];
        mContext = new WeakReference<>(context);
        Date nowTime = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time = dateFormat.format(nowTime);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
        String time2 = dateFormat2.format(nowTime);

        filePath = context.getExternalFilesDir("screenshot").getAbsoluteFile() + "/" + time;
        File file = new File(filePath);
        file.mkdir();
        for (int i = 0; i < location.length; i++) {
            fullFileName[i] = filePath + "/" + time2 + "_" + "LU" + location[i][0] + "_" + location[i][1] +
                    " " + "RU" + location[i][2] + "_" + location[i][3] + ".jpg";
        }
    }

    public void getScreenshot(boolean isGrayscale, int delay, Intent data, Shotter.OnShotListener onShotListener) {
        Shotter shotter = new Shotter(mContext.get(), -1, data);
        shotter.startScreenShot(onShotListener, fullFileName, location, isGrayscale, true, delay);
    }

    public String[] getFullFileNames() {
        return fullFileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void cleanImage() {
        for (String str : fullFileName) {
            File image = new File(str);
            image.delete();
        }
    }

}

