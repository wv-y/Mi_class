package com.example.mi_class.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.mi_class.Login_activity;
import com.example.mi_class.MainActivity;
import com.example.mi_class.R;
import com.example.mi_class.activity.UserInfoActivity;
import com.example.mi_class.tool.MyWebSocket;

public class UserFragment extends Fragment {

    private ImageView feedback_cha;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        view.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
            }
        });
        view.findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedbackDialog();
            }
        });
        view.findViewById(R.id.info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog();
            }
        });
        //退出登录
        view.findViewById(R.id.exit_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pf = getActivity().getSharedPreferences("user_login_info", Context.MODE_PRIVATE);
                MyWebSocket.OK = false;
                MyWebSocket.myWebSocket = null;
                SharedPreferences.Editor editor = pf.edit();
                editor.putString("phone","");
                editor.putString("identity","");
                editor.commit();

                Intent intent = new Intent(getActivity(), Login_activity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }

    public void showFeedbackDialog(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_feedback,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).create();
        Window window = dialog.getWindow();
        //去掉背景白色实现对话框四个角完全曲化
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button commit = view.findViewById(R.id.commit);
        final EditText feedback = view.findViewById(R.id.feedback);
        final EditText email = view.findViewById(R.id.email);
        feedback_cha = view.findViewById(R.id.feedback_cha);
        feedback_cha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().equals("")){
                    Toast.makeText(getContext(),"请填写邮箱地址",Toast.LENGTH_SHORT).show();
                }
                else if(feedback.getText().toString().equals("")){
                    Toast.makeText(getContext(),"请填写反馈内容",Toast.LENGTH_SHORT).show();
                }
                else{
                    //保存数据
                }
            }
        });
        dialog.show();
    }

    public void showInfoDialog(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_info,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).create();

        dialog.show();
    }

}
