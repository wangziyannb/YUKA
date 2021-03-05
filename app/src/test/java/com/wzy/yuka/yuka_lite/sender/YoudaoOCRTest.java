package com.wzy.yuka.yuka_lite.sender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ziyan on 2021/2/1.
 */
public class YoudaoOCRTest {
    int toleration = 15;
    boolean punctuation = false;
    ArrayList<String> lang = new ArrayList<>();

    @Test
    public void addition_isCorrect() {
        String str = "{\"requestId\":\"c97a176a-ba1b-4156-86a2-49a5a95f0a82\",\"errorCode\":\"0\",\"Result\":{\"orientation\":\"UP\",\"regions\":[{\"boundingBox\":\"47,50,778,50,778,137,47,137\",\"dir\":\"h\",\"lang\":\"en\",\"lines\":[{\"boundingBox\":\"62,50,778,50,778,87,62,87\",\"text_height\":33,\"words\":[{\"boundingBox\":\"62,50,119,50,119,83,62,83\",\"word\":\"love\"},{\"boundingBox\":\"136,51,202,51,202,84,136,84\",\"word\":\"three\"},{\"boundingBox\":\"218,51,301,51,301,84,218,84\",\"word\":\"things\"},{\"boundingBox\":\"309,51,333,51,333,84,309,84\",\"word\":\"in\"},{\"boundingBox\":\"350,51,391,51,391,84,350,84\",\"word\":\"this\"},{\"boundingBox\":\"408,51,556,51,556,84,408,84\",\"word\":\"world.Sun,\"},{\"boundingBox\":\"564,51,638,51,638,84,564,84\",\"word\":\"moon\"},{\"boundingBox\":\"655,51,696,52,696,85,655,84\",\"word\":\"and\"},{\"boundingBox\":\"721,53,778,54,778,87,721,86\",\"word\":\"you.\"}],\"text\":\"love three things in this world.Sun, moon and you.\",\"lang\":\"en\"},{\"boundingBox\":\"47,100,748,100,748,137,47,137\",\"text_height\":35,\"words\":[{\"boundingBox\":\"47,100,99,100,99,135,47,135\",\"word\":\"Sun\"},{\"boundingBox\":\"107,101,151,101,151,136,107,136\",\"word\":\"for\"},{\"boundingBox\":\"159,101,289,102,289,137,159,136\",\"word\":\"morning,\"},{\"boundingBox\":\"298,102,376,102,376,137,298,137\",\"word\":\"moon\"},{\"boundingBox\":\"385,102,428,102,428,137,385,137\",\"word\":\"for\"},{\"boundingBox\":\"437,102,567,102,567,137,437,137\",\"word\":\"night,and\"},{\"boundingBox\":\"584,102,627,102,627,137,584,137\",\"word\":\"you\"},{\"boundingBox\":\"645,102,748,102,748,137,645,137\",\"word\":\"forever.\"}],\"text\":\"Sun for morning, moon for night,and you forever.\",\"lang\":\"en\"}]}],\"exif\":\"UP\",\"scene\":\"other\"}}\n";
        try {
            JSONArray array = new JSONObject(str).getJSONObject("Result").getJSONArray("regions");
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
            System.out.println(lang);
            System.out.println(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
                count.replace(lan, count.get(lan) + 1);
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