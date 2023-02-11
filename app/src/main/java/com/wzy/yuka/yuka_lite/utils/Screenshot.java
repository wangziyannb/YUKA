package com.wzy.yuka.yuka_lite.utils;

import android.content.Context;
import android.media.projection.MediaProjection;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Screenshot {
    private final String[] fullFileName;
    private final int[][] location;
    private final String filePath;
    private final WeakReference<Context> mContext;
    private final int[] index;
    private final String[] fileName;

    public Screenshot(Context context, int[][] location, int[] index) {
        this.location = location;
        this.index = index;
        fileName = new String[location.length];
        fullFileName = new String[location.length];
        mContext = new WeakReference<>(context);
        Date nowTime = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time = dateFormat.format(nowTime);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH-mm-ss");
        String time2 = dateFormat2.format(nowTime);

        filePath = context.getExternalFilesDir("screenshot").getAbsoluteFile() + "/" + time;
        File file = new File(filePath);
        file.mkdir();
        for (int i = 0; i < location.length; i++) {
            fileName[i] = time2 + "_" + "LU" + location[i][0] + "_" + location[i][1] +
                    " " + "RU" + location[i][2] + "_" + location[i][3] + ".jpg";
            fullFileName[i] = filePath + "/" + fileName[i];
        }
    }

    public void getScreenshot(boolean otsu, int delay, MediaProjection mediaProjection, Shotter.OnShotListener onShotListener) {
        Shotter shotter = new Shotter(mContext.get(), mediaProjection);
        shotter.startScreenShot(onShotListener, fullFileName, location, otsu, true, delay);
    }

    public String[] getFullFileNames() {
        return fullFileName;
    }

    public String[] getFileNames() {
        return fileName;
    }

    public int[][] getLocation() {
        return location;
    }

    public String getFilePath() {
        return filePath;
    }

    public int[] getIndex() {
        return index;
    }

    public File[] getFiles() {
        File[] files = new File[fullFileName.length];
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(fullFileName[i]);
        }
        return files;
    }

    public void cleanImage() {
        for (String str : fullFileName) {
            File image = new File(str);
            image.delete();
        }
    }

}

