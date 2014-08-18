
package com.tiger.quicknews.bean;

public class PhotoModle extends BaseModle {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * setid
     */
    private String setid;
    /**
     * seturl
     */
    private String seturl;
    /**
     * clientcover
     */
    private String clientcover;
    /**
     * setname
     */
    private String setname;

    public String getSetid() {
        return setid;
    }

    public void setSetid(String setid) {
        this.setid = setid;
    }

    public String getSeturl() {
        return seturl;
    }

    public void setSeturl(String seturl) {
        this.seturl = seturl;
    }

    public String getClientcover() {
        return clientcover;
    }

    public void setClientcover(String clientcover) {
        this.clientcover = clientcover;
    }

    public String getSetname() {
        return setname;
    }

    public void setSetname(String setname) {
        this.setname = setname;
    }
}
