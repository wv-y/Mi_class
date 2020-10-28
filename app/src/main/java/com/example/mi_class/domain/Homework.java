package com.example.mi_class.domain;

public class Homework {
    private String title;
    private String detail;
    private String pubtime;
    private String subtime;
    private int filenumber;
    private int state; //截止和提交，    t未截止0，已截止1, s未截止-未提交2，s未截止-已提交3，s已截止-未提交4, s已截止-已提交4

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getFilenumber() {
        return filenumber;
    }

    public void setFilenumber(int filenumber) {
        this.filenumber = filenumber;
    }

    public Homework(){}
    public Homework(String title,String detail, String pubtime,String subtime,int filenumber){
        this.title = title;
        this.detail = detail;
        this.pubtime = pubtime;
        this.subtime = subtime;
        this.filenumber = filenumber;

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

    public String getPubtime() {
        return pubtime;
    }

    public void setPubtime(String pubtime) {
        this.pubtime = pubtime;
    }

    public String getSubtime() {
        return subtime;
    }

    public void setSubtime(String subtime) {
        this.subtime = subtime;
    }
}
