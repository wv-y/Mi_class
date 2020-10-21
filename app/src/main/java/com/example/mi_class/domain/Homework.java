package com.example.mi_class.domain;

public class Homework {
    private String title;
    private String detail;
    private long pubtime;
    private long subtime;

    public Homework(String title,String detail, long pubtime,long subtime){
        this.title = title;
        this.detail = detail;
        this.pubtime = pubtime;
        this.subtime = subtime;

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public long getPubtime() {
        return pubtime;
    }

    public void setPubtime(long pubtime) {
        this.pubtime = pubtime;
    }

    public long getSubtime() {
        return subtime;
    }

    public void setSubtime(long subtime) {
        this.subtime = subtime;
    }
}
