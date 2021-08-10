package com.wzy.yukalite.config;


/**
 * Created by Ziyan on 2020/8/11.
 */
public class YukaConfig {
    public static final String api_version = "yuka_v1.1";
    public String mode;
    public String model;
    public String translator;
    public boolean SBCS;
    public boolean punctuation;
    public boolean vertical;
    public int toleration;

    private YukaConfig(Builder builder) {
        this.mode = builder.mode;
        this.model = builder.model;
        this.translator = builder.translator;
        this.SBCS = builder.SBCS;
        this.punctuation = builder.punctuation;
        this.vertical = builder.vertical;
        this.toleration = builder.toleration;
    }

    public static class Builder {
        private String mode = Mode.translate.name();
        private String model = Model.youdao.name();
        private String translator = Translator.youdao.name();
        private boolean SBCS = false;
        private boolean punctuation = false;
        private boolean vertical = false;
        private int toleration = 15;

        public Builder setMode(Mode mode) {
            this.mode = mode.name();
            return this;
        }

        public Builder setOCR_Google(boolean vertical) {
            this.model = Model.google.name();
            this.vertical = vertical;
            return this;
        }

        public Builder setOCR_Baidu(boolean punctuation, boolean vertical) {
            this.model = Model.baidu.name();
            this.punctuation = punctuation;
            this.vertical = vertical;
            return this;
        }

        public Builder setOCR_Youdao(boolean punctuation) {
            this.model = Model.youdao.name();
            this.punctuation = punctuation;
            return this;
        }

        public Builder setAutoOCR_Google(boolean vertical, int toleration) {
            this.toleration = toleration;
            return setOCR_Google(vertical);
        }

        public Builder setAutoOCR_Youdao(boolean punctuation, int toleration) {
            this.toleration = toleration;
            return setOCR_Youdao(punctuation);
        }

        public Builder setAutoOCR_Baidu(boolean punctuation, boolean vertical, int toleration) {
            this.toleration = toleration;
            return setOCR_Baidu(punctuation, vertical);
        }

        public Builder setTranslator_Youdao() {
            this.translator = Translator.youdao.name();
            return this;
        }

        public Builder setTranslator_Tencent() {
            this.translator = Translator.tencent.name();
            return this;
        }

        public Builder setTranslator_Google() {
            this.translator = Translator.google.name();
            return this;
        }

        public Builder setTranslator_Baidu(boolean SBCS) {
            this.translator = Translator.baidu.name();
            this.SBCS = SBCS;
            return this;
        }

        public YukaConfig build() {
            return new YukaConfig(this);
        }
    }


}
