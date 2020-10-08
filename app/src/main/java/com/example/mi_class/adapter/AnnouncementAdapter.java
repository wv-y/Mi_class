package com.example.mi_class.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mi_class.R;
import com.example.mi_class.domain.Announcement;
import com.example.mi_class.domain.Course;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnnouncementAdapter extends ArrayAdapter {

    private Context context;
    private List<Announcement> AnnouncementList;
    private final int resourceId;

    public AnnouncementAdapter( Context context, int resource ,List<Announcement> items) {
        super(context, resource,items);
        this.resourceId = resource;
        this.AnnouncementList = items;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {

        Announcement announcement = (Announcement) getItem(position); // 获取当前项的实例
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象

        TextView announcement_name = (TextView) view.findViewById(R.id.announcement_name);
        TextView announcement_content = (TextView) view.findViewById(R.id.announcement_content);
        TextView announcement_time = (TextView) view.findViewById(R.id.announcement_time);

        assert announcement != null;
        announcement_name.setText(announcement.getAnnouncement_name()); //为文本视图设置文本内容
        announcement_content.setText(announcement.getAnnouncement_content());
        announcement_time.setText(announcement.getAnnouncement_time());
        return view;
    }

}
