package com.example.mi_class.domain;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONException;
import org.json.JSONObject;

public class TeacherData implements User {
    private String teacher_name;
    private String sex;
    private String department;
    private String teacher_phone;
    private String teacher_id;
    private int school_id;
    private int pic_id;

    @Override
    public void SetUser(Context context) {
        SharedPreferences pf = context.getSharedPreferences("user_login_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pf.edit();
        ed.putString("name",this.teacher_name);
        ed.putString("gender",this.sex);
        ed.putString("id",this.teacher_id);
        ed.putInt("school_id",this.school_id);
        ed.putInt("portrait",this.pic_id);
        ed.putString("department",this.department);
        ed.apply();
    }

    @Override
    public User getUser(Context context) {
        TeacherData teacherData = new TeacherData();
        SharedPreferences pf = context.getSharedPreferences("user_login_info", Context.MODE_PRIVATE);
        teacherData.setTeacher_name(pf.getString("name","user"));
        teacherData.setSex(pf.getString("gender","man"));
        teacherData.setTeacher_phone(pf.getString("phone",""));
        teacherData.setTeacher_id(pf.getString("id",""));
        teacherData.setSchool_id(pf.getInt("school_id",0));
        teacherData.setPic_id(pf.getInt("portrait",0));
        teacherData.setDepartment(pf.getString("department",""));
        return teacherData;
    }

    public void getTeacher(JSONObject json) {
        try {
            this.teacher_name = json.getString("teacher_name");
            this.sex = json.getString("sex");
            this.department = json.getString("department");
            this.teacher_phone = json.getString("teacher_phone");
            this.teacher_id = json.getString("teacher_id");
            this.school_id = json.getInt("school_id");
            this.pic_id = json.getInt("pic_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTeacher_phone() {
        return teacher_phone;
    }

    public void setTeacher_phone(String teacher_phone) {
        this.teacher_phone = teacher_phone;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public int getSchool_id() {
        return school_id;
    }

    public void setSchool_id(int school_id) {
        this.school_id = school_id;
    }

    public int getPic_id() {
        return pic_id;
    }

    public void setPic_id(int piv_id) {
        this.pic_id = piv_id;
    }

}
