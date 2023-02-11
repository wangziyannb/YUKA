package com.wzy.yuka.yuka_lite.sender;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.wzy.yuka.yuka_lite.utils.Screenshot;

import org.json.JSONException;
import org.json.JSONObject;

public class MLKitVision {

    public static String postProcessing(Text result) {
        StringBuilder builder = new StringBuilder();
        for (Text.TextBlock block : result.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                builder.append(line.getText());
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public static void detect(Screenshot screenshot, String language, Handler handler, boolean save) {
        String[] pics = screenshot.getFullFileNames();
        TextRecognizer recognizer;
        switch (language) {
            case "Chinese":
                recognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
                break;
            case "Japanese":
                recognizer = TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());
                break;
            case "Korean":
                recognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
                break;
            case "Devanagari":
                recognizer = TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());
                break;
            case "latin":
            default:
                recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                break;
        }
        for (int i = 0; i < pics.length; i++) {
            InputImage inputImage = InputImage.fromBitmap(BitmapFactory.decodeFile(pics[i]), 0);
            int finalI = i;
            recognizer.process(inputImage)
                    .addOnSuccessListener(text -> {
                        try {
                            String t = postProcessing(text);
                            Bundle bundle = new Bundle();
                            bundle.putInt("index", finalI);
                            bundle.putBoolean("save", save);
                            JSONObject result = new JSONObject();
                            result.put("origin", t);
                            result.put("results", t);
                            result.put("time", 0);
                            bundle.putString("ocr_initial", result.toString());
                            bundle.putString("ocr", t);
                            bundle.putString("fileName", pics[finalI]);
                            bundle.putString("filePath", screenshot.getFilePath());
                            Message message = Message.obtain();
                            message.what = 0x1;
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    })
                    .addOnFailureListener(e -> {
                        //failed to detect texts in this area
                        String text = "识别错误，是否真的有字出现呢？\n错误信息：" + e.getMessage();
                        try {
                            Bundle bundle = new Bundle();
                            bundle.putInt("index", finalI);
                            bundle.putBoolean("save", save);
                            JSONObject result = new JSONObject();
                            result.put("origin", text);
                            result.put("results", text);
                            result.put("time", 0);
                            bundle.putString("ocr_initial", result.toString());
                            bundle.putString("ocr", text);
                            bundle.putString("fileName", pics[finalI]);
                            bundle.putString("filePath", screenshot.getFilePath());
                            Message message = Message.obtain();
                            message.what = 0x1;
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    });
        }
    }

}
