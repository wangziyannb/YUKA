package com.wzy.yuka.tools.network;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.wzy.yuka.tools.message.GlobalHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
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

    private String u_name;
    private String uuid;

    private WebSocket mSocket;
    private GlobalHandler globalHandler = GlobalHandler.getInstance();

    public WebsocketRequest(@NotNull String[] user) {
        this.u_name = user[0];
        this.uuid = user[2];
    }

    public void send(ByteBuffer buffer) {
        if (mSocket != null) {
            mSocket.send(ByteString.of(buffer));
        }
    }

    public void close() {
        byte[] closebytes = "fin".getBytes();
        if (mSocket != null) {
            mSocket.send(ByteString.of(closebytes));
            mSocket = null;
        }
    }

    public boolean isClosed() {
        return mSocket == null;
    }

    public boolean isRunning() {
        return mSocket != null;
    }

    public void start(@NotNull String from, @NotNull String to, @NotNull String pattern) {
        String url = "wss://yukacn.xyz/yuka/youdaoAsr"
                + "/" + u_name + "&" + uuid
                + "/from=" + from + "&to=" + to + "&p=" + pattern;

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
            Message message = Message.obtain();
            Bundle bundle = new Bundle();

            if (text.contains("\"errorCode\":\"602\"") || text.contains("\"errorCode\":\"601\"")) {
                onClosed(webSocket, 600, "");
                bundle.putString("syncMessage", text);
                message.what = 253;
            } else if (text.contains("\"total_time\"")) {
                message.what = 252;
                bundle.putString("syncMessage", text);
            } else if (text.equals("ready") || (text.contains("{}"))) {
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

