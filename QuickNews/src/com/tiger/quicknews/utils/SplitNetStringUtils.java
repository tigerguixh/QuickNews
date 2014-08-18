
package com.tiger.quicknews.utils;

import com.tiger.quicknews.bean.PhotoDetailModle;

import java.util.ArrayList;
import java.util.List;

public class SplitNetStringUtils {

    public static List<PhotoDetailModle> lists = new ArrayList<PhotoDetailModle>();

    public static List<PhotoDetailModle> getPhotoDetailModles(String result) {
        lists.clear();
        PhotoDetailModle photoDetailModle = null;

        String teString = result.split("<textarea class")[1];
        teString = teString.substring(teString.indexOf("<li>"),
                teString.lastIndexOf("<div id=\"galleryTpl\" class=\"hidden\">"));
        String[] restu = teString.split("<a href=\"#p=");
        for (int i = 1; i < restu.length; i++) {
            photoDetailModle = new PhotoDetailModle();
            String data =
                    restu[i].substring(restu[i].indexOf("<i title=\"img\">"),
                            restu[i].lastIndexOf("<i title=\"timg\">"));
            String imgUrl = data.split("</i>")[0].split("<i title=\"img\">")[1];
            String titleString = restu[i].substring(restu[i].indexOf("alt=\" "),
                    restu[i].lastIndexOf("\" /></a>"));
            String contentString = restu[i].substring(restu[i].indexOf("<p>"),
                    restu[i].lastIndexOf("</p>"));
            photoDetailModle.setImgUrl(imgUrl);
            if (titleString.split("alt=\" ")[1].split(" ").length > 1) {
                titleString = titleString.split("alt=\" ")[1].split(" ")[1];
            } else {
                titleString = titleString.split("alt=\" ")[1].split(" ")[0];
            }
            photoDetailModle.setTitle(titleString);
            if (contentString.split("<p>").length > 0) {
                photoDetailModle.setContent(contentString.split("<p>")[1]);
            } else {
                photoDetailModle.setContent("");
            }
            lists.add(photoDetailModle);
        }

        return lists;
    }
}
