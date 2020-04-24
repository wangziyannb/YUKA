package com.wzy.yuka.tools.io;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResultOutput {

    private static void append(String fileName, String string) {
        File file = new File(fileName);
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(string);
            bw.close();
            fw.close();
        } catch (IOException e) {
            Log.e("ScreenshotList", e.getMessage());
        }
    }

    public static void appendResult(String file, String fileName, String result) {
        append(file, fileName + "_##_" + result + "\r\n");
    }
}
