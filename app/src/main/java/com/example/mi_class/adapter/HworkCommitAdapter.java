package com.example.mi_class.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mi_class.R;
import com.example.mi_class.activity.HomeworkActivity;
import com.example.mi_class.activity.HworkDetailActivity;
import com.example.mi_class.domain.StuLogInfo;
import com.example.mi_class.tool.HttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HworkCommitAdapter extends RecyclerView.Adapter<HworkCommitAdapter.MyViewHolder> {
    private Context context;
    private List<StuLogInfo> stuLogInfos;
    private Map<String,String> params;
    private String course_id,fb_time;
    public HworkCommitAdapter(Context context, List<StuLogInfo> stuLogInfoList,String code,String time){
        this.context = context;
        this.stuLogInfos = stuLogInfoList;
        this.course_id = code;
        this.fb_time = time;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.homework_stu_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        View view = holder.mView;

        final StuLogInfo stuLogInfo = stuLogInfos.get(position);

        holder.commit_stu_id.setText(stuLogInfo.getStu_id());
        holder.commit_stu_name.setText(stuLogInfo.getStu_name());

        holder.commit_stu_value.setText(stuLogInfo.getValue());
        if(stuLogInfo.getValue().equals("未提交")){
            holder.commit_stu_value.setTextColor(Color.RED);
        }
        else{
            holder.commit_stu_value.setTextColor(Color.BLUE);
        }
        if(stuLogInfo.getValue().equals("已提交")){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("提示");
                    builder.setMessage("跳转到学生作业下载页面？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            get_homework_file_list(course_id,fb_time,stuLogInfo.getStu_phone());
                            Toast.makeText(context,"下载学生作业",Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
        else if(stuLogInfo.getValue().equals("未提交")){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"学生作业未提交",Toast.LENGTH_LONG).show();
                }
            });
        }else{

//            get_homework_file_list();
//            stuLogInfos.get(position).get
//            Uri uri = Uri.parse(downUrl+"?course_id="+course_code+"&file_id="+file_id);
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            context.startActivity(intent);
        }


    }

    @Override
    public int getItemCount() {
        return stuLogInfos.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        protected TextView commit_stu_name;
        protected TextView commit_stu_id;
        protected TextView commit_stu_value;

        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            commit_stu_id = mView.findViewById(R.id.commit_stu_id);
            commit_stu_name = mView.findViewById(R.id.commit_stu_name);
            commit_stu_value = mView.findViewById(R.id.commit_stu_value);

        }
    }

    //获取作业文件列表
    public void get_homework_file_list(String code,String time,String phone){
        params = new HashMap<>();
        params.put("course_id",code);
        params.put("fb_time",time);
        params.put("stu_phone",phone);
        System.out.println("download_studentfile"+code+time+phone);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
//                System.out.println("course_code params"+params.get("course_id"));
                bundle.putString("info2", HttpUtils.sendPostMessage(params,"utf-8","homework/getHomework"));
                message.setData(bundle);
                message.what = 207;
                HomeworkActivity.homework_handler.sendMessage(message);
            }
        }).start();
    }
}


