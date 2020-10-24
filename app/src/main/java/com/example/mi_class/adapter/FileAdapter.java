package com.example.mi_class.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        TextView file_size = (TextView) view.findViewById(R.id.file_size);
        TextView file_time = (TextView) view.findViewById(R.id.file_time);
        TextView file_id = (TextView) view.findViewById(R.id.file_id);
        ImageView file_type = (ImageView) view.findViewById(R.id.file_image);

        assert file!= null;
        file_name.setText(file.getName()); //为文本视图设置文本内容
        file_size.setText(file.getSize());
        file_time.setText(file.getTime());
        file_id.setTag(file.getId());
        file_type.setImageLevel(file.getImage_level());
        return view;
    }

}
