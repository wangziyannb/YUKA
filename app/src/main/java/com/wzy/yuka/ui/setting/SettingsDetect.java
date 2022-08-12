package com.wzy.yuka.ui.setting;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.interaction.LoadingViewManager;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.yuka_lite.sender.TessOCR;

/**
 * Created by Ziyan on 2020/6/6.
 */
public class SettingsDetect extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    //api设置
    ListPreference sender_api;
    ListPreference sender_api_trans;
    //yuka的识别器设置
    ListPreference model;
    //yuka的翻译器设置
    ListPreference translator;
    //other的识别器设置
    ListPreference model_other;
    //other的翻译器设置
    ListPreference translator_other;
    //tess的识别器设置
    ListPreference model_tess;
    ListPreference tess_langs;

    Preference checkModel;
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0x01:
                    LoadingViewManager.dismiss();
                    boolean tessReady = TessOCR.checkModel(getContext());
                    changeStateOfTess(tessReady);
                    checkModel.setSummary("当前可用性：" + (tessReady ? "是" : "否"));
                    break;
            }
        }
    };

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_detect, rootKey);
        sender_api = getPreferenceScreen().findPreference(SharedPreferenceCollection.detect_api);
        sender_api_trans = getPreferenceScreen().findPreference(SharedPreferenceCollection.trans_api);

        model = getPreferenceScreen().findPreference(SharedPreferenceCollection.detect_model);
        translator = getPreferenceScreen().findPreference(SharedPreferenceCollection.trans_translator);

        model_other = getPreferenceScreen().findPreference(SharedPreferenceCollection.detect_other_model);
        translator_other = getPreferenceScreen().findPreference(SharedPreferenceCollection.trans_other_translator);

        model_tess = getPreferenceScreen().findPreference(SharedPreferenceCollection.detect_tess_model);
        tess_langs = getPreferenceScreen().findPreference(SharedPreferenceCollection.detect_tess_lang);
        for (ListPreference l : new ListPreference[]{sender_api, sender_api_trans, model, translator, model_other, translator_other, model_tess, tess_langs}) {
            l.setValue(l.getValue() != null ? l.getValue() : l.getEntryValues()[0] + "");
            l.setSummary(l.getEntry() != null ? l.getEntry() : l.getEntries()[0]);
            l.setOnPreferenceChangeListener(this);
        }
        checkModel = getPreferenceScreen().findPreference(SharedPreferenceCollection.detect_tess_check_model);
        boolean tessReady = TessOCR.checkModel(getContext());
        changeStateOfTess(tessReady);
        checkModel.setSummary("当前可用性：" + (tessReady ? "是" : "否"));
        checkModel.setOnPreferenceClickListener(preference -> {
            LoadingViewManager
                    .with(getActivity())
                    .setHintText("正在下载模型文件(约160.32MB)...")
                    .setAnimationStyle("BallScaleIndicator")
                    .setShowInnerRectangle(true)
                    .setOutsideAlpha(0.3f)
                    .setLoadingContentMargins(50, 50, 50, 50)
                    .build();
            new Thread(() -> TessOCR.downloadModel(getContext(), () -> {
                Message message = Message.obtain();
                message.what = 0x01;
                handler.sendMessage(message);
            })).start();
            return false;
        });
        preferenceVisibilityChange();
    }


    private void changeStateOfTess(boolean available) {
        PreferenceScreen screen = getPreferenceScreen();
        PreferenceCategory category_model_tess = screen.findPreference(SharedPreferenceCollection.detect_tess);
        category_model_tess.findPreference(SharedPreferenceCollection.detect_tess_model).setEnabled(available);
        category_model_tess.findPreference(SharedPreferenceCollection.detect_tess_lang).setEnabled(available);
        category_model_tess.findPreference(SharedPreferenceCollection.detect_tess_lang_sub).setEnabled(available);
    }

    private void preferenceVisibilityChange() {
        PreferenceScreen screen = getPreferenceScreen();
        PreferenceCategory category_model = screen.findPreference(SharedPreferenceCollection.detect);
        PreferenceCategory category_translator = screen.findPreference(SharedPreferenceCollection.translator);
        PreferenceCategory category_model_other = screen.findPreference(SharedPreferenceCollection.detect_other);
        PreferenceCategory category_translator_other = screen.findPreference(SharedPreferenceCollection.translator_other);
        PreferenceCategory category_model_tess = screen.findPreference(SharedPreferenceCollection.detect_tess);

        if (category_model != null && category_translator != null && category_model_other != null && category_translator_other != null && category_model_tess != null) {
            switch (sender_api.getValue()) {
                case "other": {
                    //自定义
                    category_model.setVisible(false);
                    category_model_tess.setVisible(false);
                    category_model_other.setVisible(true);

                    //todo 加监听器送过去注册账号
                    Preference youdao_reg = screen.findPreference(SharedPreferenceCollection.detect_other_reg_youdao);
                    Preference baidu_reg = screen.findPreference(SharedPreferenceCollection.detect_other_reg_baidu);

                    EditTextPreference youdao_key = screen.findPreference(SharedPreferenceCollection.detect_other_youdao_key);
                    EditTextPreference youdao_sec = screen.findPreference(SharedPreferenceCollection.detect_other_youdao_appsec);
                    EditTextPreference baidu_key = screen.findPreference(SharedPreferenceCollection.detect_other_baidu_key);
                    EditTextPreference baidu_sec = screen.findPreference(SharedPreferenceCollection.detect_other_baidu_appsec);

                    SwitchPreference vertical = screen.findPreference(SharedPreferenceCollection.detect_other_vertical);
                    SwitchPreference punctuation = screen.findPreference(SharedPreferenceCollection.detect_other_punctuation);

                    Preference share_store = screen.findPreference(SharedPreferenceCollection.detect_share_store);

                    if (youdao_key != null && youdao_sec != null
                            && baidu_key != null && baidu_sec != null
                            && vertical != null && punctuation != null
                            && youdao_reg != null && baidu_reg != null) {
                        switch (model_other.getValue()) {
                            case "youdao":
                                youdao_reg.setVisible(true);
                                baidu_reg.setVisible(false);
                                youdao_key.setVisible(true);
                                youdao_sec.setVisible(true);
                                baidu_key.setVisible(false);
                                baidu_sec.setVisible(false);
                                vertical.setVisible(false);
                                break;
                            case "baidu":
                                youdao_reg.setVisible(false);
                                baidu_reg.setVisible(true);
                                youdao_key.setVisible(false);
                                youdao_sec.setVisible(false);
                                baidu_key.setVisible(true);
                                baidu_sec.setVisible(true);
                                vertical.setVisible(true);
                                break;
                        }
                    }
                    if (share_store != null) {
                        share_store.setVisible(false);
                    }
                    sender_api_trans.setEnabled(true);
                    break;
                }
                case "yuka_v1": {
                    //yuka_v1
                    category_model.setVisible(true);
                    category_model_other.setVisible(false);
                    category_model_tess.setVisible(false);

                    SwitchPreference vertical = screen.findPreference(SharedPreferenceCollection.detect_vertical);
                    SwitchPreference punctuation = screen.findPreference(SharedPreferenceCollection.detect_punctuation);
                    Preference share_store = screen.findPreference(SharedPreferenceCollection.detect_share_store);

                    if (vertical != null && model != null && punctuation != null) {
                        switch (model.getValue()) {
                            case "youdao":
                                vertical.setVisible(false);
                                punctuation.setVisible(true);
                                break;
                            case "google":
                                vertical.setVisible(true);
                                punctuation.setVisible(false);
                                break;
                            case "baidu":
                                vertical.setVisible(true);
                                punctuation.setVisible(true);
                                break;
                        }
                    }
                    if (share_store != null) {
                        share_store.setVisible(false);
                    }
                    sender_api_trans.setEnabled(true);
                    break;
                }
                case "share": {
                    category_model.setVisible(false);
                    category_model_other.setVisible(false);
                    category_model_tess.setVisible(false);
                    //only share mode need this notification
                    Preference share_store = screen.findPreference(SharedPreferenceCollection.detect_share_store);
                    if (share_store != null) {
                        share_store.setVisible(true);
                    }
                    sender_api_trans.setValueIndex(2);
                    sender_api_trans.setSummary(sender_api_trans.getEntry());
                    sender_api_trans.setEnabled(false);
                    break;
                }
                case "tess": {
                    category_model.setVisible(false);
                    category_model_other.setVisible(false);
                    category_model_tess.setVisible(true);
                    Preference share_store = screen.findPreference(SharedPreferenceCollection.detect_share_store);
                    if (share_store != null) {
                        share_store.setVisible(false);
                    }
                    sender_api_trans.setEnabled(true);
                    break;
                }
            }
            switch (sender_api_trans.getValue()) {
                case "other": {
                    //自定义
                    category_translator.setVisible(false);
                    category_translator_other.setVisible(true);

                    Preference youdao_reg_t = screen.findPreference(SharedPreferenceCollection.trans_other_reg_youdao);
                    Preference baidu_reg_t = screen.findPreference(SharedPreferenceCollection.trans_other_reg_baidu);

                    EditTextPreference youdao_key_t = screen.findPreference(SharedPreferenceCollection.trans_other_youdao_key);
                    EditTextPreference youdao_sec_t = screen.findPreference(SharedPreferenceCollection.trans_other_youdao_appsec);
                    EditTextPreference baidu_key_t = screen.findPreference(SharedPreferenceCollection.trans_other_baidu_key);
                    EditTextPreference baidu_sec_t = screen.findPreference(SharedPreferenceCollection.trans_other_baidu_appsec);

                    SwitchPreference SBCS = screen.findPreference(SharedPreferenceCollection.trans_other_baidu_SBCS);
                    if (youdao_key_t != null && youdao_sec_t != null && baidu_key_t != null && baidu_sec_t != null && SBCS != null && youdao_reg_t != null && baidu_reg_t != null) {
                        switch (translator_other.getValue()) {
                            case "youdao":
                                youdao_reg_t.setVisible(true);
                                baidu_reg_t.setVisible(false);
                                youdao_key_t.setVisible(true);
                                youdao_sec_t.setVisible(true);
                                baidu_key_t.setVisible(false);
                                baidu_sec_t.setVisible(false);
                                SBCS.setVisible(false);
                                break;
                            case "baidu":
                                youdao_reg_t.setVisible(false);
                                baidu_reg_t.setVisible(true);
                                youdao_key_t.setVisible(false);
                                youdao_sec_t.setVisible(false);
                                baidu_key_t.setVisible(true);
                                baidu_sec_t.setVisible(true);
                                SBCS.setVisible(true);
                                break;
                        }
                    }
                    break;
                }
                case "yuka_v1": {
                    //yuka_v1
                    category_translator.setVisible(true);
                    category_translator_other.setVisible(false);
                    SwitchPreference SBCS = screen.findPreference(SharedPreferenceCollection.trans_baidu_SBCS);
                    if (SBCS != null && translator != null) {
                        switch (translator.getValue()) {
                            case "youdao":
                            case "google":
                            case "tencent":
                                SBCS.setVisible(false);
                                break;
                            case "baidu":
                                SBCS.setVisible(true);
                                break;
                        }
                    }
                    break;
                }
                case "null": {
                    //不翻译
                    category_translator.setVisible(false);
                    category_translator_other.setVisible(false);
                    break;
                }
            }
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

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ((ListPreference) preference).setValue((String) newValue);
        preference.setSummary(((ListPreference) preference).getEntry());
        preferenceVisibilityChange();
        return false;
    }
}
