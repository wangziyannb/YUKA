package com.wzy.yuka.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.debug.UpdateManager;

public class AboutDev extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View about = inflater.inflate(R.layout.about_dev, container, false);

        about.findViewById(R.id.personal_function).setOnClickListener(this);
        about.findViewById(R.id.personal_web).setOnClickListener(this);
        about.findViewById(R.id.personal_update).setOnClickListener(this);

        return about;
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_function:
                Navigation.findNavController(getView()).navigate(R.id.action_nav_about_dev_to_nav_about_dev_function);
                break;
            case R.id.personal_web:
                Uri uri = Uri.parse("https://yukacn.xyz/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.personal_update:
                checkUpdate();
                break;
            default:
                break;
        }
    }

    private void checkUpdate() {
        UpdateManager manager = new UpdateManager(getContext());
        manager.findUpdate();
    }
}
