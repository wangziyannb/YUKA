package com.wzy.yuka.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wzy.yuka.R;

/**
 * author:tajinxiong
 */
public class GuidePageFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_guide, container, false);
        root.findViewById(R.id.Narcissu1).setOnClickListener(this);
        root.findViewById(R.id.Narcissu2).setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        switch (v.getId()) {
            case R.id.Narcissu1:
                intent.setData(Uri.parse("https://www.bilibili.com/video/BV1hK411T7gq"));
                startActivity(intent);
            case R.id.Narcissu2:
                intent.setData(Uri.parse("https://www.bilibili.com/video/BV1yi4y1u7uM"));
                startActivity(intent);
        }
    }
}
