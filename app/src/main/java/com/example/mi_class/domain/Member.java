package com.example.mi_class.domain;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;


public class Member implements Parcelable {
    private String name;
    private String style;
    private String phone;
    private String id;
    private int portrait;

    public Member(String name, String style,String phone,int portrait){
        this.name = name;
        this.style = style;
        this.phone = phone;
        this.portrait = portrait;
    }

    public Member(JSONObject object){
        try {
            this.id = object.getString("stu_id");
            this.name = object.getString("stu_name");
            this.style = "S";
            this.phone = object.getString("stu_phone");
            this.portrait = object.getInt("pic_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Member(JSONObject object,Boolean isTeacher){
        try {
            this.id = object.getString("teacher_id");
            this.name = object.getString("teacher_name");
            this.style = "T";
            this.phone = object.getString("teacher_phone");
            this.portrait = object.getInt("pic_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected Member(Parcel in) {
        name = in.readString();
        style = in.readString();
        phone = in.readString();
        id = in.readString();
        portrait = in.readInt();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPortrait() {
        return portrait;
    }

    public void setPortrait(int portrait) {
        this.portrait = portrait;
    }

    public String getName() {
        return name;
    }

    public String getStyle() {
        return style;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(style);
        dest.writeString(phone);
        dest.writeString(id);
        dest.writeInt(portrait);
    }
}
