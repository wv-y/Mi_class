package com.example.mi_class.domain;

public class Announcement {
    private String announcement_name;
    private String announcement_time;
    private String announcement_content;
    public Announcement(){}
    public Announcement(String announcement_name,String announcement_content,String announcement_time){
        this.announcement_name = announcement_name;
        this.announcement_time = announcement_time;
        this.announcement_content = announcement_content;

    }
   public String getAnnouncement_name(){
        return announcement_name;
   }

    public String getAnnouncement_time() {
        return announcement_time;
    }

    public String getAnnouncement_content() {
        return announcement_content;
    }

    public void setAnnouncement_name(String announcement_name) {
        this.announcement_name = announcement_name;
    }

    public void setAnnouncement_time(String announcement_time) {
        this.announcement_time = announcement_time;
    }

    public void setAnnouncement_content(String announcement_content) {
        this.announcement_content = announcement_content;
    }
}
