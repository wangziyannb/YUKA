package com.wzy.yuka.yuka_lite.utils;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.util.Log;


public class TTS {
    private static final String TAG = "TTS";
    private static TTS manager = null;
    private static TextToSpeech textToSpeech = null;
    private static boolean isReady = false;

    private TTS(Application application) {
        textToSpeech = new TextToSpeech(application, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Log.d(TAG, "onInit: success");
                isReady = true;
                Log.d(TAG, "TTS: " + textToSpeech.getEngines());
                Log.d(TAG, "TTS: " + textToSpeech.getAvailableLanguages());
            } else {
                Log.d(TAG, "onInit: failed");
            }
        });

    }

    public static void init(Application application) {
        manager = new TTS(application);
    }

    public static TTS getInstance(Application application) {
        if (manager == null) {
            synchronized (TTS.class) {
                if (manager == null) {
                    // 使用双重同步锁
                    manager = new TTS(application);
                    return manager;
                }
            }
        }
        return manager;
    }

    public int speak(CharSequence text, String utteranceId) {
        if (isReady && textToSpeech != null) {
            return textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId);
        }
        return TextToSpeech.ERROR;
    }

    public int stop() {
        if (isReady && textToSpeech != null) {
            return textToSpeech.stop();
        }
        return TextToSpeech.ERROR;
    }

    private static class TTSException extends Exception {
        public TTSException(String message) {
            super(message);
        }

        public static TTSException FailedToInit() {
            return new TTSException("Failed to init tts service");
        }
    }

}
