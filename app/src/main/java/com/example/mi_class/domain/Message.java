package com.example.mi_class.domain;

import android.graphics.Bitmap;

public class Message {
    public static final int TYPE_SEND = 0;
    public static final int TYPE_RECEIVE = 1;
    private String name;    //人名或课程名
    private String last_message;    //最后发送的信息
    private long time;   //发送时间
    private Bitmap head_portrait;   //头像
    private int type;   //类型
    private int unReadCnt;

    public int getUnReadCnt() {
        return unReadCnt;
    }

    public void setUnReadCnt(int unReadCnt) {
        this.unReadCnt = unReadCnt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Bitmap getHead_portrait() {
        return head_portrait;
    }

    public void setHead_portrait(Bitmap head_portrait) {
        this.head_portrait = head_portrait;
    }

}
