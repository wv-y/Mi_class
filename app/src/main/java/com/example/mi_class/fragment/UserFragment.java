package com.example.mi_class.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mi_class.Login_activity;
import com.example.mi_class.MainActivity;
import com.example.mi_class.R;
import com.example.mi_class.activity.UserInfoActivity;
import com.example.mi_class.domain.StudentData;
import com.example.mi_class.domain.TeacherData;
import com.example.mi_class.tool.MyWebSocket;

import com.minminaya.widget.GeneralRoundFrameLayout;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

public class UserFragment extends Fragment {

    private ImageView feedback_cha;
    private SharedPreferences sp;
    private TextView name;
    private TextView ide;
    private ImageView portrait;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);
        sp = getActivity().getSharedPreferences("user_login_info", Context.MODE_PRIVATE);
        initView(view);
        setHasOptionsMenu(true);
        return view;
    }

    // 隐藏我的碎片中的menu
    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.setGroupVisible(R.menu.main_add_btn, false);
    }

    private void initView(View view) {
        name = view.findViewById(R.id.user_name);
        ide = view.findViewById(R.id.user_character);
        portrait = view.findViewById(R.id.portrait);


        if (sp.getString("identity", "").equals("S")) {
            StudentData student = (StudentData) MainActivity.user;
            if (student == null) {
                Toast.makeText(getContext(), "网络异常", Toast.LENGTH_SHORT).show();
            } else {
                name.setText(student.getStu_name());
                ide.setText("学生");
                portrait.setBackground(getResources().getDrawable(MainActivity.portraits[student.getPic_id()]));
                Glide.with(this).load(MainActivity.portraits[student.getPic_id()]).into(portrait);
            }
        } else {
            TeacherData teacher = (TeacherData) MainActivity.user;
            if (teacher == null) {
                Toast.makeText(getContext(), "网络异常", Toast.LENGTH_SHORT).show();
            } else {
                name.setText(teacher.getTeacher_name());
                ide.setText("老师");
                portrait.setBackground(getResources().getDrawable(MainActivity.portraits[teacher.getPic_id()]));
                Glide.with(this).load(MainActivity.portraits[teacher.getPic_id()]).into(portrait);
            }
        }

        if(MainActivity.user == null){
            view.findViewById(R.id.feedback).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getContext(),"网络错误",Toast.LENGTH_SHORT).show();
                }
            });
            view.findViewById(R.id.info).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getContext(),"网络错误",Toast.LENGTH_SHORT).show();
                }
            });
            view.findViewById(R.id.exit_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getContext(),"网络错误",Toast.LENGTH_SHORT).show();
                }
            });
            view.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(getContext(),"网络错误",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
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
                    sp = getActivity().getSharedPreferences("user_login_info", Context.MODE_PRIVATE);
                    MyWebSocket.OK = false;
                    MyWebSocket.myWebSocket = null;
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("phone", "");
                    editor.putString("identity", "");
                    editor.clear();
                    editor.commit();
                    Intent intent = new Intent(getActivity(), Login_activity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            });
        }
    }

    public void showFeedbackDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_feedback, null, false);
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
                if (email.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "请填写邮箱地址", Toast.LENGTH_SHORT).show();
                } else if (feedback.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "请填写反馈内容", Toast.LENGTH_SHORT).show();
                } else {
                    //保存数据
                }
            }
        });
        dialog.show();
    }

    public void showInfoDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_info, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).create();

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        initView(view);
    }
}
