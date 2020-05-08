package com.wzy.yuka.ui.log;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.handler.GlobalHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Login extends Fragment implements View.OnClickListener, GlobalHandler.HandleMsgListener {
    private GlobalHandler globalHandler;

    @Override
    public void handleMsg(Message msg) {
        Bundle bundle;
        switch (msg.what) {
            case 10:
                bundle = msg.getData();
                Toast.makeText(getContext(), bundle.getString("error"), Toast.LENGTH_SHORT).show();
                break;
            case 11:
                bundle = msg.getData();
                String response = bundle.getString("response");
                Log.d("TAG", "handleMsg: " + response);
                try {
                    JSONObject resultJson = new JSONObject(response);
                    String origin = resultJson.getString("origin");
                    if (origin.equals("200")) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLogin", true);
                        editor.commit();
                    }
                    String result = resultJson.getString("results");
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(this).navigateUp();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        View view = (View) v.getParent();
        switch (v.getId()) {
            case R.id.register:
                Navigation.findNavController(getView()).navigate(R.id.action_nav_login_to_nav_register);
                break;
            case R.id.login:
                String[] params = new String[3];
                Log.d("TAG", "onClick: ");
                TextView id = view.findViewById(R.id.id);
                TextView password = view.findViewById(R.id.password);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                params[0] = id.getText() + "";
                params[1] = password.getText() + "";
                params[2] = preferences.getString("uuid", "");
                com.wzy.yuka.tools.network.HttpRequest.Login(params, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Bundle bundle = new Bundle();
                        bundle.putString("error", e.toString());
                        Message message = Message.obtain();
                        message.what = 10;
                        message.setData(bundle);
                        globalHandler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Bundle bundle = new Bundle();
                        bundle.putString("response", response.body().string());
                        Message message = Message.obtain();
                        message.what = 11;
                        message.setData(bundle);
                        globalHandler.sendMessage(message);
                    }
                });
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.login, container, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String id = preferences.getString("id", "");
        String pwd = preferences.getString("pwd", "");
        TextView idt = root.findViewById(R.id.id);
        TextView passwordt = root.findViewById(R.id.password);
        idt.setText(id);
        passwordt.setText(pwd);
        root.findViewById(R.id.login).setOnClickListener(this);
        root.findViewById(R.id.register).setOnClickListener(this);
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        return root;
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