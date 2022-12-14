package com.wzy.yuka.yuka_lite.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
    int mHeight;
    int mWidth;
    private ImageReader mImageReader;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private OnShotListener mOnShotListener;
    private boolean otsu = false;
    private int[][] location;
    private String[] mLocalUrl;
    private boolean multiple = false;
    private int delay;

    @SuppressLint("WrongConstant")
    public Shotter(Context context, MediaProjection mediaProjection) {

        this.mRefContext = new SoftReference<>(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mMediaProjection = getMediaProjectionManager().getMediaProjection(reqCode, data);
            this.mMediaProjection = mediaProjection;
            WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display mDisplay = window.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            mDisplay.getRealMetrics(metrics);
            mWidth = metrics.widthPixels;//size.x;
            mHeight = metrics.heightPixels;//size.y;

            mImageReader = ImageReader.newInstance(
                    mWidth,
                    mHeight,
                    PixelFormat.RGBA_8888,//????????????????????? buffer????????????????????? ???RGB_565???????????????????????????????????????
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

    //???????????????????????????(??????????????????)
    public void startScreenShot(OnShotListener onShotListener, String[] loc_url, int[][] location, boolean otsu, boolean multiple, int delay) {
        mLocalUrl = loc_url;
        this.multiple = multiple;
        this.location = location;
        this.otsu = otsu;
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
            //??????delay ???????????????????????? ???????????????????????????????????????????????????????????? @see<a href="https://github.com/weizongwei5/AndroidScreenShot_SysApi/issues/4">issues</a>
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

    public class SaveTask extends AsyncTask<Image, Void, Bitmap[]> {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Bitmap[] doInBackground(Image... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            Image image = params[0];
            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //?????????????????????
            int pixelStride = planes[0].getPixelStride();
            //????????????
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                    Bitmap.Config.ARGB_8888);//??????????????????????????????????????? ???????????????
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            if (otsu) {
                bitmap = PicPreprocess.OTSUThreshold(bitmap);
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
                        //????????????
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
//                    mMediaProjection.stop();
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
            //do nothing
            super.onPostExecute(bitmap);
        }
    }
}