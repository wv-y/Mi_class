package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.alibaba.fastjson.JSON;
import com.example.mi_class.MainActivity;
import com.example.mi_class.R;
import com.example.mi_class.Start_activity;
import com.example.mi_class.domain.*;
import com.example.mi_class.fragment.CourseFragment;
import com.example.mi_class.tool.HttpUtils;
import com.example.mi_class.tool.Match;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CourseDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private String course_code,course_name,course_introduce;
    private ImageView announcement_button,sign_in_button,member_button,homework_button,file_button,more_button;
    private TextView new_ann,new_ann_time;
    private String identity,phone_number;
    private Handler handler;
    private final static int DELETE_COURSE = 304;
    private final static int CHANGE_COURSE = 305;
    private final static int EXIT_COURSE = 306;
    private final static int MESSAGE_COURSE = 307;
    private final static int GET_ANN_LIST = 308;
    private final static int NEW_ANN = 309;
    private final static int GET_SIGN_LIST = 310;
    //private final static int STU_GET_SIGN_LIST = 311;
    private final static int get_filelist = 311;
    private Map<String,String> params;
    private CourseMessage courseMessage = new CourseMessage();
    private String info;
    private AlertDialog dialog;
    private String change_name,change_introduce;
    private String ann_list;
    //存放本地文件绝对路径
    String path;
    private ArrayList<Member> memberList = new ArrayList<>();
    private ProgressDialog progressDialog = null;
    //private ProgressBar progressBar;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_course_details);

        //获得上个活动发送的信息
        final Intent intent = getIntent();
        course_code = intent.getStringExtra("course_code");
        course_name = intent.getStringExtra("course_name");
        course_introduce = intent.getStringExtra("course_introduce");

        // 设置标题
        setTitle(course_name);
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        announcement_button = (ImageView)findViewById(R.id.announcement_button);
        sign_in_button = findViewById(R.id.sign_in_button);
        member_button = findViewById(R.id.member_button);
        homework_button = findViewById(R.id.homework_button);
        file_button = findViewById(R.id.file_button);
        more_button = findViewById(R.id.more_button);
        new_ann = findViewById(R.id.new_ann);
        new_ann_time = findViewById(R.id.new_ann_time);

        new_ann.setMovementMethod(ScrollingMovementMethod.getInstance());

        announcement_button.setOnClickListener(this);
        sign_in_button.setOnClickListener(this);
        member_button.setOnClickListener(this);
        homework_button.setOnClickListener(this);
        file_button.setOnClickListener(this);
        more_button.setOnClickListener(this);

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                    switch (msg.what){
                        case DELETE_COURSE:
                        // 删除课程判断结果
                            info  = msg.getData().getString("info");
                            if(info.equals("1")){
                            // 删除成功 退出课程详情并刷新课程列表页面
                                startActivity(new Intent(CourseDetailsActivity.this, MainActivity.class));
                                CourseDetailsActivity.this.finish();
                                Toast.makeText(CourseDetailsActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            } else if (info.equals("0")) {
                                // 删除失败
                                Toast.makeText(CourseDetailsActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CourseDetailsActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case CHANGE_COURSE:
                            // 修改课程信息
                            info = msg.getData().getString("info");
                            if(info.equals("1")){
                                Toast.makeText(CourseDetailsActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                                course_name = change_name;
                                course_introduce = change_introduce;
                                dialog.cancel();
                                setTitle(change_name);
                            } else if(info.equals("0")){
                                Toast.makeText(CourseDetailsActivity.this,"修改失败",Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(CourseDetailsActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case EXIT_COURSE:
                            // 退出课程
                            info = msg.getData().getString("info");
                            if(info.equals("1")){
                                startActivity(new Intent(CourseDetailsActivity.this,MainActivity.class));
                                CourseDetailsActivity.this.finish();
                                Toast.makeText(CourseDetailsActivity.this,"退出成功",Toast.LENGTH_SHORT).show();
                            } else if(info.equals("0")){
                                Toast.makeText(CourseDetailsActivity.this,"退出失败",Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(CourseDetailsActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case MESSAGE_COURSE:
                            // 查看课程信息
                            info = msg.getData().getString("info");
                            if(info.equals("")){
                                Toast.makeText(CourseDetailsActivity.this,"查询错误",Toast.LENGTH_SHORT).show();
                            } else if(info.equals("-999")){
                                Toast.makeText(CourseDetailsActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                            } else {
                                courseMessage = get_course_message(info);
                                course_message_dialog();
                            }
                            break;
                        case GET_ANN_LIST:
                            // 获取公告列表 跳转到公告列表
                            info = msg.getData().getString("info");
                            System.out.println("annlist"+info);
                            if(info.equals("-999")){
                                Toast.makeText(CourseDetailsActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(CourseDetailsActivity.this, AnnouncementActivity.class);
                                intent.putExtra("ann_list",info);
                                intent.putExtra("course_code",course_code);
                                startActivity(intent);
                            }
                            break;
                        case get_filelist:
                            //获取文件列表，跳转到文件列表
                            info = msg.getData().getString("info");
                            if(info.equals("-999")){
                                Toast.makeText(CourseDetailsActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(CourseDetailsActivity.this, FileActivity.class);
                                intent.putExtra("file_list",info);
                                intent.putExtra("course_code",course_code);
                                intent.putExtra("identity",identity);
                                startActivity(intent);
                            }
                            break;
                        case NEW_ANN:
                            // 获取最新公告
                            info = msg.getData().getString("info");
                            if(info.equals("[]")){
                                new_ann.setText("暂时没有最新公告");
                                new_ann_time.setText("");
                                save_ann(info);
                            } else if(info.equals("-999")){
                                Toast.makeText(CourseDetailsActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                                read_ann_local();
                            } else {
                                Announcement ann = get_new_ann(info);
                                new_ann.setText(ann.getAnnouncement_name()+"："+ann.getAnnouncement_content());
                                new_ann_time.setText(ann.getAnnouncement_time());
                                save_ann(info);
                            }
                            break;
                        case GET_SIGN_LIST:
                            info = msg.getData().getString("info");
                            Log.d("sign",info);
                            if(info.equals("-999")){
                                Toast.makeText(CourseDetailsActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(CourseDetailsActivity.this, SignInActivity.class);
                                intent.putExtra("course_code",course_code);
                                intent.putExtra("identity",identity);
                                intent.putExtra("sign_list",info);
                                intent.putExtra("phone_number",phone_number);
                                startActivity(intent);
                            }
                            break;

                }
            }
        };
    }

    @Override
    public void onStart() {
        // 初始化数据
        init_data();
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.announcement_button:
                get_ann_list(course_code);
                break;
            case R.id.sign_in_button:
                if(identity.equals("T"))
                    tea_get_sign_list();
                else if(identity.equals("S"))
                    stu_get_sign_list();
                break;
            case R.id.member_button:
                toMemberActivity();
                break;
            case R.id.homework_button:
                intent = new Intent(CourseDetailsActivity.this,HomeworkActivity.class);
                startActivity(intent);
                break;
            case R.id.file_button:
                /*intent = new Intent(CourseDetailsActivity.this,FileActivity.class);
                intent.putExtra("course_code",course_code);
                startActivity(intent);*/
                get_file_list(course_code);
                break;
            case R.id.more_button:
                showMoreDialog();
                break;
            default:
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    public void showMoreDialog() {
        LayoutInflater inflater = LayoutInflater.from(CourseDetailsActivity.this);
        View dialogView;
        final AlertDialog alterDialog;
        TextView dialog_course_name, dialog_course_code,dialog_copy_code,change_course,
                exit_course,delete_course, course_message;
        ImageView dialog_close;
        if (identity.equals("S")) {
            dialogView = inflater.inflate(R.layout.dialog_more_button_stu, null);
            alterDialog = new AlertDialog.Builder(CourseDetailsActivity.this).create();
            alterDialog.show();
            alterDialog.setCancelable(false);
            Window window = alterDialog.getWindow();
            //去掉背景白色实现对话框四个角完全曲化
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setContentView(dialogView);
            dialog_course_name = dialogView.findViewById(R.id.stu_dialog_course_name);
            dialog_course_code = dialogView.findViewById(R.id.stu_dialog_course_code);
            dialog_copy_code = dialogView.findViewById(R.id.stu_dialog_copy_code);
            exit_course = dialogView.findViewById(R.id.exit_course);
            dialog_close = dialogView.findViewById(R.id.dialog_close);
            course_message = dialogView.findViewById(R.id.course_message);
            dialog_course_name.setText(course_name);
            dialog_course_code.setText("课程码："+course_code);
            dialog_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alterDialog.cancel();
                }
            });
            dialog_copy_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    copyCode();
                    alterDialog.cancel();
                }
            });
            // 查看课程信息
            course_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    message_course(course_code);
                    //course_message_dialog();
                    alterDialog.cancel();
                }
            });
            // 学生退出课程
            exit_course.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alterDialog.cancel();
                    exit_course_dialog();
                }
            });
        } else {
            dialogView = inflater.inflate(R.layout.dialog_more_button_tea, null);
            alterDialog = new AlertDialog.Builder(CourseDetailsActivity.this).create();
            alterDialog.show();
            alterDialog.setCancelable(false);
            Window window = alterDialog.getWindow();
            //去掉背景白色实现对话框四个角完全曲化
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setContentView(dialogView);
            dialog_course_name = dialogView.findViewById(R.id.tea_dialog_course_name);
            dialog_course_code = dialogView.findViewById(R.id.tea_dialog_course_code);
            dialog_copy_code = dialogView.findViewById(R.id.tea_dialog_copy_code);
            change_course = dialogView.findViewById(R.id.change_course);
            delete_course = dialogView.findViewById(R.id.delete_course);
            dialog_close = dialogView.findViewById(R.id.dialog_close);
            course_message = dialogView.findViewById(R.id.course_message);
            dialog_course_name.setText(course_name);
            dialog_course_code.setText("课程码："+course_code);
            dialog_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alterDialog.cancel();
                }
            });
            dialog_copy_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    copyCode();
                    alterDialog.cancel();
                }
            });
            // 显示课程信息
            course_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    message_course(course_code);
                    //course_message_dialog();
                    alterDialog.cancel();
                }
            });
            // 显示修改课程对话框
            change_course.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alterDialog.cancel();
                    change_course_dialog();
                }
            });
            // 老师删除课程
            delete_course.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alterDialog.cancel();
                    delete_course_dialog();
                }
            });
        }
    }

    public void copyCode(){
        //获取剪贴板管理器
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("text",course_code);
        // 将ClipData内容放到系统剪贴板里
        assert cm != null;
        cm.setPrimaryClip(mClipData);
        Toast.makeText(CourseDetailsActivity.this,"已复制到剪切板",Toast.LENGTH_SHORT).show();
    }

    // 老师修改课程
    @SuppressLint("SetTextI18n")
    public void change_course_dialog(){
        LayoutInflater inflater = LayoutInflater.from(CourseDetailsActivity.this);
        final View dialogView;
        final AlertDialog alterDialog;
        ImageView change_course_cha;
        final EditText change_course_name, change_course_introduce;
        Button change_course_submit;
        final TextView change_course_introduce_num;

        dialogView = inflater.inflate(R.layout.dialog_change_courses,null);
        alterDialog = new AlertDialog.Builder(CourseDetailsActivity.this).create();
        alterDialog.show();
        alterDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alterDialog.setCancelable(false);
        Window window = alterDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);
        change_course_cha = dialogView.findViewById(R.id.change_course_cha);
        change_course_name = dialogView.findViewById(R.id.change_course_name);
        change_course_introduce = dialogView.findViewById(R.id.change_course_introduce);
        change_course_introduce_num = dialogView.findViewById(R.id.change_course_introduce_num);
        change_course_submit = dialogView.findViewById(R.id.change_course_submit);
        change_course_name.setText(course_name);
        change_course_introduce.setText(course_introduce);
        change_course_introduce_num.setText(change_course_introduce.getText().toString().length()+"/300");
        change_course_cha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterDialog.cancel();
            }
        });
        // 获取焦点改变视图
        change_course_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                change_edit_style(b,change_course_name);
            }
        });
        change_course_introduce.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                change_edit_style(b,change_course_introduce);
            }
        });

        // 限制课程描述字数小于300
        change_course_introduce.addTextChangedListener(new TextWatcher() {
            CharSequence temp;
            @SuppressLint("SetTextI18n")
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                change_course_introduce_num.setText(charSequence.length()+"/300");
                if(temp.length()==300){
                    change_course_introduce.setBackgroundResource(R.drawable.edit_back_error);
                    //Toast.makeText(getApplicationContext(),"课程描述最多300字",Toast.LENGTH_SHORT).show();
                } else{
                    change_course_introduce.setBackgroundResource(R.drawable.edit_back_onfocus);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                int editStart = change_course_introduce.getSelectionStart();
                int editEnd = change_course_introduce.getSelectionEnd();
                if (temp.length() > 300) {
                    editable.delete(editStart - 1, editEnd);
                    change_course_introduce.setText(editable);
                    change_course_introduce.setSelection(editable.length());
                }
            }
        });
        // 点击确定按钮
        change_course_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // alterDialog.cancel();
                dialog = alterDialog;
                change_name = change_course_name.getText().toString();
                change_introduce = change_course_introduce.getText().toString();
                progressDialog = ProgressDialog.show(view.getContext(), "请稍等", "更新课程信息中，请稍候。。。",true);
                //progressBar.setVisibility(View.VISIBLE);
                change_course(course_code,change_course_name.getText().toString(),change_course_introduce.getText().toString());
            }
        });
    }

    // 显示课程信息对话框
    public void course_message_dialog(){
        LayoutInflater inflater = LayoutInflater.from(CourseDetailsActivity.this);
        final View dialogView;
        final AlertDialog alertDialog;
        ImageView dialog_close;
        TextView course_introduce,teacher_name;

        dialogView = inflater.inflate(R.layout.dialog_course_message,null);
        alertDialog = new AlertDialog.Builder(CourseDetailsActivity.this).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);
        dialog_close = dialogView.findViewById(R.id.dialog_close);
        course_introduce = dialogView.findViewById(R.id.course_introduce);
        teacher_name = dialogView.findViewById(R.id.teacher_name);
        course_introduce.setMovementMethod(ScrollingMovementMethod.getInstance());
        teacher_name.setText(courseMessage.getTeacher_name());
        course_introduce.setText(courseMessage.getCourse_introduce());
        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    // 老师确认删除课程
    public void delete_course_dialog(){
        LayoutInflater inflater = LayoutInflater.from(CourseDetailsActivity.this);
        final View dialogView;
        final AlertDialog alterDialog;
        Button confirm;
        TextView cancel;
        ImageView dialog_close;

        dialogView = inflater.inflate(R.layout.dialog_delete_confirm,null);
        alterDialog = new AlertDialog.Builder(CourseDetailsActivity.this).create();
        alterDialog.show();
        alterDialog.setCancelable(false);
        Window window = alterDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);
        cancel =  dialogView.findViewById(R.id.cancel);
        confirm = dialogView.findViewById(R.id.confirm);
        dialog_close = dialogView.findViewById(R.id.dialog_close);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterDialog.cancel();
            }
        });
        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterDialog.cancel();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterDialog.cancel();
                delete_course(course_code);
            }
        });
    }

    // 学生退出课程
    public void exit_course_dialog(){
        LayoutInflater inflater = LayoutInflater.from(CourseDetailsActivity.this);
        final View dialogView;
        final AlertDialog alterDialog;
        Button confirm;
        TextView cancel,title;
        ImageView dialog_close;

        dialogView = inflater.inflate(R.layout.dialog_delete_confirm,null);
        alterDialog = new AlertDialog.Builder(CourseDetailsActivity.this).create();
        alterDialog.show();
        alterDialog.setCancelable(false);
        Window window = alterDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);
        cancel =  dialogView.findViewById(R.id.cancel);
        confirm = dialogView.findViewById(R.id.confirm);
        title = dialogView.findViewById(R.id.text_title);
        dialog_close = dialogView.findViewById(R.id.dialog_close);
        title.setText("确认退出课程");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterDialog.cancel();
            }
        });
        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterDialog.cancel();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterDialog.cancel();
                exit_course(course_code,phone_number);
            }
        });
    }

    // 改变编辑框的视图
    public void change_edit_style(boolean b,EditText editText){
        if(b)
            editText.setBackgroundResource(R.drawable.edit_back_onfocus);
        else
            editText.setBackgroundResource(R.drawable.edit_back);
    }

    // 从本地缓存初始化手机号和身份
    public void init_data(){
        SharedPreferences sp = getSharedPreferences("user_login_info",MODE_PRIVATE);
        phone_number = sp.getString("phone","");
        identity = sp.getString("identity","");
        read_ann_local();
        get_new_ann_list(course_code);
    }

    // 老师删除课程
    public void delete_course(String code){
        params = new HashMap<>();
        params.put("course_id",code);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","teaDeleteCourse");
                Log.d("teadelete",res);
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("info",res);
                message.setData(bundle);
                message.what = DELETE_COURSE;
                handler.sendMessage(message);
            }
        }).start();
    }

    // 老师修改课程信息
    public void change_course(String code,String name,String introduce){
        params = new HashMap<>();
        params.put("course_id",code);
        params.put("course_name",name);
        params.put("course_introduce",introduce);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","teaUpdateCourse");
                System.out.println("res"+res);
                Message message = new Message();
                Bundle b = new Bundle();
                b.putString("info",res);
                message.setData(b);
                message.what = CHANGE_COURSE;
                handler.sendMessage(message);
              //  progressBar.setProgress(100);
               // progressBar.setVisibility(View.VISIBLE);


               progressDialog.dismiss();
            }
        }).start();
    }

    // 学生退出课程
    public void exit_course(String code,String phone){
        params = new HashMap<>();
        params.put("course_id",code);
        params.put("stu_phone",phone);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","stuDeleteCourseLog");
                Message m = new Message();
                Bundle b = new Bundle();
                b.putString("info",res);
                m.setData(b);
                m.what = EXIT_COURSE;
                handler.sendMessage(m);
            }
        }).start();
    }

    // 查看课程信息
    public void message_course(String code){
        params = new HashMap<>();
        params.put("course_id",code);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","showCourseMessage");
                Message m = new Message();
                Bundle b = new Bundle();
                b.putString("info",res);
                m.setData(b);
                m.what = MESSAGE_COURSE;
                handler.sendMessage(m);
            }
        }).start();
    }

    // 解析返回的课程信息
    public CourseMessage get_course_message(String str){
        CourseMessage message = new CourseMessage();
        try {
            JSONObject object = new JSONObject(str);
            message.setTeacher_name(object.getString("teacher_name"));
            message.setCourse_introduce(object.getString("course_introduce"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }

    // 获取公告列表
    public void get_ann_list(String code){
        params = new HashMap<>();
        params.put("course_id",code);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("info",HttpUtils.sendPostMessage(params,"utf-8","notice/get"));
                message.setData(bundle);
                message.what = GET_ANN_LIST;
                handler.sendMessage(message);
            }
        }).start();

    }

    // 获取最新公告
    public void get_new_ann_list(String code){
        params = new HashMap<>();
        params.put("course_id",code);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("info",HttpUtils.sendPostMessage(params,"utf-8","notice/get"));
                message.setData(bundle);
                message.what = NEW_ANN;
                handler.sendMessage(message);
            }
        }).start();

    }

    // 解析公告列表，拿取最新公告
    public Announcement get_new_ann(String str){
        Announcement ann = new Announcement();
        try {
            JSONArray array = new JSONArray(str);
            /*for(int i=0; i<array.length();i++){
                JSONObject object = array.getJSONObject(i);
            }*/
            JSONObject object = array.getJSONObject(0);
            ann.setAnnouncement_name(object.getString("title"));
            ann.setAnnouncement_content(object.getString("value"));
            ann.setAnnouncement_time(object.getString("fb_time").substring(0,16));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ann;
    }

    // 最新公告本地缓存
    public void save_ann(String str){
        SharedPreferences sp = CourseDetailsActivity.this.getSharedPreferences("ann",MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor edit = sp.edit();
        edit.clear();
        edit.putString("new_ann",str);
        edit.apply();
        edit.commit();
    }

    // 读取最新公告本地缓存
    @SuppressLint("SetTextI18n")
    public void read_ann_local(){
        SharedPreferences sp = CourseDetailsActivity.this.getSharedPreferences("ann",MODE_PRIVATE);
        String ann_local = sp.getString("new_ann","");
        if(ann_local.equals("[]") || ann_local.equals("")){
            new_ann.setText("暂时没有最新公告");
            new_ann_time.setText("");
        } else {
            Announcement announcement = get_new_ann(ann_local);
            new_ann.setText(announcement.getAnnouncement_name()+"："+announcement.getAnnouncement_content());
            new_ann_time.setText(announcement.getAnnouncement_time());
        }
    }
    // 老师获取签到列表
    public void tea_get_sign_list(){
        params = new HashMap<>();
        params.put("course_id",course_code);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","qiandao/getListT");
                Message message = new Message();
                Bundle b = new Bundle();
                b.putString("info",res);
                message.setData(b);
                message.what = GET_SIGN_LIST;
                handler.sendMessage(message);
            }
        }).start();
    }

    // 学生获取签到列表
    public void stu_get_sign_list(){
        params = new HashMap<>();
        params.put("student_phone",phone_number);
        params.put("course_id",course_code);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","qiandao/getListS");
                Message m = new Message();
                Bundle b = new Bundle();
                b.putString("info",res);
                m.setData(b);
                m.what = GET_SIGN_LIST;
                handler.sendMessage(m);
            }
        }).start();
    }

    //获取文件列表
    public void get_file_list(String code){
        params = new HashMap<>();
        params.put("course_id",code);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("info", HttpUtils.sendPostMessage(params,"utf-8","sharedfile/getList"));
                message.setData(bundle);
                message.what = get_filelist;
                handler.sendMessage(message);
            }
        }).start();
    }
    //解析文件列表
    public List<File> get_new_file_list(String str){
        List<File> list = new ArrayList<>();
        FileActivity activity = new FileActivity();
        String name,type;
        int typeLevel;
        try{
            JSONArray array = new JSONArray(str);
            for(int i=0;i<array.length();i++){
                File file = new File();
                JSONObject jsonObject = array.getJSONObject(i);
                file.setName(jsonObject.getString("file_name"));
                name = jsonObject.getString("file_name");
                file.setTime(jsonObject.getString("fb_time"));

                file.setSize(jsonObject.getString("file_size"));

                file.setId(jsonObject.getString("file_id"));
                //设置文件类型
                type=activity.getFileType(name);
                switch (type){
                    case "png": typeLevel=0;
                        break;
                    case "jpg": typeLevel=0;
                        break;
                    case "gif": typeLevel=0;
                        break;
                    case "webp": typeLevel=0;
                        break;
                    case "mp3": typeLevel=1;
                        break;
                    case "wav": typeLevel=1;
                        break;
                    case "mp4": typeLevel=3;
                        break;
                    case "avi": typeLevel=3;
                        break;
                    case "pdf": typeLevel=2;
                        break;
                    case "ppt": typeLevel=2;
                        break;
                    case "doc": typeLevel=2;
                        break;
                    case "docx": typeLevel=2;
                        break;
                    case "html": typeLevel=2;
                        break;
                    default: typeLevel = 4;
                        break;
                }

                file.setImage_level(typeLevel);
                list.add(file);

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return list;
    }

    //跳到成员列表
    public void toMemberActivity(){
        final Map<String,String> map = new HashMap<>();
        map.put("course_id",course_code);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(map, "utf-8", "showClassMember");
                try {
                    memberList = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(res);
                    System.out.println(res);
                    Member member = new Member(jsonArray.getJSONObject(0),true);
                    memberList.add(member);
                    for(int i = 1 ; i < jsonArray.length() ; i++){
                        memberList.add(new Member(jsonArray.getJSONObject(i)));
                    }
                    if(memberList.get(0) == null) Toast.makeText(CourseDetailsActivity.this,"网络错误",Toast.LENGTH_SHORT);
                    else {
                        Intent intent = new Intent(CourseDetailsActivity.this, MemberActivity.class);
                        intent.putParcelableArrayListExtra("memberList",memberList);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    Toast.makeText(CourseDetailsActivity.this,"异常错误",Toast.LENGTH_SHORT);
                }
            }
        }).start();
    }
}