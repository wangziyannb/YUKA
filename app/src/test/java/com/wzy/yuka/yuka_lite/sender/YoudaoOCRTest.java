package com.wzy.yuka.yuka_lite.sender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

/**
 * Created by Ziyan on 2021/2/1.
 */
public class YoudaoOCRTest {
    @Test
    public void addition_isCorrect() {
        System.out.println();
        String str = "{\"requestId\":\"c97a176a-ba1b-4156-86a2-49a5a95f0a82\",\"errorCode\":\"0\",\"Result\":{\"orientation\":\"UP\",\"regions\":[{\"boundingBox\":\"47,50,778,50,778,137,47,137\",\"dir\":\"h\",\"lang\":\"en\",\"lines\":[{\"boundingBox\":\"62,50,778,50,778,87,62,87\",\"text_height\":33,\"words\":[{\"boundingBox\":\"62,50,119,50,119,83,62,83\",\"word\":\"love\"},{\"boundingBox\":\"136,51,202,51,202,84,136,84\",\"word\":\"three\"},{\"boundingBox\":\"218,51,301,51,301,84,218,84\",\"word\":\"things\"},{\"boundingBox\":\"309,51,333,51,333,84,309,84\",\"word\":\"in\"},{\"boundingBox\":\"350,51,391,51,391,84,350,84\",\"word\":\"this\"},{\"boundingBox\":\"408,51,556,51,556,84,408,84\",\"word\":\"world.Sun,\"},{\"boundingBox\":\"564,51,638,51,638,84,564,84\",\"word\":\"moon\"},{\"boundingBox\":\"655,51,696,52,696,85,655,84\",\"word\":\"and\"},{\"boundingBox\":\"721,53,778,54,778,87,721,86\",\"word\":\"you.\"}],\"text\":\"love three things in this world.Sun, moon and you.\",\"lang\":\"en\"},{\"boundingBox\":\"47,100,748,100,748,137,47,137\",\"text_height\":35,\"words\":[{\"boundingBox\":\"47,100,99,100,99,135,47,135\",\"word\":\"Sun\"},{\"boundingBox\":\"107,101,151,101,151,136,107,136\",\"word\":\"for\"},{\"boundingBox\":\"159,101,289,102,289,137,159,136\",\"word\":\"morning,\"},{\"boundingBox\":\"298,102,376,102,376,137,298,137\",\"word\":\"moon\"},{\"boundingBox\":\"385,102,428,102,428,137,385,137\",\"word\":\"for\"},{\"boundingBox\":\"437,102,567,102,567,137,437,137\",\"word\":\"night,and\"},{\"boundingBox\":\"584,102,627,102,627,137,584,137\",\"word\":\"you\"},{\"boundingBox\":\"645,102,748,102,748,137,645,137\",\"word\":\"forever.\"}],\"text\":\"Sun for morning, moon for night,and you forever.\",\"lang\":\"en\"}]}],\"exif\":\"UP\",\"scene\":\"other\"}}\n";
        try {
            JSONArray array = new JSONObject(str).getJSONObject("Result").getJSONArray("regions");
            StringBuilder fin = new StringBuilder();
            for (int i = 0; i < array.length(); i++) {
                JSONArray array1 = array.getJSONObject(i).getJSONArray("lines");
                for (int j = 0; j < array1.length(); j++) {
                    fin.append(array1.getJSONObject(j).getString("text"));
                    fin.append(" ");
                }
            }
            System.out.println(fin.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}