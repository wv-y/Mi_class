package com.example.mi_class.domain;

public class SignIn {
    private String sign_in_name;
    private String sign_in_time;
    private String sign_in_style;

    public SignIn(String sign_in_name,String sign_in_tiem, String sign_in_style){
        this.sign_in_name = sign_in_name;
        this.sign_in_time = sign_in_tiem;
        this.sign_in_style = sign_in_style;
    }

    public String getSign_in_name() {
        return sign_in_name;
    }

    public String getSign_in_time() {
        return sign_in_time;
    }

    public String getSign_in_style() {
        return sign_in_style;
    }

    public void setSign_in_name(String sign_in_name) {
        this.sign_in_name = sign_in_name;
    }

    public void setSign_in_time(String sign_in_time) {
        this.sign_in_time = sign_in_time;
    }

    public void setSign_in_style(String sign_in_style) {
        this.sign_in_style = sign_in_style;
    }
}

