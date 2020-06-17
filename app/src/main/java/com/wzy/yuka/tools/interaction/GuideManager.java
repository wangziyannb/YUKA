package com.wzy.yuka.tools.interaction;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.qw.curtain.lib.Curtain;
import com.qw.curtain.lib.shape.Shape;

import java.lang.ref.WeakReference;

/**
 * Created by Ziyan on 2020/6/17.
 */
public class GuideManager {
    private WeakReference<FragmentActivity> weakReferenceActivity;

    public GuideManager(FragmentActivity activity) {
        weakReferenceActivity = new WeakReference<>(activity);
    }

    public GuideManager(Fragment fragment) {
        weakReferenceActivity = new WeakReference<>(fragment.getActivity());
    }

    public Curtain showCurtain(View view, Shape shape, int padding, int layout) {
        return new Curtain(weakReferenceActivity.get()).with(view)
                .withPadding(view, padding)
                .withShape(view, shape)
                .setTopView(layout);
    }

    public Curtain showCurtain(View view, Shape shape, int padding, int layout, Curtain.CallBack callBack) {
        return new Curtain(weakReferenceActivity.get()).with(view)
                .withPadding(view, padding)
                .withShape(view, shape)
                .setTopView(layout)
                .setCallBack(callBack);
    }
}
