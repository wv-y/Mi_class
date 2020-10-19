package com.example.mi_class.domain;

public class CourseMessage {
    private String teacher_name;
    private String course_introduce;

    public void setCourse_introduce(String course_introduce) {
        this.course_introduce = course_introduce;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public String getCourse_introduce() {
        return course_introduce;
    }

    public String getTeacher_name() {
        return teacher_name;
    }
}
