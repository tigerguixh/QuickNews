
package com.tiger.quicknews.bean;

public class PhotoDetailModle extends BaseModle {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * title
     */
    private String title;
    /**
     * content
     */
    private String content;
    /**
     * imgUrl
     */
    private String imgUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
