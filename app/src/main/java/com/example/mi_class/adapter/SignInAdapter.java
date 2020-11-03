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
import com.example.mi_class.domain.SignIn;


import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SignInAdapter extends ArrayAdapter {

    private Context context;
    private List<SignIn> SignInList;
    private final int resourceId;

    public SignInAdapter( Context context, int resource ,List<SignIn> items) {
        super(context, resource,items);
        this.resourceId = resource;
        this.SignInList = items;
    }

    @SuppressLint("SetTextI18n")
    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {

        SignIn signIn = (SignIn) getItem(position); // 获取当前项的实例
        @SuppressLint("ViewHolder") View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象

        TextView sign_name = (TextView) view.findViewById(R.id.sign_name);
        TextView sign_time = (TextView) view.findViewById(R.id.start_and_end_time);
        TextView sign_style = (TextView) view.findViewById(R.id.sign_style);
        TextView sign_date = (TextView) view.findViewById(R.id.sign_date);

        assert signIn != null;
        sign_name.setText(signIn.getSign_name()); //为文本视图设置文本内容
        sign_date.setText(signIn.getStart_time().substring(0,10));
        sign_time.setText(signIn.getStart_time().substring(11,16)+" - "+signIn.getEnd_time().substring(11,16));
        //判断签到状态
        if(signIn.getSize().equals("") || signIn.getSize() == null){
            switch (signIn.getSign_style()) {
                case "已签到":
                    sign_style.setText("已签到");
                    sign_style.setTextColor(Color.parseColor("#39BC3E"));
                    break;
                case "未签到":
                    sign_style.setText("未签到");
                    sign_style.setTextColor(Color.parseColor("#9D9D9D"));
                    break;
                case "迟到":
                    sign_style.setText("迟到");
                    sign_style.setTextColor(Color.parseColor("#FFDF0D0D"));
                    break;
                case "请假":
                    sign_style.setText("请假");
                    sign_style.setTextColor(Color.parseColor("#ED930D"));
                    break;
            }
        } else {
            sign_style.setText(signIn.getSize());
            sign_style.setTextColor(Color.parseColor("#0099ff"));
        }
        return view;
    }

}
