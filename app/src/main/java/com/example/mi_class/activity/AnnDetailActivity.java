package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi_class.R;
import com.example.mi_class.tool.HttpUtils;
import com.example.mi_class.tool.Match;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AnnDetailActivity extends AppCompatActivity {

    private String course_code,ann_title,ann_content,ann_time,identity;
    private TextView ann_title_view,ann_content_view,ann_time_view;
    private MenuItem item;
    private Handler handler;
    private final static int DELETE_ANN = 360;
    private final static int CHANGE_ANN = 361;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_ann_detail);
        // 设置标题
        setTitle("公告详情");
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ann_content_view = findViewById(R.id.ann_detail_content);
        ann_title_view = findViewById(R.id.ann_detail_title);
        ann_time_view = findViewById(R.id.ann_detail_time);
        // 初始化数据
        init_data();

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                String info = msg.getData().getString("info");
                super.handleMessage(msg);
                switch (msg.what){
                    case DELETE_ANN:
                        if(info.equals("1")){
                            Toast.makeText(AnnDetailActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            // 跳转到公告列表发消息刷新列表
                            Message m = new Message();
                            m.what = 352;
                            AnnouncementActivity.AnnListHandler.sendMessage(m);
                            Intent intent = new Intent(AnnDetailActivity.this,AnnouncementActivity.class);
                            startActivity(intent);
                        } else if(info.equals("0")){
                            Toast.makeText(AnnDetailActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AnnDetailActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case CHANGE_ANN:
                        Log.d("change",info);
                        if(info.equals("1")){
                            Toast.makeText(AnnDetailActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                            // 跳转到公告列表发消息刷新列表
                            dialog.cancel();
                            Message m = new Message();
                            m.what = 352;
                            AnnouncementActivity.AnnListHandler.sendMessage(m);
                            Intent intent = new Intent(AnnDetailActivity.this,AnnouncementActivity.class);
                            startActivity(intent);
                        } else if(info.equals("0")) {
                            Toast.makeText(AnnDetailActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AnnDetailActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.edit_ann:
                show_ann_dialog();
                return true;
            case R.id.delete_ann:
                delete_dialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(identity.equals("S")){
            menu.setGroupVisible(R.menu.main_add_btn,false);
        } else {
            setIconEnable(menu,true);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.edit_annountment,menu);
        }
        return true;
    }

    // 显示菜单栏中的图标
    public void setIconEnable(Menu menu,boolean b){
        if(menu != null){
            try{
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                method.setAccessible(true);
                method.invoke(menu,b);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void init_data(){
        Intent intent = getIntent();
        ann_title = intent.getStringExtra("ann_title");
        ann_content = intent.getStringExtra("ann_content");
        ann_time = intent.getStringExtra("ann_time");
        course_code = intent.getStringExtra("course_code");
        SharedPreferences sp = getSharedPreferences("user_login_info",MODE_PRIVATE);
        //phone_number = sp.getString("phone","");
        identity = sp.getString("identity","");

        ann_title_view.setText(ann_title);
        ann_content_view.setText(ann_content);
        ann_time_view.setText(ann_time.substring(0,16));
    }

    // 确认删除对话框
    public void delete_dialog(){
        LayoutInflater inflater = LayoutInflater.from(AnnDetailActivity.this);
        final View dialogView;
        final AlertDialog alterDialog;
        Button confirm;
        TextView cancel,title;
        ImageView dialog_close;

        dialogView = inflater.inflate(R.layout.dialog_delete_confirm,null);
        alterDialog = new AlertDialog.Builder(AnnDetailActivity.this).create();
        alterDialog.show();
        alterDialog.setCancelable(false);
        Window window = alterDialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);
        cancel =  dialogView.findViewById(R.id.cancel);
        confirm = dialogView.findViewById(R.id.confirm);
        title = dialogView.findViewById(R.id.text_title);
        dialog_close = dialogView.findViewById(R.id.dialog_close);
        title.setText("确认删除公告");
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
                delete_ann(course_code,ann_time);
            }
        });
    }

    public void show_ann_dialog(){
        LayoutInflater inflater = LayoutInflater.from(AnnDetailActivity.this);
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_add_ann,null);
        ImageView dialog_close;
        final TextView ann_num,dialog_title;
        final EditText ann_name_view,ann_content_view;
        Button submit;
        final AlertDialog alertDialog = new AlertDialog.Builder(AnnDetailActivity.this).create();
        alertDialog.show();
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        //去掉背景白色实现对话框四个角完全曲化
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);
        dialog_close = dialogView.findViewById(R.id.dialog_ann_cha);
        ann_name_view = dialogView.findViewById(R.id.insert_ann_name);
        ann_content_view = dialogView.findViewById(R.id.insert_ann_content);
        ann_num = dialogView.findViewById(R.id.ann_num);
        submit = dialogView.findViewById(R.id.submit_insert_ann);
        dialog_title = dialogView.findViewById(R.id.dialog_title);

        dialog_title.setText("修改公告");
        ann_content_view.setText(ann_content);
        ann_name_view.setText(ann_title);
        ann_num.setText(ann_content.length()+"/300");
        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        ann_content_view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                change_edit_style(b,ann_content_view);
            }
        });
        ann_name_view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                change_edit_style(b,ann_name_view);
            }
        });
        // 限制课程描述字数小于300
        ann_content_view.addTextChangedListener(new TextWatcher() {
            CharSequence temp;
            @SuppressLint("SetTextI18n")
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                temp = charSequence;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ann_num.setText(charSequence.length()+"/300");
                if(temp.length()==300){
                    ann_content_view.setBackgroundResource(R.drawable.edit_back_error);
                } else{
                    ann_content_view.setBackgroundResource(R.drawable.edit_back_onfocus);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                int editStart = ann_content_view.getSelectionStart();
                int editEnd = ann_content_view.getSelectionEnd();
                if (temp.length() > 300) {
                    editable.delete(editStart - 1, editEnd);
                    ann_content_view.setText(editable);
                    ann_content_view.setSelection(editable.length());
                }
            }
        });
        // 点击确定按钮
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title,content;
                title = ann_name_view.getText().toString();
                content = ann_content_view.getText().toString();
                if(title.equals("")){
                    Toast.makeText(AnnDetailActivity.this,"公告标题不能为空",Toast.LENGTH_SHORT).show();
                    ann_name_view.setBackgroundResource(R.drawable.edit_back_error);
                }else if(content.equals("")){
                    Toast.makeText(AnnDetailActivity.this,"公告内容不能为空",Toast.LENGTH_SHORT).show();
                    ann_content_view.setBackgroundResource(R.drawable.edit_back_error);
                }else if(Match.char_mobile(title) && Match.char_mobile(content)){  //防止特殊字符
                    change_ann(course_code,title,content,ann_time);
                    dialog = alertDialog;
                }else if(!Match.char_mobile(title)){
                    ann_name_view.setBackgroundResource(R.drawable.edit_back_error);
                    Toast.makeText(AnnDetailActivity.this,"不能输入特殊字符",Toast.LENGTH_SHORT).show();
                }else if(!Match.char_mobile(content)){
                    ann_content_view.setBackgroundResource(R.drawable.edit_back_error);
                    Toast.makeText(AnnDetailActivity.this,"不能输入特殊字符",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    // 改变编辑框的视图
    public void change_edit_style(boolean b, EditText editText){
        if(b)
            editText.setBackgroundResource(R.drawable.edit_back_onfocus);
        else
            editText.setBackgroundResource(R.drawable.edit_back);
    }

    // 老师删除公告
    public void delete_ann(String code,String time){
        final Map<String,String> params = new HashMap<>();
        params.put("course_id",code);
        params.put("fb_time",time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","notice/remove");
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("info",res);
                message.setData(bundle);
                message.what = DELETE_ANN;
                handler.sendMessage(message);
            }
        }).start();
    }

    // 老师修改公告
    public void change_ann(String code,String title,String content,String time){
        final Map<String,String> params = new HashMap<>();
        params.put("course_id",code);
        params.put("title",title);
        params.put("value",content);
        params.put("fb_time",time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","notice/change");
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("info",res);
                message.setData(bundle);
                message.what = CHANGE_ANN;
                handler.sendMessage(message);
            }
        }).start();
    }


}