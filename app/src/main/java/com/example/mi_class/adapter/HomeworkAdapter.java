package com.example.mi_class.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mi_class.R;
import com.example.mi_class.domain.Homework;


import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HomeworkAdapter extends ArrayAdapter {

    private Context context;
    private List<Homework> HomeworkList;
    private final int resourceId;

    public HomeworkAdapter( Context context, int resource ,List<Homework> items) {
        super(context, resource,items);
        this.resourceId = resource;
        this.HomeworkList = items;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {

        Homework homework = (Homework) getItem(position); // 获取当前项的实例
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象

        TextView homework_name = (TextView) view.findViewById(R.id.homework_name);
        TextView homework_time = (TextView) view.findViewById(R.id.homework_time);
        TextView homework_style = (TextView) view.findViewById(R.id.homework_style);

        assert homework != null;
        homework_name.setText(homework.getName()); //为文本视图设置文本内容
        homework_time.setText(homework.getTime());
        //判断作业提交状态
        if(homework.getStyle().equals("已提交")){
            homework_style.setText("已提交");
            homework_style.setTextColor(Color.parseColor("#000000"));
        }
        else if(homework.getStyle().equals("未提交")){
            homework_style.setText("未提交");
            homework_style.setTextColor(Color.parseColor("#FFDF0D0D"));
        }

        return view;
    }

}
