package com.example.mi_class.domain;

public class SignIn {
    private String sign_name;
    private String end_time;
    private String start_time;
    private String way;
    private String sign_style;
    private String size;
    private String value;

    public SignIn (){}

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    public void setSign_name(String sign_name) {
        this.sign_name = sign_name;
    }

    public String getSign_name() {
        return sign_name;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getWay() {
        return way;
    }

    public void setSign_style(String sign_style) {
        this.sign_style = sign_style;
    }

    public String getSign_style() {
        return sign_style;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

