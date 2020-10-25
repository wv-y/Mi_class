package com.example.mi_class.domain;

public class File {
    private String name;
    private int image_level;
    private String size;
    private String time;
    private String id;
    public File(){}
    public File(String name, String size,String time,String id){
        this.name = name;
        this.size = size;
        this.time = time;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(String size) {
        this.size = size;
    }
    public void setTime(String time){
        this.time = time;
    }
    public String getTime(){
        return time;
    }
    public String getId(){return id;}
    public void setId(String id){this.id = id;}
    public void setImage_level(int level){
        this.image_level = level;
    }
    public int getImage_level() {
        return image_level;
    }
}
