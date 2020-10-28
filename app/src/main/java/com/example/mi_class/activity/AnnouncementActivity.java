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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi_class.MainActivity;
import com.example.mi_class.R;
import com.example.mi_class.adapter.AnnouncementAdapter;
import com.example.mi_class.domain.Announcement;
import com.example.mi_class.tool.HttpUtils;
import com.example.mi_class.tool.Match;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnouncementActivity extends AppCompatActivity implements View.OnClickListener{

    private List<Announcement> announcement_list;
    private AnnouncementAdapter announcement_adapter;
    private ListView announcement_list_view;
    private TextView ann_null;
    private ImageView add_ann;
    private Map<String,String> params;
    private String info,identity,course_code;
    public static Handler AnnListHandler;
    private final static int ADD_ANN = 350;
    private final static int GET_ANN_LIST = 351;
    private final static int SET_ANN_LIST = 352;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_announcement);


        // 设置标题
        setTitle("公告");
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        announcement_list_view = findViewById(R.id.announcement_list_view);
        add_ann = findViewById(R.id.add_ann);
        ann_null = findViewById(R.id.ann_null);

        // 初始化数据
        init_data();


        add_ann.setOnClickListener(this);

        announcement_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(AnnouncementActivity.this,AnnDetailActivity.class);
                intent.putExtra("course_code",course_code);
                intent.putExtra("ann_title",announcement_list.get(i).getAnnouncement_name());
                intent.putExtra("ann_content",announcement_list.get(i).getAnnouncement_content());
                intent.putExtra("ann_time",announcement_list.get(i).getAnnouncement_time());
                startActivity(intent);
            }
        });


        AnnListHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case SET_ANN_LIST:
                        get_ann_list(course_code);
                        break;
                    case ADD_ANN:
                        info = msg.getData().getString("info");
                        if(info.equals("1")){
                            Toast.makeText(AnnouncementActivity.this,"创建成功",Toast.LENGTH_SHORT).show();
                            // 关闭对话框
                            dialog.cancel();
                            // 刷新公告列表
                            get_ann_list(course_code);
                        } else if(info.equals("0")){
                            Toast.makeText(AnnouncementActivity.this,"创建失败",Toast.LENGTH_SHORT).show();
                        } else if(info.equals("2")){
                            Toast.makeText(AnnouncementActivity.this,"课程不存在",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AnnouncementActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case GET_ANN_LIST:
                        info = msg.getData().getString("info");
                        if(info.equals("-999")){
                            Toast.makeText(AnnouncementActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                        } else if(info.equals("[]")){
                            ann_null.setVisibility(View.VISIBLE);
                            announcement_list = get_new_ann_list(info);
                            announcement_adapter = new AnnouncementAdapter(AnnouncementActivity.this, R.layout.announcement_list,announcement_list );
                            announcement_list_view.setAdapter(announcement_adapter); //设置适配器，显示查询结果
                        }else {
                            ann_null.setVisibility(View.GONE);
                            announcement_list = get_new_ann_list(info);
                            announcement_adapter = new AnnouncementAdapter(AnnouncementActivity.this, R.layout.announcement_list,announcement_list );
                            announcement_list_view.setAdapter(announcement_adapter); //设置适配器，显示查询结果
                        }
                        break;
                }
            }
        };
    }

    public void init_data(){
        SharedPreferences sp = getSharedPreferences("user_login_info",MODE_PRIVATE);
        //phone_number = sp.getString("phone","");
        identity = sp.getString("identity","");
        course_code = getIntent().getStringExtra("course_code");
        // 学生隐藏创建按钮
        if(identity.equals("S"))
            add_ann.setVisibility(View.GONE);
        // 解析传过来的课程列表
        String ann_list = getIntent().getStringExtra("ann_list");
        System.out.println("annlist"+ann_list);
        if(ann_list.equals("[]")){
            ann_null.setVisibility(View.VISIBLE);
        } else {
            announcement_list = get_new_ann_list(ann_list);
            announcement_adapter = new AnnouncementAdapter(AnnouncementActivity.this, R.layout.announcement_list, announcement_list);
            announcement_list_view.setAdapter(announcement_adapter); //设置适配器，显示查询结果
        }
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
        switch (view.getId()){
            case R.id.add_ann:
                // 显示添加公告对话框
                show_ann_dialog();
                break;
            default:
                break;

        }
    }

    public void show_ann_dialog(){
        LayoutInflater inflater = LayoutInflater.from(AnnouncementActivity.this);
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_add_ann,null);
        ImageView dialog_close;
        final TextView ann_num;
        final EditText ann_name,ann_content;
        Button submit;
        final AlertDialog alertDialog = new AlertDialog.Builder(AnnouncementActivity.this).create();
        alertDialog.show();
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        //去掉背景白色实现对话框四个角完全曲化
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);
        dialog_close = dialogView.findViewById(R.id.dialog_ann_cha);
        ann_name = dialogView.findViewById(R.id.insert_ann_name);
        ann_content = dialogView.findViewById(R.id.insert_ann_content);
        ann_num = dialogView.findViewById(R.id.ann_num);
        submit = dialogView.findViewById(R.id.submit_insert_ann);
        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        ann_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                change_edit_style(b,ann_content);
            }
        });
        ann_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                change_edit_style(b,ann_name);
            }
        });
        // 限制课程描述字数小于300
        ann_content.addTextChangedListener(new TextWatcher() {
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
                    ann_content.setBackgroundResource(R.drawable.edit_back_error);
                    //Toast.makeText(getApplicationContext(),"课程描述最多300字",Toast.LENGTH_SHORT).show();
                } else{
                    ann_content.setBackgroundResource(R.drawable.edit_back_onfocus);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                int editStart = ann_content.getSelectionStart();
                int editEnd = ann_content.getSelectionEnd();
                if (temp.length() > 300) {
                    editable.delete(editStart - 1, editEnd);
                    ann_content.setText(editable);
                    ann_content.setSelection(editable.length());
                }
            }
        });
        // 点击确定按钮
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title,content;
                title = ann_name.getText().toString();
                content = ann_content.getText().toString();
                if(title.equals("")){
                    Toast.makeText(AnnouncementActivity.this,"公告标题不能为空",Toast.LENGTH_SHORT).show();
                    ann_name.setBackgroundResource(R.drawable.edit_back_error);
                }else if(content.equals("")){
                    Toast.makeText(AnnouncementActivity.this,"公告内容不能为空",Toast.LENGTH_SHORT).show();
                    ann_content.setBackgroundResource(R.drawable.edit_back_error);
                }else if(Match.char_mobile(title) && Match.char_mobile(content)){  //防止特殊字符
                    add_ann(course_code,title,content);
                    dialog = alertDialog;
                }else if(!Match.char_mobile(title)){
                    ann_name.setBackgroundResource(R.drawable.edit_back_error);
                    Toast.makeText(AnnouncementActivity.this,"不能输入特殊字符",Toast.LENGTH_SHORT).show();
                }else if(!Match.char_mobile(content)){
                    ann_content.setBackgroundResource(R.drawable.edit_back_error);
                    Toast.makeText(AnnouncementActivity.this,"不能输入特殊字符",Toast.LENGTH_SHORT).show();
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

    // 老师发布公告
    public void add_ann(String code,String title,String content){
       params = new HashMap<>();
       params.put("course_id",code);
       params.put("title",title);
       params.put("value",content);
       new Thread(new Runnable() {
           @Override
           public void run() {
               Message message = new Message();
               Bundle bundle = new Bundle();
               bundle.putString("info", HttpUtils.sendPostMessage(params,"utf-8","notice/add"));
               message.setData(bundle);
               message.what = ADD_ANN;
               AnnListHandler.sendMessage(message);
           }
       }).start();
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
                AnnListHandler.sendMessage(message);
            }
        }).start();

    }

    // 解析公告列表
    public List<Announcement> get_new_ann_list(String str){
        List<Announcement> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(str);
            for(int i=0; i<array.length();i++){
                Announcement ann = new Announcement();
                JSONObject object = array.getJSONObject(i);
                ann.setAnnouncement_name(object.getString("title"));
                ann.setAnnouncement_content(object.getString("value"));
                ann.setAnnouncement_time(object.getString("fb_time"));
                list.add(ann);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}