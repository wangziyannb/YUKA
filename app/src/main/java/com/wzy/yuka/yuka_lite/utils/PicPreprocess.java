package com.wzy.yuka.yuka_lite.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by Ziyan on 2021/8/20.
 */
public class PicPreprocess {
    public static Bitmap OTSUThreshold(Bitmap b) {
        int width = b.getWidth();
        int height = b.getHeight();
        Bitmap b1 = Bitmap.createBitmap(b.getWidth(), b.getHeight(), b.getConfig());
        int[] hist = new int[256];
        int[][] gray = new int[width][height];
        //灰度化同时计算直方图
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int colorPixel = b.getPixel(i, j);
                int red = (int) (0.2989 * Color.red(colorPixel)),
                        green = (int) (0.5870 * Color.green(colorPixel)),
                        blue = (int) (0.1140 * Color.blue(colorPixel));
                gray[i][j] = red + green + blue;
                b1.setPixel(i, j, Color.rgb(red, green, blue));
                hist[gray[i][j]]++;
            }
        }
        double[] P = new double[256];
        double[] PK = new double[256];
        double[] MK = new double[256];
        double pixnum = width * height, sumTmpPK = 0, sumTmpMK = 0;
        for (int i = 0; i < 256; ++i) {
            P[i] = hist[i] / pixnum;    //pi
            PK[i] = sumTmpPK + P[i];    //加入pi
            sumTmpPK = PK[i];         //p1
            MK[i] = sumTmpMK + i * P[i];  //m
            sumTmpMK = MK[i];         //mG
        }
        double Var = 0;
        int thresh = 0;
        for (int k = 0; k < 256; ++k) {
            double v = MK[255] * PK[k] - MK[k];  //mG*p1-m
            if (v * v > Var) {
                Var = v;
                thresh = k;
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (gray[x][y] < thresh) {
                    b1.setPixel(x, y, Color.rgb(0, 0, 0));
                } else {
                    b1.setPixel(x, y, Color.rgb(255, 255, 255));
                }
            }
        }
        return b1;
    }

}
