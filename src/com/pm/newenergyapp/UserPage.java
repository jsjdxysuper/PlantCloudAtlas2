package com.pm.newenergyapp;

/**
 * Created by ding on 2018/9/14.
 */

public class UserPage {
    private String id;
    private String  pagename;
    private String pageUrl;
    private int order_int;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPagename() {
        return pagename;
    }
    public void setPagename(String pagename) {
        this.pagename = pagename;
    }
    public String getPageUrl() {
        return pageUrl;
    }
    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }
    public int getOrder_int() {
        return order_int;
    }
    public void setOrder_int(int order_int) {
        this.order_int = order_int;
    }
}
