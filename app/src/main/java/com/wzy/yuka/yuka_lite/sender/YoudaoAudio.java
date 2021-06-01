package com.wzy.yuka.yuka_lite.sender;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.tools.network.SyncAudio;
import com.wzy.yuka.tools.params.Encrypt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by Ziyan on 2021/6/1.
 */
public class YoudaoAudio extends SyncAudio {
    private final String TAG = "YoudaoAudio";
    private final GlobalHandler globalHandler = GlobalHandler.getInstance();
    private final String APP_KEY;
    private final String APP_SECRET;
    private WebSocket mSocket;

    public YoudaoAudio(String APP_KEY, String APP_SECRET) {
        this.APP_KEY = APP_KEY;
        this.APP_SECRET = APP_SECRET;
    }

    public void send(ByteBuffer buffer) {
        if (mSocket != null) {
            mSocket.send(ByteString.of(buffer));
        }
    }

    public void close() {
        byte[] closebytes = "{\"end\": \"true\"}".getBytes();
        if (mSocket != null) {
            mSocket.send(ByteString.of(closebytes));
            mSocket = null;
        }
    }

    public void start(@NotNull String from, @NotNull String to, @NotNull String pattern) {
        String salt = UUID.randomUUID() + "";
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        String sign = Encrypt.sha256(APP_KEY + salt + curtime + APP_SECRET);
        String signType = "v4";
        String format = "wav";
        String channel = "1";
        String version = "v1";
        String rate = "16000";
        String url = "wss://openapi.youdao.com/stream_speech_trans?appKey=" + APP_KEY
                + "&salt=" + salt
                + "&curtime=" + curtime
                + "&sign=" + sign
                + "&signType=" + signType
                + "&from=" + from
                + "&to=" + to
                + "&format=" + format
                + "&channel=" + channel
                + "&version=" + version
                + "&rate=" + rate
                + "&transPattern=" + pattern;
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .connectTimeout(3, TimeUnit.SECONDS)
                .pingInterval(15, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url).build();
        mOkHttpClient.newWebSocket(request, new YoudaoAudio.mListener());
    }

    public boolean isClosed() {
        return mSocket == null;
    }

    public boolean isRunning() {
        return mSocket != null;
    }

    private final class mListener extends WebSocketListener {
        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
            mSocket = webSocket;
            Log.d(TAG, "onOpen: !!!");
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
            Log.d(TAG, "onMessage: " + text);
            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            if (text.contains("\"action\":\"error\"")) {
                onClosed(webSocket, 600, "");
                bundle.putString("syncMessage", text);
                message.what = 253;
            } else if (text.contains("\"action\":\"started\"") && text.contains("\"result\": {}")) {
                message.what = 251;
            } else {
                message.what = 250;
                bundle.putString("syncMessage", text);
            }
            message.setData(bundle);
            globalHandler.sendMessage(message);
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
            Log.d(TAG, "onClosed: !!!");
            mSocket = null;
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            Log.d(TAG, "onFailure: " + t.getMessage());
        }
    }
}
