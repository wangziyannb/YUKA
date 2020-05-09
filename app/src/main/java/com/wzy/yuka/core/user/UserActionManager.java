package com.wzy.yuka.core.user;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.wzy.yuka.tools.handler.GlobalHandler;
import com.wzy.yuka.tools.network.HttpRequest;
import com.wzy.yuka.tools.params.GetParams;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Ziyan on 2020/5/9.
 */
public class UserActionManager {
    static GlobalHandler globalHandler = GlobalHandler.getInstance();


    public static void login(Context context) {
        HttpRequest.Login(GetParams.Account(context), new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Bundle bundle = new Bundle();
                bundle.putString("error", e.toString());
                Message message = Message.obtain();
                message.what = 400;
                message.setData(bundle);
                globalHandler.sendMessage(message);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Bundle bundle = new Bundle();
                bundle.putString("response", response.body().string());
                Message message = Message.obtain();
                message.what = 200;
                message.setData(bundle);
                globalHandler.sendMessage(message);
            }
        });
    }

    public static void logout() {

    }

    public static void regist() {

    }

}
