package com.wzy.yuka.tools.io;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ziyan on 2020/5/17.
 */
public class ResultInput {
    public static ArrayList<String> ReadTxtFile(String strFilePath) {
        String path = strFilePath;
        ArrayList<String> newList = new ArrayList<>();
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("ResultInput", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        newList.add(line + "\n");
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        return newList;
    }

    public static String[] DecodeString(String str) {
        // /storage/emulated/0/Android/data/com.wzy.yuka/files/screenshot/2020-05-17 14:17:09_LU42_566 RU439_1532.jpg_##_参数错误
        String[] strings = new String[2];
        String pattern = "_##_";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        if (m.find()) {
            strings[0] = str.substring(0, m.start());
            strings[1] = str.substring(m.end());
            Log.d("input", strings[0] + "\n" + strings[1]);
        }
        return strings;
    }
}
