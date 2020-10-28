package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi_class.R;
import com.example.mi_class.domain.File;
import com.example.mi_class.tool.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HworkDetailActivity extends AppCompatActivity {

    private Map<String,String> params;

    private TextView fb_textview,jz_textview,file_list_text,stu_list_text;
    private EditText title_edittext,value_edittext;

    private ListView file_list_view;

    private List<File> homework_file_list;

    private String course_code,identity,info,jz_time,fb_time,title,value;

    private int state; //截止和提交，    t未截止0，已截止1, s未截止-未提交2，s未截止-已提交3，s已截止-未提交4, s已截止-已提交4

    private final static int delete_homework = 205;

    String downUrl = "http://192.168.137.1:8080/homework/download";
    String posturl = "http://192.168.137.1:8080/homework/upload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_hwork_detail);


        initInfo();

        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("作业详情");
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
//            返回按钮
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.delete_work:  //删除作业
                AlertDialog.Builder builder = new AlertDialog.Builder(HworkDetailActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定删除作业吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteHomework(course_code,fb_time);
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

                return true;
            case R.id.check_stu:    //查看未交学生

        }
        return super.onOptionsItemSelected(item);
    }

    //    右上角按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(identity.equals("T")) {
            getMenuInflater().inflate(R.menu.homework_detail_btn,menu);
            return super.onCreateOptionsMenu(menu);
        }
        else
            return false;

    }

    //    初始化信息
    private void initInfo(){

        Intent intent = getIntent();
        course_code = intent.getStringExtra("course_code");
        fb_time = intent.getStringExtra("fb_time");
        jz_time = intent.getStringExtra("jz_time");
        value = intent.getStringExtra("value");
        title = intent.getStringExtra("title");
        state = intent.getIntExtra("state",0);
        System.out.println("homeworkstate"+state);
        identity = intent.getStringExtra("identity");
//        获得活动传递的作业列表
        String homeworkFileList =  intent.getStringExtra("homework_file_list");
        System.out.println("homeworkListfile"+homeworkFileList);

        file_list_text = (TextView) findViewById(R.id.file_list_text);
        stu_list_text = (TextView) findViewById(R.id.homework_stu_sub_list);

        if(homeworkFileList.equals("[]")){
            file_list_text.setText("无附件");
//            作业列表为空的情况
        }else
        {
            homework_file_list = get_file_list(homeworkFileList);
        }

        fb_textview = (TextView) findViewById(R.id.homework_pubdate_detail);
        jz_textview = (TextView) findViewById(R.id.homework_subdate_detail);
        title_edittext = (EditText) findViewById(R.id.homework_title_detail);
        value_edittext = (EditText) findViewById(R.id.homework_detail_detail);
        file_list_view = (ListView) findViewById(R.id.homework_file_list);

//        File file = new File();


        fb_textview.setText("发布时间："+fb_time);
        jz_textview.setText("截止时间："+jz_time);
        title_edittext.setText(title);
        value_edittext.setText(value);



    }

    //解析文件列表(String->List<File>
    public List<File> get_file_list(String str){
        List<File> list = new ArrayList<>();
        String name,type;
        int typeLevel;
        try{
            JSONArray array = new JSONArray(str);
            for(int i=0;i<array.length();i++){
                File file = new File();
                JSONObject jsonObject = array.getJSONObject(i);
                file.setName(jsonObject.getString("file_name"));    //文件名
                name = jsonObject.getString("file_name");
                file.setTime(jsonObject.getString("fb_time"));  //发布时间
                file.setSize(jsonObject.getString("file_size"));    //文件大小
                System.out.println("homeworklistmode"+jsonObject.getString("mode"));    //stu和tea
                file.setId(jsonObject.getString("file_id"));
                //设置文件类型
                type=getFileType(name);
                System.out.println("name是："+name);
                System.out.println("type是："+type);
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

    //捕获文件的类型
    public String getFileType(String str){
        String s[] = str.split("\\.");
        System.out.println("name："+str);
        System.out.println("s[]："+s);
        System.out.println("length"+s.length);
        if(s.length>0){
            return s[s.length-1];
        }else
            return "list_null";
    }


    //删除文件
    public void deleteHomework(String code,String time){
        params = new HashMap<>();
        params.put("course_id",code);
        params.put("fb_time",time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("info", HttpUtils.sendPostMessage(params,"utf-8","homework/remove"));
                System.out.println("bundle是"+bundle);
                message.setData(bundle);
                message.what = delete_homework;
                HomeworkActivity.homework_handler.sendMessage(message);
            }
        }).start();
        HworkDetailActivity.this.finish();
    }

}