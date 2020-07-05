package com.wzy.yuka.yuka.utils;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ziyan on 2020/5/30.
 */
public class YoudaoAsrResolver {
    private String transPattern;
    private int segId;
    private int bg;
    private String context;
    private String tranContent;
    private boolean partial;
    private int ed;
    private String errorCode;
    private String action;
    private String total_time;

    public YoudaoAsrResolver(@NotNull String json) {
        Log.d("TAG", "YoudaoAsrResolver: " + json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("result")) {
                if (!jsonObject.getString("result").equals("{}")) {
                    JSONObject result = new JSONObject(jsonObject.getString("result"));
                    transPattern = result.getString("transPattern");
                    segId = result.getInt("segId");
                    bg = result.getInt("bg");
                    context = result.getString("context");
                    tranContent = result.getString("tranContent");
                    partial = result.getBoolean("partial");
                    ed = result.getInt("ed");

                }
            } else if (jsonObject.has("total_time")) {
                total_time = jsonObject.getString("total_time");
            }
            if (jsonObject.has("errorCode")) {
                errorCode = jsonObject.getString("errorCode");
            }
            if (jsonObject.has("action")) {
                action = jsonObject.getString("action");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTransPattern() {
        return transPattern;
    }

    public String getContext() {
        return context;
    }

    public String getTranContent() {
        return tranContent;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getAction() {
        return action;
    }

    public String getTotal_time() {
        return total_time;
    }

    public int getSegId() {
        return segId;
    }

    public int getBg() {
        return bg;
    }

    public int getEd() {
        return ed;
    }

    public boolean getPartial() {
        return partial;
    }
}
