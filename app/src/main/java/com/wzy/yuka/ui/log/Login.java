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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.wzy.yuka.MainViewModel;
import com.wzy.yuka.R;
import com.wzy.yuka.core.user.Account;
import com.wzy.yuka.core.user.UserManager;
import com.wzy.yuka.tools.handler.GlobalHandler;
import com.wzy.yuka.tools.interaction.LoadingViewManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends Fragment implements View.OnClickListener, GlobalHandler.HandleMsgListener {
    private GlobalHandler globalHandler;
    private Account account;
    private HashMap<String, String> hashMap;
    @Override
    public void handleMsg(Message msg) {
        Bundle bundle;
        switch (msg.what) {
            case 200:
                bundle = msg.getData();
                String response = bundle.getString("response");
                Log.d("TAG", "handleMsg: " + response);
                try {
                    LoadingViewManager.dismiss();
                    JSONObject resultJson = new JSONObject(response);
                    String origin = resultJson.getString("origin");
                    String result = resultJson.getString("results");
                    if (origin.equals("200")) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isLogin", true);
                        editor.commit();
                        NavHostFragment.findNavController(this).navigateUp();
                    }
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 400:
                LoadingViewManager.dismiss();
                bundle = msg.getData();
                Toast.makeText(getContext(), bundle.getString("error"), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.login, container, false);
        TextView id_t = root.findViewById(R.id.id);
        TextView pwd_t = root.findViewById(R.id.password);
        account = new Account(getContext());
        hashMap = account.get();
        id_t.setText(hashMap.get("id"));
        pwd_t.setText(hashMap.get("pwd"));

        final MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        final MutableLiveData<String> id = (MutableLiveData<String>) mainViewModel.getid();
        final MutableLiveData<String> pwd = (MutableLiveData<String>) mainViewModel.getpwd();

        id.observe(getViewLifecycleOwner(), id_t::setText);
        pwd.observe(getViewLifecycleOwner(), pwd_t::setText);

        root.findViewById(R.id.login).setOnClickListener(this);
        root.findViewById(R.id.register).setOnClickListener(this);
        globalHandler = GlobalHandler.getInstance();
        globalHandler.setHandleMsgListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        View view = (View) v.getParent();
        switch (v.getId()) {
            case R.id.register:
                Navigation.findNavController(getView()).navigate(R.id.action_nav_login_to_nav_register);
                break;
            case R.id.login:
                Log.d("TAG", "onClick: ");
                TextView id = view.findViewById(R.id.id);
                TextView password = view.findViewById(R.id.password);
                hashMap = account.get();
                hashMap.put("id", id.getText() + "");
                hashMap.put("pwd", password.getText() + "");
                account.update(hashMap);
                LoadingViewManager
                        .with(getActivity())
                        .setHintText("登录中...")
                        .setAnimationStyle("BallScaleIndicator")
                        .setShowInnerRectangle(true)
                        .setOutsideAlpha(0.3f)
                        .setLoadingContentMargins(50, 50, 50, 50)
                        .build();
                UserManager.login();
                break;
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
