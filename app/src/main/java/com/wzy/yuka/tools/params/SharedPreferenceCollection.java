package com.wzy.yuka.tools.params;

/**
 * Created by Ziyan on 2020/7/8.
 */
public class SharedPreferenceCollection {
    //无障碍
    public static final String application_touchExplorationEnabled = "application_touchExplorationEnabled";

    //悬浮球相应设置项
    public static final String ball_autoHide = "settings_ball_autoHide";
    public static final String ball_autoClose = "settings_ball_autoClose";
    public static final String ball_safeMode = "settings_ball_safeMode";
    public static final String ball_fluidMode = "settings_ball_fluidMode";

    //悬浮窗相应设置项
    public static final String window_textBlackBg = "settings_window_textBlackBg";
    public static final String window_opacityBg = "settings_window_opacityBg";
    public static final String window_originalText = "settings_window_originalText";

    public static final String debug_savePic = "settings_debug_savePic";
    public static final String action_fastMode = "settings_fastMode";

    //单多悬浮窗相应设置项
    //使用的api
    public static final String detect_api = "settings_detect_api";
    //设置组-单多悬浮窗的识别器(yuka)
    public static final String detect = "settings_detect";
    public static final String detect_model = "settings_detect_model";
    public static final String detect_punctuation = "settings_detect_punctuation";
    public static final String detect_vertical = "settings_detect_vertical";
    //设置组-单多悬浮窗的识别器(other)
    public static final String detect_other = "settings_detect_other";
    public static final String detect_other_model = "settings_detect_other_detect_model";
    public static final String detect_other_reg_baidu = "settings_detect_other_reg_baidu";
    public static final String detect_other_reg_youdao = "settings_detect_other_reg_youdao";
    public static final String detect_other_vertical = "settings_detect_other_vertical";
    public static final String detect_other_punctuation = "settings_detect_other_punctuation";
    public static final String detect_other_youdao_key = "settings_detect_other_youdao_appkey";
    public static final String detect_other_youdao_appsec = "settings_detect_other_youdao_appsec";
    public static final String detect_other_baidu_key = "settings_detect_other_baidu_apikey";
    public static final String detect_other_baidu_appsec = "settings_detect_other_baidu_seckey";
    //设置组-本地tesseract的识别器(tess)
    public static final String detect_tess = "settings_detect_tess";
    public static final String detect_tess_model = "settings_detect_tess_detect_model";
    public static final String detect_tess_lang = "settings_detect_tess_lang";
    public static final String detect_tess_lang_sub = "settings_detect_tess_lang_sub";

    //设置组-特殊
    public static final String detect_continuousMode_interval = "settings_continuousMode_interval";
    public static final String detect_share_store = "settings_detect_share_store";
    //自动识别悬浮窗相应设置项
    //使用的api
    public static final String auto_api = "settings_auto_api";
    //设置组-单多悬浮窗的识别器(yuka)
    public static final String auto = "settings_auto";
    public static final String auto_model = "settings_auto_model";
    public static final String auto_toleration = "settings_auto_toleration";
    public static final String auto_punctuation = "settings_auto_punctuation";
    public static final String auto_vertical = "settings_auto_vertical";
    //设置组-单多悬浮窗的识别器(other)
    public static final String auto_other = "settings_auto_other";
    public static final String auto_other_model = "settings_auto_other_model";
    public static final String auto_other_reg_baidu = "settings_auto_other_reg_baidu";
    public static final String auto_other_reg_youdao = "settings_auto_other_reg_youdao";
    public static final String auto_other_toleration = "settings_auto_other_toleration";
    public static final String auto_other_punctuation = "settings_auto_other_punctuation";
    public static final String auto_other_vertical = "settings_auto_other_vertical";
    public static final String auto_other_youdao_key = "settings_auto_other_youdao_appkey";
    public static final String auto_other_youdao_appsec = "settings_auto_other_youdao_appsec";
    public static final String auto_other_baidu_key = "settings_auto_other_baidu_apikey";
    public static final String auto_other_baidu_appsec = "settings_auto_other_baidu_seckey";
    //设置组-共有
    public static final String auto_offset = "settings_auto_offset";

    //同步翻译悬浮窗相应设置项
    //使用的api
    public static final String sync_api = "settings_sync_api";
    //设置组-同步翻译悬浮窗(yuka)
    public static final String sync = "settings_sync";
    public static final String sync_provider = "settings_sync_syncProvider";
    public static final String sync_modes = "settings_sync_syncModes";
    public static final String sync_o = "settings_sync_sync_o";
    public static final String sync_t = "settings_sync_sync_t";
    //设置组-同步翻译悬浮窗(other)
    public static final String sync_other = "settings_sync_other";
    public static final String sync_other_provider = "settings_sync_other_syncProvider";
    public static final String sync_other_reg_youdao = "settings_sync_other_reg_youdao";
    public static final String sync_other_youdao_key = "settings_sync_other_youdao_appkey";
    public static final String sync_other_youdao_appsec = "settings_sync_other_youdao_appsec";
    public static final String sync_other_modes = "settings_sync_other_syncModes";
    public static final String sync_other_o = "settings_sync_other_sync_o";
    public static final String sync_other_t = "settings_sync_other_sync_t";
    //设置组-共有
    public static final String sync_findCompatible = "settings_sync_findCompatible";

    //翻译相应设置项
    //使用的api
    public static final String trans_api = "settings_trans_api";
    public static final String trans_api_auto = "settings_auto_trans_api";
    //设置组-翻译(yuka)
    public static final String translator = "settings_translator";
    public static final String trans_translator = "settings_trans_translator";
    public static final String trans_baidu_SBCS = "settings_baidu_SBCS";
    //设置组-翻译(other)
    public static final String translator_other = "settings_translator_other";
    public static final String trans_other_translator = "settings_trans_other_translator";
    public static final String trans_other_reg_baidu = "settings_trans_other_reg_baidu";
    public static final String trans_other_reg_youdao = "settings_trans_other_reg_youdao";
    public static final String trans_other_youdao_key = "settings_trans_other_youdao_appkey";
    public static final String trans_other_youdao_appsec = "settings_trans_other_youdao_appsec";
    public static final String trans_other_baidu_key = "settings_trans_other_baidu_apikey";
    public static final String trans_other_baidu_appsec = "settings_trans_other_baidu_seckey";
    public static final String trans_other_baidu_SBCS = "settings_trans_other_baidu_SBCS";

    //引导选项
    public static final String FIRST_GuideActivity = "first_open_guideActivity";
    public static final String FIRST_MainActivity = "first_open_mainActivity";
    public static final String FIRST_LOGIN = "first_Login";
    public static final String FIRST_FloatBall = "first_FloatBall";
    public static final String FIRST_SubtitleWindow = "first_SubtitleWindow";
    public static final String FIRST_SelectWindow_N_1 = "first_SWN_1";
    public static final String FIRST_SelectWindow_N_2 = "first_SWN_2";
    public static final String FIRST_SelectWindow_A = "first_SWA";
}
