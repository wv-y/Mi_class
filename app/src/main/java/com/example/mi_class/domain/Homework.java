package com.example.mi_class.domain;

public class Homework {
    private String name;
    private String content;
    private String time;
    private String style;

    public  Homework(String name,String time,String style){
        this.name = name;
        this.time = time;
        this.style = style;
    }

    public  Homework(String name,String content,String time,String style){
        this.name = name;
        this.content =content;
        this.time = time;
        this.style = style;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public String getStyle() {
        return style;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
