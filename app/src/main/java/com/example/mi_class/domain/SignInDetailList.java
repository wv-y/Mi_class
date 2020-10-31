package com.example.mi_class.domain;


import javax.xml.namespace.QName;

public class SignInDetailList {
    private int image;
    private String  name;
    private String  id;
    private String  style;
    private String phone;

    public SignInDetailList(){}

    public SignInDetailList(int image, String name,String id,String style){
        this.image = image;
        this.name = name;
        this.id = id;
        this.style = style;
    }
    public void setImage(int image){
        this.image = image;
    }

    public int getImage(){
        return this.image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
