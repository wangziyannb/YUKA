package com.wzy.yuka.yuka_lite.sender;


import android.util.Base64;

import com.wzy.yuka.tools.params.Encrypt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Ziyan on 2021/2/1.
 */
public class YoudaoOCR {

    public static void request(String APP_KEY, String APP_SECRET, File[] file, Callback[] callbacks) {
        if (file.length == callbacks.length) {
            for (int i = 0; i < file.length; i++) {
                request(APP_KEY, APP_SECRET, file[i], callbacks[i]);
            }
        }
    }

    //可以同时应用于普通和自动模式
    public static void request(String APP_KEY, String APP_SECRET, File file, Callback callback) {
        String img = loadAsBase64(file);
        String YOUDAO_URL = "https://openapi.youdao.com/ocrapi";

        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        String detectType = "10012";
        String imageType = "1";
        String langType = "auto";
        String docType = "json";
        String signType = "v3";
        String salt = UUID.randomUUID() + "";
        String sign = Encrypt.sha256(APP_KEY + truncate(img) + salt + curtime + APP_SECRET);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .readTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .writeTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .build();

        RequestBody body = new FormBody.Builder()
                .add("img", img)
                .add("langType", langType)
                .add("detectType", detectType)
                .add("imageType", imageType)
                .add("curtime", curtime)
                .add("appKey", APP_KEY)
                .add("docType", docType)
                .add("signType", signType)
                .add("salt", salt)
                .add("sign", sign)
                .build();
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(YOUDAO_URL)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static String single(String response, boolean punctuation) throws JSONException {
        JSONArray array = new JSONObject(response).getJSONObject("Result").getJSONArray("regions");
        StringBuilder origin = new StringBuilder();
        for (int i = 0; i < array.length(); i++) {
            JSONArray array1 = array.getJSONObject(i).getJSONArray("lines");
            for (int j = 0; j < array1.length(); j++) {
                origin.append(array1.getJSONObject(j).getString("text"));
                if (punctuation) {
                    origin.append(" ");
                }
            }
        }
        return origin.toString();
    }

    public static String auto(String response, boolean punctuation, int toleration) throws JSONException {
        youdaoJsonBuilder builder = new youdaoJsonBuilder(response, punctuation, toleration);
        return builder.build().toString();
    }

    public static String loadAsBase64(File file) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        if (!file.exists()) {
            return null;
        }
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(file);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(data, Base64.DEFAULT);//返回Base64编码过的字节数组字符串
    }

    private static String truncate(String q) {
        if (q == null) {
            return null;
        }
        int len = q.length();
        return len <= 20 ? q : (q.substring(0, 10) + len + q.substring(len - 10, len));
    }

    private static class youdaoJsonBuilder {
        String origin_dict;
        boolean punctuation;
        int toleration;
        //后期得猜语言
        ArrayList<String> lang = new ArrayList<>();

        public youdaoJsonBuilder(String origin_dict, boolean punctuation, int toleration) {
            this.origin_dict = origin_dict;
            this.punctuation = punctuation;
            this.toleration = toleration;
        }

        public JSONObject build() throws JSONException {
            JSONArray array = new JSONObject(origin_dict).getJSONObject("Result").getJSONArray("regions");
            int j = 0;
            ArrayList<ArrayList<Object>> final_blocks = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject js = array.getJSONObject(i);
                //dir是方向-行，列
                String dir = js.getString("dir");
                //lines是具体的识别内容
                JSONArray lines = js.getJSONArray("lines");
                ArrayList<ArrayList<Object>> blocks = new ArrayList<>();

                for (int k = 0; k < lines.length(); k++) {
                    JSONObject l = lines.getJSONObject(k);
                    String[] box_l = l.getString("boundingBox").split(",");
                    lang.add(l.getString("lang"));
                    ArrayList<Object> b_l = new ArrayList<>();
                    b_l.add(Integer.parseInt(box_l[0]));
                    b_l.add(Integer.parseInt(box_l[1]));
                    b_l.add(Integer.parseInt(box_l[4]));
                    b_l.add(Integer.parseInt(box_l[5]));
                    ArrayList<String> text = new ArrayList<>();
                    text.add(l.getString("text"));
                    ArrayList<Integer> index = new ArrayList<>();
                    index.add(j);
                    b_l.add(text);
                    b_l.add(index);
                    classify(b_l, dir, blocks);
                    j += 1;
                }
                final_blocks.addAll(blocks);
            }
            String language = guess_language();
            int i = 0;
            ArrayList<Object> json = new ArrayList<>();
            for (ArrayList<Object> b : final_blocks) {
                json.add(dump(i,
                        new int[]{(int) b.get(0), (int) b.get(1), (int) b.get(2), (int) b.get(3)},
                        append_text((ArrayList<String>) b.get(4), language.equals("en"))));
                i += 1;
            }
            JSONObject result = new JSONObject();
            result.put("values", new JSONArray(json));
            return result;
        }

        /**
         * param b_l: [left,top,right,bottom,description,index]
         * param dir: 列：v/行：h
         * param blocks: 已整合的blocks
         * return: 整合后的blocks
         */
        public void classify(ArrayList<Object> b_l, String dir, ArrayList<ArrayList<Object>> blocks) {
            if (blocks.size() == 0) {
                blocks.add(b_l);
            } else {
                if (dir.equals("v")) {
                    for (ArrayList<Object> b : blocks) {
                        if ((((int) b.get(0) - toleration) <= (int) b_l.get(2))
                                && ((int) b_l.get(2) <= ((int) b.get(2) + toleration))) {
                            if ((((int) b.get(1) <= (int) b_l.get(1)) && ((int) b_l.get(1) <= (int) b.get(3)))
                                    || (((int) b.get(1) <= (int) b_l.get(3)) && ((int) b_l.get(3) <= (int) b.get(3)))
                                    || (((int) b.get(3) < (int) b_l.get(3)) && ((int) b.get(1) > (int) b_l.get(1)))) {
                                b = merge(b_l, b);
                                return;
                            }
                        }
                    }
                    blocks.add(b_l);
                } else if (dir.equals("h")) {
                    for (ArrayList<Object> b : blocks) {
                        if ((((int) b.get(1) - toleration) <= (int) b_l.get(1))
                                && ((int) b_l.get(1) <= ((int) b.get(3) + toleration))) {
                            if ((((int) b.get(1) <= (int) b_l.get(0)) && ((int) b_l.get(0) <= (int) b.get(2)))
                                    || (((int) b.get(0) <= (int) b_l.get(2)) && ((int) b_l.get(2) <= (int) b.get(2)))
                                    || (((int) b.get(2) < (int) b_l.get(2)) && ((int) b.get(0) > (int) b_l.get(0)))) {
                                b = merge(b_l, b);
                                return;
                            }
                        }
                    }
                    blocks.add(b_l);
                }
            }
        }

        public ArrayList<Object> merge(ArrayList<Object> l_new, ArrayList<Object> l_blk) {
            l_blk.set(0, Math.min((int) l_blk.get(0), (int) l_new.get(0)));
            l_blk.set(1, Math.min((int) l_blk.get(1), (int) l_new.get(1)));
            l_blk.set(2, Math.max((int) l_blk.get(2), (int) l_new.get(2)));
            l_blk.set(3, Math.max((int) l_blk.get(3), (int) l_new.get(3)));
            ((ArrayList<String>) l_blk.get(4)).addAll((ArrayList<String>) l_new.get(4));
            ((ArrayList<Integer>) l_blk.get(5)).addAll((ArrayList<Integer>) l_new.get(5));
            return l_blk;
        }

        public String guess_language() {
            HashMap<String, Integer> count = new HashMap<>();
            String language;
            for (String lan : lang) {
                if (count.containsKey(lan)) {
                    count.put(lan, count.get(lan) + 1);
                } else {
                    count.put(lan, 1);
                }
            }
            if (count.containsKey("en") && count.containsKey("jp")) {
                if (count.get("en") * 3 > count.get("jp")) {
                    language = "en";
                } else {
                    language = "jp";
                }
            } else {
                int max = 0;
                String key = "en";
                for (Map.Entry<String, Integer> entry : count.entrySet()) {
                    if (entry.getValue() > max) {
                        max = entry.getValue();
                        key = entry.getKey();
                    }
                }
                language = key;
            }
            return language;
        }

        public HashMap<String, Object> dump(int i, int[] location, String words) {
            HashMap<String, Object> src = new HashMap<>();
            src.put("location", location);
            src.put("words", words);
            HashMap<String, Object> dic = new HashMap<>();
            dic.put("index", i);
            dic.put("src", src);
            return dic;
        }

        public String append_text(ArrayList<String> w, boolean is_eng) {
            StringBuilder result = new StringBuilder();
            if (is_eng) {
                for (String ws : w) {
                    result.append(ws).append(" ");
                }
            } else if (punctuation) {
                for (String ws : w) {
                    result.append(ws).append("、");
                }
            } else {
                for (String ws : w) {
                    result.append(ws);
                }
            }
            return result.toString();
        }

    }
}

