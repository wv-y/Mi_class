package com.example.mi_class.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class StuLogInfo implements Parcelable {
    String stu_id;
    String stu_phone;
    String stu_name;
    String value;

    public StuLogInfo(){}

    public String getStu_id() {
        return stu_id;
    }

    public void setStu_id(String stu_id) {
        this.stu_id = stu_id;
    }

    public String getStu_phone() {
        return stu_phone;
    }

    public void setStu_phone(String stu_phone) {
        this.stu_phone = stu_phone;
    }

    public String getStu_name() {
        return stu_name;
    }

    public void setStu_name(String stu_name) {
        this.stu_name = stu_name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "StuLogInfo{" +
                "stu_id='" + stu_id + '\'' +
                ", stu_phone='" + stu_phone + '\'' +
                ", stu_name='" + stu_name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stu_id);
        dest.writeString(stu_name);
        dest.writeString(stu_phone);
        dest.writeString(value);

    }

    public static final Parcelable.Creator<StuLogInfo> CREATOR = new Creator<StuLogInfo>() {
        @Override
        public StuLogInfo createFromParcel(Parcel source) {
            StuLogInfo stuLogInfo = new StuLogInfo();
            stuLogInfo.setStu_name(source.readString());
            stuLogInfo.setStu_id(source.readString());
            stuLogInfo.setStu_phone(source.readString());
            stuLogInfo.setValue(source.readString());
            return stuLogInfo;
        }

        @Override
        public StuLogInfo[] newArray(int size) {
            return new StuLogInfo[size];
        }
    };
}
