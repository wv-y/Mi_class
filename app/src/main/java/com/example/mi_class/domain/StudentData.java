package com.example.mi_class.domain;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONException;
import org.json.JSONObject;

public class StudentData implements User {
    private String stu_name;
    private String sex;
    private String stu_phone;
    private String stu_id;
    private int school_id;
    private int pic_id;

    public StudentData(){

    }
    public StudentData(String name,String id,int portrait){
        this.stu_name = name;
        this.stu_id = id;
        this.pic_id = portrait;
    }

    @Override
    public void SetUser(Context context) {
        SharedPreferences pf = context.getSharedPreferences("user_login_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pf.edit();
        ed.putString("name",this.stu_name);
        ed.putString("gender",this.sex);
        ed.putString("id",this.stu_id);
        ed.putInt("school_id",this.school_id);
        ed.putInt("portrait",this.pic_id);
        ed.apply();
    }

    @Override
    public User getUser(Context context) {
        StudentData studentData = new StudentData();
        SharedPreferences pf = context.getSharedPreferences("user_login_info", Context.MODE_PRIVATE);
        studentData.setStu_name(pf.getString("name","user"));
        studentData.setSex(pf.getString("gender","man"));
        studentData.setStu_phone(pf.getString("phone",""));
        studentData.setStu_id(pf.getString("id",""));
        studentData.setSchool_id(pf.getInt("school_id",0));
        studentData.setPic_id(pf.getInt("portrait",0));
        return studentData;
    }

    public void getStudent(JSONObject json) {
        try {
            this.stu_name = json.getString("stu_name");
            this.sex = json.getString("sex");
            this.stu_phone = json.getString("stu_phone");
            this.stu_id = json.getString("stu_id");
            this.school_id = json.getInt("school_id");
            this.pic_id = json.getInt("pic_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String getStu_name() {
        return stu_name;
    }

    public void setStu_name(String stu_name) {
        this.stu_name = stu_name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getSchool_id() {
        return school_id;
    }

    public void setSchool_id(int school_id) {
        this.school_id = school_id;
    }

    public String getStu_phone() {
        return stu_phone;
    }

    public void setStu_phone(String stu_phone) {
        this.stu_phone = stu_phone;
    }

    public String getStu_id() {
        return stu_id;
    }

    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }

    public int getPic_id() {
        return pic_id;
    }

    public void setPic_id(int pic_id) {
        this.pic_id = pic_id;
    }

}
