package com.wzy.yuka.ui.about;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wzy.yuka.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class AboutOpenSource extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.about_opensource, container, false);
        writeText2TextView(R.raw.easyfloat, root.findViewById(R.id.os1));
        writeText2TextView(R.raw.ss, root.findViewById(R.id.os2));
        writeText2TextView(R.raw.chinese_ocr, root.findViewById(R.id.os3));
        writeText2TextView(R.raw.vision_web_service, root.findViewById(R.id.os4));
        writeText2TextView(R.raw.text_renderer, root.findViewById(R.id.os5));
        writeText2TextView(R.raw.okhttp, root.findViewById(R.id.os6));
        return root;
    }

    public void writeText2TextView(int id, TextView textView) {
        try {
            InputStream inputStream = getResources().openRawResource(id);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                textView.append(line + "\n");
            }
            inputStream.close();
        } catch (Exception e) {
            Log.e("OpenSource", e.getMessage());
        }
    }

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.scene_open_enter);
        } else {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.scene_close_exit);
        }
    }
}
