package com.example.mi_class.domain;

import android.graphics.Bitmap;

public class Message {
    private String name;    //人名或课程名
    private String last_message;    //最后发送的信息
    private int time;   //发送时间
    private Bitmap head_portrait;   //头像


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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Bitmap getHead_portrait() {
        return head_portrait;
    }

    public void setHead_portrait(Bitmap head_portrait) {
        this.head_portrait = head_portrait;
    }

}
