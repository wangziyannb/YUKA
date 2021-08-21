package com.wzy.yuka.ui.home;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wzy.yuka.R;
import com.wzy.yuka.tools.message.BaseFragment;
import com.wzy.yuka.tools.message.GlobalHandler;
import com.wzy.yuka.yuka_lite.sender.ConfigBuilder;
import com.wzy.yuka.yuka_lite.sender.Modes;
import com.wzy.yukalite.YukaLite;
import com.wzy.yukalite.config.YukaConfig;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Ziyan on 2020/5/8.
 */
public class HomeMain extends BaseFragment implements View.OnClickListener, GlobalHandler.HandleMsgListener {
    private TextInputLayout text_l;
    private GlobalHandler globalHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_main, container, false);
        globalHandler = GlobalHandler.getInstance();
        text_l = root.findViewById(R.id.inputLayout);
        text_l.findViewById(R.id.translate_button).setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.translate_button:
                if (!YukaLite.isLogin()) {
                    Toast.makeText(getContext(), "请登陆", Toast.LENGTH_SHORT).show();
                } else {
                    TextInputEditText text_w = text_l.findViewById(R.id.origin_text);
                    String origin = text_w.getText() + "";
                    YukaConfig config = new ConfigBuilder(getContext(), Modes.text).getYukaConfig();
                    Callback callback = new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Bundle bundle = new Bundle();
                            bundle.putString("error", e.toString());
                            Message message = Message.obtain();
                            message.what = 400;
                            message.setData(bundle);
                            globalHandler.sendMessage(message);
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Bundle bundle = new Bundle();
                            bundle.putString("response", response.body().string());
                            Message message = Message.obtain();
                            message.what = 200;
                            message.setData(bundle);
                            globalHandler.sendMessage(message);
                        }
                    };
                    globalHandler.setHandleMsgListener(this);
                    YukaLite.request(config, origin, callback);
                }
                break;
        }

    }


    @Override
    public void handleMsg(Message msg) {
        Bundle bundle;
        String response;
        switch (msg.what) {
            case 200:
                bundle = msg.getData();
                response = bundle.getString("response");
                try {
                    Log.d("TAG", "handleMsg: " + response);
                    JSONObject resultJson = new JSONObject(response);
                    ImageView imageView = getView().findViewById(R.id.imageView2);
                    imageView.setVisibility(View.GONE);
                    TextView textView = getView().findViewById(R.id.text_translated);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(resultJson.getString("results"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "请求失败，请检查网络或于开发者选项者检查服务器！", Toast.LENGTH_SHORT).show();
                }
                break;
            case 400:
                Toast.makeText(getContext(), "请求失败，请检查网络或于开发者选项者检查服务器！", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        text_l = null;
        globalHandler = null;
        super.onDestroyView();
    }
}
