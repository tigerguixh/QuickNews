
package com.tiger.quicknews.http.json;

import android.content.Context;

import com.tiger.quicknews.bean.PhotoModle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PhotoListJson extends JsonPacket {

    public List<PhotoModle> photoModles = new ArrayList<PhotoModle>();

    public static PhotoListJson photoListJson;

    public PhotoListJson(Context context) {
        super(context);
    }

    public static PhotoListJson instance(Context context) {
        if (photoListJson == null) {
            photoListJson = new PhotoListJson(context);
        }
        return photoListJson;
    }

    public List<PhotoModle> readJsonPhotoListModles(String res) {
        photoModles.clear();
        try {
            if (res == null || res.equals("")) {
                return null;
            }
            PhotoModle photoModle = null;
            JSONArray jsonArray = new JSONArray(res);
            for (int i = 0; i < jsonArray.length(); i++) {
                photoModle = readJsonPhotoModle(jsonArray.getJSONObject(i));
                photoModles.add(photoModle);
            }
        } catch (Exception e) {

        } finally {
            System.gc();
        }
        return photoModles;
    }

    private PhotoModle readJsonPhotoModle(JSONObject jsonObject) throws Exception {

        PhotoModle photoModle = null;

        String setid = "";
        String seturl = "";
        String clientcover = "";
        String setname = "";

        setid = getString("setid", jsonObject);
        seturl = getString("seturl", jsonObject);
        clientcover = getString("clientcover1", jsonObject);
        setname = getString("datetime", jsonObject);

        setname = setname.split(" ")[0];

        photoModle = new PhotoModle();

        photoModle.setClientcover(clientcover);
        photoModle.setSetid(setid);
        photoModle.setSetname(setname);
        photoModle.setSeturl(seturl);

        return photoModle;
    }

}
