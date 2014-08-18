
package com.tiger.quicknews.bean;

import java.util.List;

public class ImagesModle extends BaseModle {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * docid
     */
    private String docid;
    /**
     * title
     */
    private String title;
    /**
     * Í¼Æ¬¼¯
     */
    private List<String> imgList;

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }
}
