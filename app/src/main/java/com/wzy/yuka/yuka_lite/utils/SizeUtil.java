package com.wzy.yuka.yuka_lite.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.WindowManager;

public class SizeUtil {

    public static int dp2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int[] Screen(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int[] size = new int[3];
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            Rect rect = windowManager.getCurrentWindowMetrics().getBounds();
            size[0] = rect.width();
            size[1] = rect.height();
        } else {
            Point point = new Point();
            windowManager.getDefaultDisplay().getSize(point);
            size[0] = point.x;
            size[1] = point.y;
        }
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        size[2] = resources.getDimensionPixelSize(resourceId);
        return size;
    }
}