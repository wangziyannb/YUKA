package com.wzy.yuka.ui.log;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.network.HttpRequest;
import com.wzy.yuka.tools.params.GetParams;

import org.json.JSONException;
import org.json.JSONObject;

public class Regist extends Fragment implements View.OnClickListener {
    private TableLayout tableLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.register, container, false);
        tableLayout = root.findViewById(R.id.tableLayout);
        root.findViewById(R.id.register_button).setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_button:
                EditText id_t = tableLayout.findViewById(R.id.id_regist);
                EditText un_t = tableLayout.findViewById(R.id.username_regist);
                EditText pwd_t = tableLayout.findViewById(R.id.password_regist);
                String[] params = new String[4];
                String[] account = GetParams.Account(getContext());
                params[0] = id_t.getText() + "";
                params[1] = pwd_t.getText() + "";
                params[2] = account[2];
                params[3] = un_t.getText() + "";
                if (responseProcess(HttpRequest.Register(params))) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("id", params[0]);
                    editor.putString("pwd", params[1]);
                    editor.putString("u_name", params[3]);
                    editor.commit();
                }
                break;
        }
    }

    private boolean responseProcess(String response) {
        if (response == null) {
            Toast.makeText(getContext(), "网络或服务器错误", Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject resultJson = new JSONObject(response);
                String origin = resultJson.getString("origin");
                String result = resultJson.getString("results");
                if (origin.equals("200")) {
                    Toast.makeText(getContext(), "注册成功，请返回登陆", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if (origin.equals("600")) {
                    Toast.makeText(getContext(), "注册失败，用户名或账号与他人相同", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
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
