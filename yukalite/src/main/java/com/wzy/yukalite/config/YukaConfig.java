package com.wzy.yukalite.config;

/**
 * Created by Ziyan on 2020/8/11.
 */
public class YukaConfig {
    public static final String api_version = "yuka_v1";
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

        public Builder setOCR(Model model, boolean punctuation, boolean vertical) {
            this.model = model.name();
            this.punctuation = punctuation;
            this.vertical = vertical;
            return this;
        }

        public Builder setOCR(Model model, boolean punctuation, boolean vertical, int toleration) {
            this.model = model.name();
            this.punctuation = punctuation;
            this.vertical = vertical;
            this.toleration = toleration;
            return this;
        }

        public Builder setTranslator(Translator translator) {
            this.translator = translator.name();
            return this;
        }

        public Builder setTranslator(Translator translator, boolean SBCS) {
            this.translator = translator.name();
            this.SBCS = SBCS;
            return this;
        }

        public YukaConfig build() {
            return new YukaConfig(this);
        }
    }


}
