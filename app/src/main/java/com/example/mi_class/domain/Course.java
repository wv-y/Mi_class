package com.example.mi_class.domain;

public class Course {
    private String course_name;
    private String course_code;
    private String course_semester;
    private String couse_member_number;
    private String course_introduce;

    public Course(){}

    public Course(String course_name,String course_code,String course_semester,String couse_member_number,String course_introduce){
        this.course_code = course_code;
        this.course_name = course_name;
        this.course_semester =course_semester;
        this.couse_member_number = couse_member_number;
        this.course_introduce = course_introduce;
    }
    public String getCourse_code() {
        return course_code;
    }

    public String getCourse_name() {
        return course_name;
    }

    public String getCourse_semester() {
        return course_semester;
    }

    public String getCouse_member_number() {
        return couse_member_number;
    }

    public String getCourse_introduce() {
        return course_introduce;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public void setCourse_semester(String course_semester) {
        this.course_semester = course_semester;
    }

    public void setCouse_member_number(String couse_member_number) {
        this.couse_member_number = couse_member_number;
    }

    public void setCourse_introduce(String course_introduce) {
        this.course_introduce = course_introduce;
    }
}
