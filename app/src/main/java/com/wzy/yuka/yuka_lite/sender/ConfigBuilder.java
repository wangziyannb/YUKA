package com.wzy.yuka.yuka_lite.sender;


import android.content.Context;
import android.content.res.Resources;

import com.wzy.yuka.R;
import com.wzy.yuka.tools.params.SharedPreferenceCollection;
import com.wzy.yuka.tools.params.SharedPreferencesUtil;
import com.wzy.yukalite.config.Mode;
import com.wzy.yukalite.config.Model;
import com.wzy.yukalite.config.Translator;
import com.wzy.yukalite.config.YukaConfig;

/**
 * Created by Ziyan on 2021/1/16.
 */
public class ConfigBuilder {
    public final static String text = "text";
    public final static String ocr = "ocr";
    public final static String translate = "translate";
    public final static String auto = "auto";

    /**
     * @param context 用于获取默认值的context
     * @param mode    只可能从以下中选择：text, ocr, translate, auto   yuka不需要yukaconfig
     * @return 组建完成的YukaConfig
     */
    public static YukaConfig yuka(Context context, String mode) {
        SharedPreferencesUtil spUtil = SharedPreferencesUtil.getInstance();
        Resources resources = context.getResources();
        YukaConfig.Builder builder = new YukaConfig.Builder();
        switch (mode) {
            case text:
                //仅仅只是翻译文本，所以不需要对ocr指定任何参数
                builder.setMode(Mode.text);
                break;
            case ocr:
                //仅仅只是识别文字，不需要翻译，所以只指定ocr参数就可以返回了
                builder.setMode(Mode.ocr);
                switch ((String) spUtil.getParam(SharedPreferenceCollection.detect_model, resources.getStringArray(R.array.detect_modelset)[0])) {
                    case "baidu":
                        builder.setOCR(Model.baidu,
                                (boolean) spUtil.getParam(SharedPreferenceCollection.detect_punctuation, false),
                                (boolean) spUtil.getParam(SharedPreferenceCollection.detect_vertical, false));
                        break;
                    case "youdao":
                        builder.setOCR(Model.youdao, (boolean) spUtil.getParam(SharedPreferenceCollection.detect_punctuation, false));
                        break;
                    case "google":
                        builder.setOCR(Model.google);
                        break;
                }
                return builder.build();
            case translate:
                //既要识别文字，还得额外翻译。需要指定ocr参数和translator参数
                builder.setMode(Mode.translate);
                switch ((String) spUtil.getParam(SharedPreferenceCollection.detect_model, resources.getStringArray(R.array.detect_modelset)[0])) {
                    case "baidu":
                        builder.setOCR(Model.baidu,
                                (boolean) spUtil.getParam(SharedPreferenceCollection.detect_punctuation, false),
                                (boolean) spUtil.getParam(SharedPreferenceCollection.detect_vertical, false));
                        break;
                    case "youdao":
                        builder.setOCR(Model.youdao, (boolean) spUtil.getParam(SharedPreferenceCollection.detect_punctuation, false));
                        break;
                    case "google":
                        builder.setOCR(Model.google);
                        break;
                }
                break;
            case auto:
                //更高级别的“translate”，但用的是另外一套ocr参数。translator则通用
                builder.setMode(Mode.auto);
                //标点优化
                boolean punctuation = (boolean) spUtil.getParam(SharedPreferenceCollection.auto_punctuation, false);
                //横竖排文字
                boolean vertical = (boolean) spUtil.getParam(SharedPreferenceCollection.auto_vertical, false);
                //宽容度
                int toleration = (int) spUtil.getParam(SharedPreferenceCollection.auto_toleration, 1) * 15;

                switch ((String) spUtil.getParam(SharedPreferenceCollection.auto_model, resources.getStringArray(R.array.auto_modelset)[0])) {
                    case "youdao":
                        builder.setOCR(Model.youdao, punctuation, toleration);
                        break;
                    case "baidu":
                        builder.setOCR(Model.baidu, punctuation, vertical, toleration);
                        break;
                    case "google":
                        builder.setOCR(Model.google, punctuation, vertical, toleration);
                        break;
                }
                break;
        }

        //翻译器相关选项
        switch ((String) spUtil.getParam(SharedPreferenceCollection.trans_translator, resources.getStringArray(R.array.translator)[0])) {
            case "youdao":
                builder.setTranslator(Translator.youdao);
                break;
            case "baidu":
                builder.setTranslator(Translator.baidu, (boolean) spUtil.getParam(SharedPreferenceCollection.trans_baidu_SBCS, false));
                break;
            case "tencent":
                builder.setTranslator(Translator.tencent);
                break;
            case "google":
                builder.setTranslator(Translator.google);
                break;
        }
        return builder.build();
    }
}
