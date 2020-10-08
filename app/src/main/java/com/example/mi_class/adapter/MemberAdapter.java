package com.example.mi_class.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mi_class.R;
import com.example.mi_class.domain.Member;



import org.jetbrains.annotations.NotNull;

import java.util.List;

    public class MemberAdapter extends ArrayAdapter {

    private Context context;
    private List<Member> MemberList;
    private final int resourceId;

    public MemberAdapter( Context context, int resource ,List<Member> items) {
        super(context, resource,items);
        this.resourceId = resource;
        this.MemberList = items;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {

        Member member = (Member) getItem(position); // 获取当前项的实例
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象

        TextView member_name = (TextView) view.findViewById(R.id.member_name);
        TextView member_code = (TextView) view.findViewById(R.id.member_code);
        TextView member_style = (TextView) view.findViewById(R.id.member_style);

        assert member != null;
        member_name.setText(member.getName()); //为文本视图设置文本内容
        member_code.setText(member.getCode());

        //判断身份
        if(member.getStyle().equals("T")){
            member_style.setText("老师");
        }
        else if(member.getStyle().equals("S")){
            member_style.setText("学生");
        }

        return view;
    }

}
