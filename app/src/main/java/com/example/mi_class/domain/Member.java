package com.example.mi_class.domain;

public class Member {
    private String name;
    private String style;
    private  String code;

    public Member(String name, String style,String code){
        this.name = name;
        this.style = style;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getStyle() {
        return style;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
