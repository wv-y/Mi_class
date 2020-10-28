package com.example.mi_class.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mi_class.R;
import com.example.mi_class.domain.Homework;


import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

public class HomeworkAdapter extends RecyclerView.Adapter<HomeworkAdapter.HomeworkViewHolder> implements View.OnClickListener{

    private Context context;    //上下文
    private List<Homework> homeworkList;    //数据源

    public HomeworkAdapter(Context context,List<Homework> homeworkList){
        this.context = context;
        this.homeworkList = homeworkList;
    }

    @NonNull
    @Override
    public HomeworkViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.homework_card,parent,false);
        return new HomeworkViewHolder(view);
    }
//    绑定
    @Override
    public void onBindViewHolder(@NonNull final HomeworkViewHolder holder, int position) {
        Homework homework = homeworkList.get(position);
//        时间格式设置
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy/MM/dd HH:mm");
//        Date p_date = new Date(homework.getPubtime());
//        Date s_date = new Date(homework.getPubtime());
//        String p_time = simpleDateFormat.format(p_date);
//        String s_time = simpleDateFormat.format(s_date);

//        传递位置
        holder.itemView.setTag(position);
//        传递按钮Id
        holder.func_botton.setTag(position);
//        数据源绑定
        holder.title.setText(homework.getTitle());
        holder.detail.setText(homework.getDetail());
        holder.pubtime.setText(homework.getPubtime());
        holder.subtime.setText(homework.getSubtime());
        if(homework.getFilenumber()>0){
            holder.filenumber.setText(homework.getFilenumber()+"个附件");
        }
        else{
            holder.filenumber.setText("无附件");
        }

        if (homework.getState()==0){    //老师查看作业详情
            holder.func_botton.setText("查看详情");
        }
        if (homework.getState()==1){
            holder.func_botton.setText("已截止");
        }
        if (homework.getState()==2){    //学生提交作业
            holder.func_botton.setText("提交作业");
        }
        if (homework.getState()==3){
            holder.func_botton.setText("更新提交");
        }
        if (homework.getState()==4){
            holder.func_botton.setText("未提交");
            holder.func_botton.setClickable(false);
        }
        if (homework.getState()==5){
            holder.func_botton.setText("已提交");
            holder.func_botton.setClickable(false);
        }
//        View itemView = ((CardView) holder.itemView).getChildAt(0);

//        if(onItemClickListener !=null){
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = holder.getLayoutPosition();
//                    onItemClickListener.onItemClick(holder.itemView,position);
//                }
//            });
//        }
    }


//    item数目
    @Override
    public int getItemCount() {
        return homeworkList.size();
    }








    class HomeworkViewHolder extends RecyclerView.ViewHolder{
        protected TextView title;
        protected TextView detail;
        protected TextView pubtime;
        protected TextView subtime;
        protected TextView filenumber;
        protected Button func_botton;
        public HomeworkViewHolder(View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.homework_title);
            detail = itemView.findViewById(R.id.homework_detail);
            pubtime = itemView.findViewById(R.id.homework_pubdate);
            subtime = itemView.findViewById(R.id.homework_subdate);
            filenumber = itemView.findViewById(R.id.homework_file_number);
            func_botton = itemView.findViewById(R.id.homework_sub_button);

//            添加点击事件
            itemView.setOnClickListener(HomeworkAdapter.this);
            func_botton.setOnClickListener(HomeworkAdapter.this);
        }
    }



//    item、item内部控件
    public enum ViewName{
        ITEM,
        PRACTISE
    }

//    自定义回调接口实现点击和长按事件
    public interface OnItemClickListener{
        void onItemClick(View v, ViewName viewName,int position);
        void onItemLongClick(View v);
    }

//    自定义接口声明
    private OnItemClickListener onItemClickListener;
//    定义方法传递给外部使用者
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();    //getTag()获取数据
        System.out.println("homework_position"+position);
        System.out.println("homework_getId"+v.getId());
        if(onItemClickListener!=null){
            switch (v.getId()){
                case R.id.homework_recycler_view:
                    onItemClickListener.onItemClick(v,ViewName.PRACTISE,position);
                    break;
                default:
                    onItemClickListener.onItemClick(v,ViewName.ITEM,position);
                    break;
            }
        }
    }
}
