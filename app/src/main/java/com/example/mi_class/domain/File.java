package com.example.mi_class.domain;

public class File {
    private String name;
    private String style;
    private String size;

    public File(String name, String style, String size){
        this.name = name;
        this.style = style;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getStyle() {
        return style;
    }

    public String getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
