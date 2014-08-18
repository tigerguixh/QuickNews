
package com.tiger.quicknews.bean;

public class VideoModle extends BaseModle {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * vid
     */
    private String vid;
    /**
     * title
     */
    private String title;
    /**
     * length
     */
    private String length;
    /**
     * cover
     */
    private String cover;
    /**
     * mp4Hd_url
     */
    private String mp4Hd_url;

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getMp4Hd_url() {
        return mp4Hd_url;
    }

    public void setMp4Hd_url(String mp4Hd_url) {
        this.mp4Hd_url = mp4Hd_url;
    }
}
