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

    public Curtain weaveCurtain(View view, Shape shape, int padding, int layout) {
        return new Curtain(weakReferenceActivity.get()).with(view)
                .withPadding(view, padding)
                .withShape(view, shape)
                .setTopView(layout);
    }

    public Curtain weaveCurtain(View view, Shape shape, int padding, int layout, Curtain.CallBack callBack) {
        return weaveCurtain(view, shape, padding, layout).setCallBack(callBack);
    }

    public Curtain weaveCurtain(Shape shape, int padding, int layout, View... views) {
        Curtain curtain = new Curtain(weakReferenceActivity.get()).setTopView(layout);
        for (View view : views) {
            if (view != null) {
                curtain.with(view).withPadding(view, padding).withShape(view, shape);
            }
        }
        return curtain;
    }
}
