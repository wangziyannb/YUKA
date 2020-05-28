package com.wzy.yuka.tools.network;

import android.os.Handler;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by Ziyan on 2020/5/26.
 */
public class WebsocketRequest {
    private final String TAG = "WebsocketRequest";
    private String salt = UUID.randomUUID().toString();
    private String curtime = String.valueOf(System.currentTimeMillis() / 1000);
    private String from;
    private String to;
    private String sign = encrypt("2eede86073d15f36" + salt + curtime + "ksQVVUKaGf0tExTE64bMEvroNMKGiprM");

    private Handler handler = new Handler();
    private WebSocket mSocket;

    public WebsocketRequest(String from, String to) {
        this.from = from;
        this.to = to;
    }

    private static String encrypt(String strSrc) {
        byte[] bt = strSrc.getBytes();
        String encName = "SHA-256";
        try {
            MessageDigest md = MessageDigest.getInstance(encName);
            md.update(bt);
            return bytes2Hex(md.digest());
        } catch (NoSuchAlgorithmException ignored) {
            return strSrc;
        }
        // to HexString
    }

    private static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    public void send(ByteBuffer buffer) {
        mSocket.send(ByteString.of(buffer));
    }

    public void close() {
        byte[] closebytes = "{\"end\": \"true\"}".getBytes();
        mSocket.send(ByteString.of(closebytes));
        mSocket = null;
    }

    public boolean isClosed() {
        return mSocket == null;
    }

    public boolean isRunning() {
        return mSocket != null;
    }

    public void start() {
        String url = "wss://openapi.youdao.com/stream_speech_trans?appKey=2eede86073d15f36&salt=" + salt
                + "&curtime=" + curtime
                + "&sign=" + sign
                + "&version=v1&channel=1&format=wav&signType=v4&rate=16000&from=" + from
                + "&to=" + to
                + "&transPattern=stream";

        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .connectTimeout(3, TimeUnit.SECONDS)
                .pingInterval(15, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url).build();
        mOkHttpClient.newWebSocket(request, new mListener());
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
            if (text.contains("\"errorCode\":\"304\"")) {
                onClosed(webSocket, 304, "会话闲置太久被切断");
            }
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

