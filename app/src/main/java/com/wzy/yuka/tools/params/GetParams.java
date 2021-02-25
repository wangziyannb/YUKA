package com.wzy.yuka.tools.params;

import android.content.Context;

import java.lang.ref.WeakReference;

public class GetParams {
    private static WeakReference<Context> context;

    public static void init(Context application) {
        context = new WeakReference<>(application);
    }
}
