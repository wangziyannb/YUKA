package com.wzy.yuka.yuka_lite.sender;


import android.content.Context;
import android.content.res.Resources;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yukalite.config.Mode;
import com.wzy.yukalite.config.YukaConfig;

import java.lang.ref.WeakReference;

/**
 * Created by Ziyan on 2021/1/16.
 */
public class ConfigBuilder {

    private final String mode;
    private final YukaConfig.Builder builder;
    private final SharedPreferencesUtil spUtil;
    private final Resources resources;

    /**
     * @param context 用于获取默认值的context
     * @param mode    只可能从Modes中选择。yuka不需要yukaconfig
     */
    public ConfigBuilder(Context context, String mode) {
        WeakReference<Context> ref = new WeakReference<>(context);
        this.mode = mode;
        this.builder = new YukaConfig.Builder();
        this.spUtil = SharedPreferencesUtil.getInstance();
        this.resources = ref.get().getResources();
    }

    public YukaConfig getYukaConfig() {
        switch (mode) {
            case Modes.text:
                builder.setMode(Mode.text);
                setTranslator();
                break;
            case Modes.auto_text:
                builder.setMode(Mode.auto_text);
                setTranslator();
                break;
            case Modes.ocr:
                //仅仅只是识别文字，不需要翻译，所以只指定ocr参数就可以返回了
                builder.setMode(Mode.ocr);
                setOCR();
                break;
            case Modes.auto_ocr:
                //auto的仅识别模式，不翻译
                builder.setMode(Mode.auto_ocr);
                setOCR();
                break;
            case Modes.translate:
                //既要识别文字，还得额外翻译。需要指定ocr参数和translator参数
                builder.setMode(Mode.translate);
                setOCR();
                setTranslator();
                break;
            case Modes.auto:
                //更高级别的“translate”，但用的是另外一套ocr参数。translator则通用
                builder.setMode(Mode.auto);
                setOCR();
                setTranslator();
                break;
        }
        return builder.build();
    }

    private void setOCR() {
        switch (mode) {
            case Modes.ocr:
            case Modes.translate:
                switch ((String) spUtil.getParam(SharedPreferenceCollection.detect_model, resources.getStringArray(R.array.detect_modelset)[0])) {
                    case "baidu":
                        builder.setOCR_Baidu((boolean) spUtil.getParam(SharedPreferenceCollection.detect_punctuation, false),
                                (boolean) spUtil.getParam(SharedPreferenceCollection.detect_vertical, false));
                        break;
                    case "youdao":
                        builder.setOCR_Youdao((boolean) spUtil.getParam(SharedPreferenceCollection.detect_punctuation, false));
                        break;
                    case "google":
                        builder.setOCR_Google((boolean) spUtil.getParam(SharedPreferenceCollection.detect_vertical, false));
                        break;
                }
                break;
            case Modes.auto:
            case Modes.auto_ocr:
                //标点优化
                boolean punctuation = (boolean) spUtil.getParam(SharedPreferenceCollection.auto_punctuation, false);
                //横竖排文字
                boolean vertical = (boolean) spUtil.getParam(SharedPreferenceCollection.auto_vertical, false);
                //宽容度
                int toleration = (int) spUtil.getParam(SharedPreferenceCollection.auto_toleration, 1) * 15;

                switch ((String) spUtil.getParam(SharedPreferenceCollection.auto_model, resources.getStringArray(R.array.auto_modelset)[0])) {
                    case "youdao":
                        builder.setAutoOCR_Youdao(punctuation, toleration);
                        break;
                    case "baidu":
                        builder.setAutoOCR_Baidu(punctuation, vertical, toleration);
                        break;
                    case "google":
                        builder.setAutoOCR_Google(vertical, toleration);
                        break;
                }
                break;
        }
    }

    private void setTranslator() {
        switch (mode) {
            case Modes.text:
            case Modes.auto_text:
            case Modes.translate:
            case Modes.auto:
                switch ((String) spUtil.getParam(SharedPreferenceCollection.trans_translator, resources.getStringArray(R.array.translator)[0])) {
                    case "youdao":
                        builder.setTranslator_Youdao();
                        break;
                    case "baidu":
                        builder.setTranslator_Baidu((boolean) spUtil.getParam(SharedPreferenceCollection.trans_baidu_SBCS, false));
                        break;
                    case "tencent":
                        builder.setTranslator_Tencent();
                        break;
                    case "google":
                        builder.setTranslator_Google();
                        break;
                }
                break;
        }
    }
}