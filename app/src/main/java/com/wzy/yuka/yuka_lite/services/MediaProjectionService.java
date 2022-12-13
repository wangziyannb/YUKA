package com.wzy.yuka.yuka_lite.services;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.wzy.yuka.R;
import com.wzy.yuka.yuka_lite.YukaFloatWindowManager;

public class MediaProjectionService extends Service {
    private final MediaProjectionService.MPBinder binder = new MPBinder();
    private Intent data;
    private MediaProjection mediaProjection;

    public MediaProjectionService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
//        Intent data = floatWindowManager.getData();
//        floatWindowManager.setMediaProjection(getMediaProjectionManager().getMediaProjection(-1, data));
        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private MediaProjectionManager getMediaProjectionManager() {
        return (MediaProjectionManager) getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String id = "channel_01";
        CharSequence name = "Yuka";
        String description = "截屏服务已开启";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, id)
                    .setContentTitle(name).setContentText(description).setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher_radius).setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher_radius)).setAutoCancel(true).build();
            startForeground(110, notification);
        } else {
            NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            manager.createNotificationChannel(notificationChannel);
            Notification notification = new NotificationCompat.Builder(this, id)
                    .setContentTitle(name).setContentText(description).setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher_radius).setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher_radius))
                    .setAutoCancel(true).build();
            startForeground(110, notification);
        }

    }

    public void setData(Intent data) {
        this.data = data;
    }

    public Intent getData() {
        return this.data;
    }

    public MediaProjection getMediaProjection() {
        if (mediaProjection == null) {
            mediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK, (Intent) data.clone());
        }
        return mediaProjection;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MPBinder extends Binder {
        public MediaProjectionService getService() {
            return MediaProjectionService.this;
        }

    }
}