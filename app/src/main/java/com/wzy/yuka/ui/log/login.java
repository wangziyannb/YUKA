package com.wzy.yuka.ui.log;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wzy.yuka.R;

public class login extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.login, container, false);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:

        }

    }
}
