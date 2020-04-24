package com.wzy.yuka.tools.screenshot;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;

/**
 * Created by wei on 16-12-1.
 * Enhanced/Modified/Adjusted by wang on 20-04-04
 * implementation 'com.github.BruceWind:AndroidScreenShot_SysApi:1.0'
 * This version seems like an unpublished release?
 */
public class Shotter {

    private final SoftReference<Context> mRefContext;
    private ImageReader mImageReader;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private OnShotListener mOnShotListener;
    int mHeight;
    int mWidth;
    private boolean isGrayscale = false;
    private int[][] location;
    private String[] mLocalUrl;
    private boolean multiple = false;
    private int delay;

    public Shotter(Context context, int reqCode, Intent data) {

        this.mRefContext = new SoftReference<>(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjection = getMediaProjectionManager().getMediaProjection(reqCode, data);

            WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display mDisplay = window.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            mDisplay.getRealMetrics(metrics);
            mWidth = metrics.widthPixels;//size.x;
            mHeight = metrics.heightPixels;//size.y;

            mImageReader = ImageReader.newInstance(
                    mWidth,
                    mHeight,
                    PixelFormat.RGBA_8888,//此处必须和下面 buffer处理一致的格式 ，RGB_565在一些机器上出现兼容问题。
                    1);
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay() {

        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                mWidth,
                mHeight,
                Resources.getSystem().getDisplayMetrics().densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);

    }

    public void startScreenShot(OnShotListener onShotListener, String[] loc_url) {
        mLocalUrl = loc_url;
        startScreenShot(onShotListener);
    }

    //增加仅截取部分图片(包括灰度设定)
    public void startScreenShot(OnShotListener onShotListener, String[] loc_url, int[][] location, boolean isGrayscale, boolean multiple, int delay) {
        mLocalUrl = loc_url;
        this.multiple = multiple;
        this.location = location;
        this.isGrayscale = isGrayscale;
        this.delay = delay;
        startScreenShot(onShotListener);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void startScreenShot(OnShotListener onShotListener) {

        mOnShotListener = onShotListener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            virtualDisplay();

            Handler handler = new Handler();

            handler.postDelayed(() -> {
                        Image image = mImageReader.acquireLatestImage();
                        new SaveTask().doInBackground(image);
                    },
                    delay);
            //这里delay 时间过短容易导致 系统权限弹窗的阴影还没消失就完成了截图。 @see<a href="https://github.com/weizongwei5/AndroidScreenShot_SysApi/issues/4">issues</a>
        }

    }


    public class SaveTask extends AsyncTask<Image, Void, Bitmap[]> {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Bitmap[] doInBackground(Image... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            Log.e("shotter", "开始工作");
            Image image = params[0];
            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                    Bitmap.Config.ARGB_8888);//虽然这个色彩比较费内存但是 兼容性更好
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            if (isGrayscale) {
                Bitmap bmpGrayscale = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas c = new Canvas(bmpGrayscale);
                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                //Set the matrix to affect the saturation of colors.
                //A value of 0 maps the color to gray-scale.
                cm.setSaturation(0);
                ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(f);
                c.drawBitmap(bitmap, 0, 0, paint);
                bitmap = Bitmap.createBitmap(bmpGrayscale);
            }
            int num = 1;
            if (location != null && multiple) {
                num = location.length;
            }
            Bitmap[] bitmaps = new Bitmap[num];
            if (location != null && multiple) {
                for (int i = 0; i < num; i++) {
                    int windowWidth = location[i][2] - location[i][0];
                    int windowHeight = location[i][3] - location[i][1];
                    if (windowWidth > getScreenWidth()) {
                        windowWidth = getScreenWidth() - location[i][0];
                    }
                    if (windowHeight > getScreenHeight()) {
                        windowHeight = getScreenHeight() - location[i][1];
                    }
                    bitmaps[i] = Bitmap.createBitmap(bitmap, location[i][0], location[i][1],
                            windowWidth, windowHeight);
                    Log.d("shotter", i + "");
                }
            }
            image.close();
            File fileImage = null;
            for (int i = 0; i < bitmaps.length; i++) {
                if (bitmaps[i] == null) {
                    Log.e("shotter", "bitmap[" + i + "]==null");
                }
                if (bitmaps[i] != null) {
                    try {
                        //存储位置
                        if (TextUtils.isEmpty(mLocalUrl[i])) {
                            mLocalUrl[i] = getContext().getExternalFilesDir("screenshot").getAbsoluteFile()
                                    +
                                    "/"
                                    +
                                    SystemClock.currentThreadTimeMillis() + ".jpg";
                        }
                        fileImage = new File(mLocalUrl[i]);
                        if (!fileImage.exists()) {
                            fileImage.createNewFile();
                        }
                        FileOutputStream out = new FileOutputStream(fileImage);
                        if (out != null) {
                            bitmaps[i].compress(Bitmap.CompressFormat.JPEG, 90, out);
                            Log.d("shotter", "存储成功");
                            out.flush();
                            out.close();
                        }
                        if (bitmaps[i] != null && !bitmaps[i].isRecycled()) {
                            bitmaps[i].recycle();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        fileImage = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                        fileImage = null;
                    }
                }
            }
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            if (mVirtualDisplay != null) {
                mVirtualDisplay.release();
            }
            if (mMediaProjection != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mMediaProjection.stop();
                }
            }
            if (mOnShotListener != null) {
                for (String str : mLocalUrl) {
                    Log.d("Shotter path:", str + "");
                }
                mOnShotListener.onFinish();
            } else {
                Log.d("Shotter", "noShotListener");
            }
            if (fileImage != null) {
                return bitmaps;
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Bitmap[] bitmap) {
            super.onPostExecute(bitmap);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private MediaProjectionManager getMediaProjectionManager() {

        return (MediaProjectionManager) getContext().getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
    }

    private Context getContext() {
        return mRefContext.get();
    }


    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    // a  call back listener
    public interface OnShotListener {
        void onFinish();
    }
}