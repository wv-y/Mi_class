package com.example.mi_class.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mi_class.R;
import com.example.mi_class.domain.Homework;


import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

public class HomeworkAdapter extends RecyclerView.Adapter<HomeworkAdapter.HomeworkViewHolder> {

    private Context context;
    private List<Homework> homeworkList;

    public HomeworkAdapter(List<Homework> homeworkList){
        this.homeworkList = homeworkList;
    }

    @NonNull
    @Override
    public HomeworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.homework_card,parent,false);
        return new HomeworkViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeworkViewHolder holder, int position) {
        Homework homework = homeworkList.get(position);
//        时间格式设置
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy/MM/dd HH:mm");
        Date p_date = new Date(homework.getPubtime());
        Date s_date = new Date(homework.getPubtime());
        String p_time = simpleDateFormat.format(p_date);
        String s_time = simpleDateFormat.format(s_date);

//        数据源绑定
        holder.title.setText(homework.getTitle());
        holder.detail.setText(homework.getDetail());
        holder.pubtime.setText(p_time);
        holder.subtime.setText(s_time);
    }

    @Override
    public int getItemCount() {
        return homeworkList.size();
    }

    class HomeworkViewHolder extends RecyclerView.ViewHolder{
        protected TextView title;
        protected TextView detail;
        protected TextView pubtime;
        protected TextView subtime;
        public HomeworkViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.homework_title);
            detail = itemView.findViewById(R.id.homework_detail);
            pubtime = itemView.findViewById(R.id.homework_pubdate);
            subtime = itemView.findViewById(R.id.homework_subdate);
        }
    }

}
