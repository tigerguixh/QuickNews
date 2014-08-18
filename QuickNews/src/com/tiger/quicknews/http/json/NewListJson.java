
package com.tiger.quicknews.http.json;

import android.content.Context;

import com.tiger.quicknews.bean.ImagesModle;
import com.tiger.quicknews.bean.NewModle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewListJson extends JsonPacket {

    public static NewListJson newListJson;

    public List<NewModle> newModles;

    public NewListJson(Context context) {
        super(context);
    }

    public static NewListJson instance(Context context) {
        if (newListJson == null) {
            newListJson = new NewListJson(context);
        }
        return newListJson;
    }

    public List<NewModle> readJsonNewModles(String res, String value) {
        newModles = new ArrayList<NewModle>();
        try {
            if (res == null || res.equals("")) {
                return null;
            }
            NewModle newModle = null;
            JSONObject jsonObject = new JSONObject(res);
            JSONArray jsonArray = jsonObject.getJSONArray(value);
            // if (isFirst) {
            // for (int i = 0; i < 4; i++) {
            // JSONObject js = jsonArray.getJSONObject(i);
            // newModle = readNewModle(js);
            // newModles.add(newModle);
            // }
            // }

            for (int i = 1; i < jsonArray.length(); i++) {
                newModle = new NewModle();
                JSONObject js = jsonArray.getJSONObject(i);
                if (js.has("skipType") && js.getString("skipType").equals("special")) {
                    continue;
                }
                if (js.has("TAGS") && !js.has("TAG")) {
                    continue;
                }
                if (js.has("imgextra")) {
                    newModle.setTitle(getString("title", js));
                    newModle.setDocid(getString("docid", js));
                    ImagesModle imagesModle = new ImagesModle();
                    List<String> list;
                    list = readImgList(js.getJSONArray("imgextra"));
                    list.add(getString("imgsrc", js));
                    imagesModle.setImgList(list);
                    newModle.setImagesModle(imagesModle);
                } else {
                    newModle = readNewModle(js);
                }
                newModles.add(newModle);
            }
        } catch (Exception e) {

        } finally {
            System.gc();
        }
        return newModles;
    }

    /**
     * 解析图片集
     * 
     * @param jsonArray
     * @return
     * @throws Exception
     */
    public List<String> readImgList(JSONArray jsonArray) throws Exception {
        List<String> imgList = new ArrayList<String>();

        for (int i = 0; i < jsonArray.length(); i++) {
            imgList.add(getString("imgsrc", jsonArray.getJSONObject(i)));
        }

        return imgList;
    }

    /**
     * 获取图文列表
     * 
     * @param jsonObject
     * @return
     * @throws Exception
     */
    public NewModle readNewModle(JSONObject jsonObject) throws Exception {
        NewModle newModle = null;

        String docid = "";
        String title = "";
        String digest = "";
        String imgsrc = "";
        String source = "";
        String ptime = "";
        String tag = "";

        docid = getString("docid", jsonObject);
        title = getString("title", jsonObject);
        digest = getString("digest", jsonObject);
        imgsrc = getString("imgsrc", jsonObject);
        source = getString("source", jsonObject);
        ptime = getString("ptime", jsonObject);
        tag = getString("TAG", jsonObject);

        newModle = new NewModle();

        newModle.setDigest(digest);
        newModle.setDocid(docid);
        newModle.setImgsrc(imgsrc);
        newModle.setTitle(title);
        newModle.setPtime(ptime);
        newModle.setSource(source);
        newModle.setTag(tag);

        return newModle;
    }

}
