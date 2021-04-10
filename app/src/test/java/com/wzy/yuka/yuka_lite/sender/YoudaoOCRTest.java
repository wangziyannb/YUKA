package com.wzy.yuka.yuka_lite.sender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by Ziyan on 2021/2/1.
 */
public class YoudaoOCRTest {
    @Test
    public void addition_isCorrect() {
        String json = "{\"values\":[{\"src\":{\"words\":\"We get old and get used to each other. We think alike. We read each others minds. We know what the other wants without asking. Sometimes we irritate \",\"location\":[34,28,1156,115]},\"index\":0},{\"src\":{\"words\":\"each other a little bit. Maybe sometimes take each other for granted. But once in awhile, like today, I meditate on it and realise how lucky I am to share my life \",\"location\":[34,131,1165,220]},\"index\":1},{\"src\":{\"words\":\"with the greatest woman I ever met \",\"location\":[35,236,539,270]},\"index\":2},{\"src\":{\"words\":\"「はじめの一歩」の鴨川源二の名言で、成功者は才能ではなく必ず並々ならぬ努力を \",\"location\":[35,336,1157,373]},\"index\":3},{\"src\":{\"words\":\"行った結果であるというメッセージが込められています。 \",\"location\":[35,389,782,422]},\"index\":4},{\"src\":{\"words\":\"헤여졌다한들슬퍼하지마. 이후에만나게될더좋은사람을위해항상웃는얼굴잃지말자. \",\"location\":[35,476,1117,512]},\"index\":5},{\"src\":{\"words\":\"I love three things in this world.Sun, moon and you. Sun for morning, moon for night,and you forever. \",\"location\":[35,573,760,663]},\"index\":6}]}";
        try {
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("values");
            String[] sa = new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject src = array.getJSONObject(i).getJSONObject("src");
                sa[i] = src.getString("words");
            }
            System.out.println(Arrays.toString(sa));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}