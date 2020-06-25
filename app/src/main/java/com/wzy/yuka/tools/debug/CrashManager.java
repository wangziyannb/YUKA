package com.wzy.yuka.tools.debug;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.wzy.yuka.MainApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashManager implements Thread.UncaughtExceptionHandler {

    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Map<String, String> infos;
    private MainApplication application;

    public CrashManager(MainApplication application) {
        //获取系统默认的UncaughtExceptionHandler
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.application = application;
    }

    private boolean handleException(final Throwable exc) {
        Log.e("CrashManager", "出现错误");
        if (exc == null) {
            Log.e("CrashManager", "错误为null");
            return false;
        }
        new Thread(() -> {
            Looper.prepare();//准备
            Log.i("CrashManager", "崩溃正在写入日志");
            //处理崩溃
            collectDeviceAndUserInfo(application);
            writeCrash(exc);
            Looper.loop();
        }).start();
        return true;
    }

    /**
     * 采集设备和用户信息
     *
     * @param context 上下文
     */
    private void collectDeviceAndUserInfo(Context context) {

        PackageManager packageManager = context.getPackageManager();
        infos = new HashMap<>();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            if (packageInfo != null) {
                String versionName = packageInfo.versionName;
                infos.put("versionName", versionName);
                infos.put("crashTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("CrashManager", e.getMessage());
        }
        Field[] fields = Build.class.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            }
        } catch (IllegalAccessException e) {
            Log.e("CrashManager", e.getMessage());
        }
    }


    /**
     * 采集崩溃原因
     *
     * @param exc 异常
     */

    private void writeCrash(Throwable exc) {
        StringBuffer sb = new StringBuffer();
        sb.append("------------------crash----------------------");
        sb.append("\r\n");
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\r\n");
        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        exc.printStackTrace(pw);
        Throwable excCause = exc.getCause();
        while (excCause != null) {
            excCause.printStackTrace(pw);
            excCause = excCause.getCause();
        }
        pw.close();
        String result = writer.toString();
        sb.append(result);
        sb.append("\r\n");
        sb.append("-------------------end-----------------------");
        sb.append("\r\n");
        String savePath = application.getExternalFilesDir("logs").getAbsoluteFile() + "/";
        Log.i("路径：", savePath);
        writeLog(sb.toString(), savePath);
    }

    /**
     * @param log  文件内容
     * @param path 文件路径
     * @return 返回写入的文件路径
     * 写入Log信息的方法，写入到SD卡里面
     */
    private String writeLog(String log, String path) {
        Date nowTime = new Date();
        String needWriteFile = logfile.format(nowTime);
        //String filename = name + "mycrash"+ ".log";
        String filename = path + "crash_" + needWriteFile + ".txt";
        File file = new File(filename);
        if (file != null && file.exists() && file.length() + log.length() >= 64 * 1024) {
            //控制日志文件大小
            file.delete();
        }
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            //写入相关Log到文件
            bw.write(log);
            bw.newLine();
            bw.close();
            fw.close();
            return filename;
        } catch (IOException e) {
            Log.w("CrashManager", e.getMessage());
            return null;
        }


    }

    @Override
    public void uncaughtException(Thread thread, Throwable exc) {
        if (!handleException(exc) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
        } else {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(application);
            if (pref.getBoolean("settings_debug_log", true)) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Log.w("CrashManager", e.getMessage());
                }
                uploadExceptionToServer();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        }

        //这里可以上传异常信息到服务器，便于开发人员分析日志从而解决Bug
        //uploadExceptionToServer();
    }

    private void uploadExceptionToServer() {

    }
}