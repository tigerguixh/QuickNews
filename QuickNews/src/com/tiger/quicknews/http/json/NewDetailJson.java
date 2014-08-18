
package com.tiger.quicknews.http.json;

import android.content.Context;

import com.tiger.quicknews.bean.NewDetailModle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewDetailJson extends JsonPacket {

    public static NewDetailJson newDetailJson;

    public NewDetailModle newDetailModle;

    public NewDetailJson(Context context) {
        super(context);
    }

    public static NewDetailJson instance(Context context) {
        if (newDetailJson == null) {
            newDetailJson = new NewDetailJson(context);
        }
        return newDetailJson;
    }

    public NewDetailModle readJsonNewModles(String res, String newId) {
        try {
            if (res == null || res.equals("")) {
                return null;
            }
            JSONObject jsonObject = new JSONObject(res).getJSONObject(newId);
            newDetailModle = readNewModle(jsonObject);
        } catch (Exception e) {

        } finally {
            System.gc();
        }
        return newDetailModle;
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
            imgList.add(getString("src", jsonArray.getJSONObject(i)));
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
    public NewDetailModle readNewModle(JSONObject jsonObject) throws Exception {
        NewDetailModle newDetailModle = null;

        String docid = "";
        String title = "";
        String source = "";
        String ptime = "";
        String body = "";
        String url_mp4 = "";
        String cover = "";

        docid = getString("docid", jsonObject);
        title = getString("title", jsonObject);
        source = getString("source", jsonObject);
        ptime = getString("ptime", jsonObject);
        body = getString("body", jsonObject);

        if (jsonObject.has("video")) {
            JSONObject jsonObje = jsonObject.getJSONArray("video").getJSONObject(0);
            url_mp4 = getString("url_mp4", jsonObje);
            cover = getString("cover", jsonObje);
        }

        JSONArray jsonArray = jsonObject.getJSONArray("img");

        List<String> imgList = readImgList(jsonArray);

        newDetailModle = new NewDetailModle();

        newDetailModle.setDocid(docid);
        newDetailModle.setImgList(imgList);
        newDetailModle.setPtime(ptime);
        newDetailModle.setSource(source);
        newDetailModle.setTitle(title);
        newDetailModle.setBody(body);
        newDetailModle.setUrl_mp4(url_mp4);
        newDetailModle.setCover(cover);

        return newDetailModle;
    }

}
