package com.example.mi_class.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mi_class.R;
import com.example.mi_class.domain.File;



import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FileAdapter extends ArrayAdapter {

    private Context context;
    private List<File> FileList;
    private final int resourceId;

    public FileAdapter( Context context, int resource ,List<File> items) {
        super(context, resource,items);
        this.resourceId = resource;
        this.FileList = items;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {

        File file = (File) getItem(position); // 获取当前项的实例
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象

        TextView file_name = (TextView) view.findViewById(R.id.file_name);
        TextView file_style = (TextView) view.findViewById(R.id.file_style);
        TextView file_size = (TextView) view.findViewById(R.id.file_size);

        assert file!= null;
        file_name.setText(file.getName()); //为文本视图设置文本内容
        file_size.setText(file.getSize());

        //判断身份
        if(file.getStyle().equals("未下载")){
            file_style.setText("未下载");
        }
        else if(file.getStyle().equals("已下载")){
            file_style.setText("已下载");
        }

        return view;
    }

}
