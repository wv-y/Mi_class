package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi_class.R;
import com.example.mi_class.adapter.HomeworkAdapter;
import com.example.mi_class.domain.File;
import com.example.mi_class.domain.Homework;
import com.example.mi_class.domain.StuLogInfo;
import com.example.mi_class.tool.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeworkActivity extends AppCompatActivity {

    private List<Homework> homework_list;
    private List<StuLogInfo> stu_homework_list;
    private List<File> stu_hworkfile_list;
    private HomeworkAdapter homework_adapter;
    private RecyclerView homework_recyclerview;
    private PopupWindow popupWindow;
    private TextView homework_null;
    private String course_code,identity,info,jz_time,fb_time,title,value,phone_number;

    private int state; //截止和提交，    t未截止0，已截止1, s未截止-未提交2，s未截止-已提交3，s已截止-未提交4, s已截止-已提交4

    private Map<String,String> params;

    public static Handler homework_handler;
    private final static int file_upLod = 200;
    private final static int get_homeworklist = 201;
    private final static int set_filelist = 202;
    private final static int delete_file = 203;
    private final static int get_homeworkfilelist = 204;
    private final static int delete_homework = 205;
    private final static int get_studentlist = 206;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_homework);

        homework_recyclerview = (RecyclerView) findViewById(R.id.homework_recycler_view);
        homework_list = new ArrayList<>();

        homework_null = (TextView) findViewById(R.id.homework_null);

//        RecyclerView 需要一个layoutManager，也就是布局管理器
//        布局管理器能确定RecyclerView内各个子视图（项目视图）的位置
//        并能决定何时重新使用对用户已不可见的项目视图
//        安卓为我们预先准备好了三种视图管理器：LinearLayoutManager、
//        GridLayoutManager、StaggeredGridLayoutManager（详见文档）
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        homework_recyclerview.setLayoutManager(layoutManager);
//        初始化数据，（固定数据测试用)
        initInfo();


        homework_adapter = new HomeworkAdapter(HomeworkActivity.this,homework_list);
        homework_recyclerview.setAdapter(homework_adapter);

//        设置item和item中控件的点击事件
        homework_adapter.setOnItemClickListener(onItemClickListener);


//        homework_adapter.setOnItemClickListener(new HomeworkAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                Intent intent = new Intent(HomeworkActivity.this,HworkDetailActivity.class);
////                intent.putExtra("homework_id",homework_id);
//                startActivity(intent);
//            }
//        });


        // 设置标题
        setTitle("所有作业");
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        homework_handler = new Handler(){
            public void handleMessage(@NonNull Message msg){
                super.handleMessage(msg);
                switch (msg.what){
                    case set_filelist:
                        break;
                    case file_upLod:
                        String file_upBack = msg.getData().getString("info");
                        if(identity.equals("T")){
                            if("添加成功".equals(file_upBack)){
                                Toast.makeText(HomeworkActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                                homework_null.setVisibility(View.GONE);
                                get_homework_data_list(course_code);
                            } else{
                                Toast.makeText(HomeworkActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            if("上传成功".equals(file_upBack)){
                                Toast.makeText(HomeworkActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(HomeworkActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
                            }
                        }


                        break;
                    case get_homeworklist:
                        info = msg.getData().getString("info");
                        System.out.println("info"+info);
                        if(info.equals("-999")) {
                            Toast.makeText(HomeworkActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                        }else if(info.equals("[]")){
                            homework_null.setVisibility(View.VISIBLE);
//                            homework_list = get_homework_list(info);
//                            homework_adapter = new HomeworkAdapter(HomeworkActivity.this,homework_list);
//                            homework_recyclerview.setAdapter(homework_adapter);
                        }else {
                            homework_null.setVisibility(View.GONE);
                            homework_list = get_homework_list(info);
                            homework_adapter = new HomeworkAdapter(HomeworkActivity.this,homework_list);
                            homework_recyclerview.setAdapter(homework_adapter);
                            homework_adapter.setOnItemClickListener(onItemClickListener);
                        }
                        break;
                    case delete_file:
                        info = msg.getData().getString("info");
                        System.out.println("info是："+info);
                        if(info.equals("删除文件")){
                            Toast.makeText(HomeworkActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            get_file_list(course_code);
                        }
                        else
                            Toast.makeText(HomeworkActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                        break;
                    case get_homeworkfilelist:
                        info = msg.getData().getString("info");


                        System.out.println("info"+info);
                        Intent intent = new Intent(HomeworkActivity.this,HworkDetailActivity.class);

                        if(identity.equals("S")){
                            String info2 = info = msg.getData().getString("info2");
                            System.out.println("infos11"+info);
                            intent.putExtra("homework_stu_file_list",info2); //文件列表
                        }

                        intent.putExtra("homework_file_list",info); //文件列表
//                    用于获取文件列表
                        intent.putExtra("course_code",course_code); //课程id
                        intent.putExtra("fb_time",fb_time); //发布时间
                        intent.putExtra("identity",identity); //身份

                        System.out.println("info"+title);
                        intent.putExtra("title",title); //标题
                        intent.putExtra("value",value); //内容
                        intent.putExtra("state",state); //状态
                        intent.putExtra("jz_time",jz_time); //截止时间

                        intent.putExtra("phone_number",phone_number); //截止时间
                        startActivity(intent);
                        break;
                    case delete_homework:
                        info = msg.getData().getString("info");
                        System.out.println("info是："+info);
                        if(info.equals("删除成功")){
                            Toast.makeText(HomeworkActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            get_homework_data_list(course_code);
                        }
                        else
                            Toast.makeText(HomeworkActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                        break;
                    case get_studentlist:
                        info = msg.getData().getString("info");
                        System.out.println("infos"+info);
                        intent = new Intent(HomeworkActivity.this,HworkCommitActivity.class);
                        intent.putExtra("stu_info_list",info); //学生列表
                        intent.putExtra("course_code",course_code); //课程id
                        startActivity(intent);
                        break;

                }

            }
        };
    }

//    列表点击事件
    private HomeworkAdapter.OnItemClickListener onItemClickListener = new HomeworkAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, HomeworkAdapter.ViewName viewName, int position) {
            title = homework_list.get(position).getTitle();
            value = homework_list.get(position).getDetail();
            state = homework_list.get(position).getState();
            jz_time = homework_list.get(position).getSubtime();
            fb_time = homework_list.get(position).getPubtime();
            get_homework_file_list(course_code,fb_time);
            if(identity.equals("S")){
                get_stu_homework_file_list(course_code,fb_time,phone_number);
            }
//            switch (v.getId()){
//                case R.id.homework_sub_button:
//                    title = homework_list.get(position).getTitle();
//                    value = homework_list.get(position).getDetail();
//                    state = homework_list.get(position).getState();
//                    jz_time = homework_list.get(position).getSubtime();
//                    fb_time = homework_list.get(position).getPubtime();
//                    get_homework_file_list(course_code,fb_time);
////                    Toast.makeText(HomeworkActivity.this,"点击了按钮"+(position+1),Toast.LENGTH_LONG).show();
//                    break;
////                    if(identity.equals("T")){
////
////                    }
//                default:
//                    title = homework_list.get(position).getTitle();
//                    value = homework_list.get(position).getDetail();
//                    state = homework_list.get(position).getState();
//                    jz_time = homework_list.get(position).getSubtime();
//                    fb_time = homework_list.get(position).getPubtime();
//                    get_homework_file_list(course_code,fb_time);
////                    Toast.makeText(HomeworkActivity.this,"点击了item"+(position+1),Toast.LENGTH_LONG).show();
//                    break;
//            }
        }

        @Override
        public void onItemLongClick(View v) {
            Toast.makeText(HomeworkActivity.this,"长按了item",Toast.LENGTH_LONG).show();
        }
    };


//    初始化列表信息
    private void initInfo(){

        Intent intent = getIntent();
        course_code = intent.getStringExtra("course_code");
        identity = intent.getStringExtra("identity");
        phone_number = intent.getStringExtra("phone_number");
//        获得活动传递的作业列表
        String homeworkList =  intent.getStringExtra("homework_list");
//        String homeworkFileList =  intent.getStringExtra("homework_file_list");
        System.out.println("homeworkList"+homeworkList);
//        System.out.println("homeworkListfile2"+homeworkFileList);
        if(homeworkList.equals("[]")){
//            作业列表为空的情况
            homework_null.setVisibility(View.VISIBLE);
        }else
        {
            homework_null.setVisibility(View.GONE);
//            homework_file_list = get_file_list(homeworkFileList);
            homework_list = get_homework_list(homeworkList);
        }

    }
    //获取作业列表
    public void get_homework_data_list(String code){
        params = new HashMap<>();
        params.put("course_id",code);
        System.out.println("SSSSSS111"+identity);
        if(identity.equals("S")){
            params.put("stu_phone",phone_number);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("info", HttpUtils.sendPostMessage(params,"utf-8","homework/getList"));
//                bundle.putString("info2", HttpUtils.sendPostMessage(params,"utf-8","homework/getInfoFileList"));
                message.setData(bundle);
                message.what = get_homeworklist;
                homework_handler.sendMessage(message);
            }
        }).start();
    }
    //获取作业文件列表
    public void get_homework_file_list(String code,String time){
        params = new HashMap<>();
        System.out.println("course_code"+code);
        params.put("course_id",code);
        params.put("fb_time",time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
//                System.out.println("course_code params"+params.get("course_id"));
                bundle.putString("info", HttpUtils.sendPostMessage(params,"utf-8","homework/getInfoFileList"));
                message.setData(bundle);
                message.what = get_homeworkfilelist;
                homework_handler.sendMessage(message);
            }
        }).start();
    }

    //获取作业文件列表
    public void get_stu_homework_file_list(String code,String time,String phone){
        params = new HashMap<>();
        params.put("course_id",code);
        params.put("fb_time",time);
        params.put("stu_phone",phone);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                Bundle bundle = new Bundle();
//                System.out.println("course_code params"+params.get("course_id"));
                bundle.putString("info2", HttpUtils.sendPostMessage(params,"utf-8","homework/getHomework"));
                message.setData(bundle);
                message.what = get_homeworkfilelist;
                homework_handler.sendMessage(message);
            }
        }).start();
    }

    //解析作业列表    String->List<Homework>
    public List<Homework> get_homework_list(String str){
        List<Homework> list = new ArrayList<>();
        try{
            JSONArray array = new JSONArray(str);
            for(int i=0;i<array.length();i++){
                Homework homework = new Homework();
                JSONObject jsonObject = array.getJSONObject(i);
//                作业标题
                title = jsonObject.getString("title");
                homework.setTitle(title);
//                作业内容
                value = jsonObject.getString("value");
                homework.setDetail(value);
//                作业发布时间
                fb_time = jsonObject.getString("fb_time");
                homework.setPubtime(fb_time);

//                作业附件数量
                homework.setFilenumber(Integer.valueOf(jsonObject.getString("file_count")));

//                作业截止时间（减法处理）
                String total_time  = jsonObject.getString("jz_time");
                long j_time = getToLong(total_time);
                long f_time = getToLong(fb_time);
                long s_time = j_time-f_time;    //截止时间
                jz_time = longtoString(s_time);
                homework.setSubtime(jz_time);

                String commit = jsonObject.getString("commit");
                if(identity.equals("T")){   //老师
                    if (s_time>System.currentTimeMillis()){ //未截止
                        state = 0;
                        homework.setState(state);   //查看详情
                    }
                    else{   //已截止
                        state = 1;
                        homework.setState(state);   //已截止
                    }
                }
                else{
                    if (s_time>System.currentTimeMillis()){ //未截止
                        if (commit.equals("未提交")){
                            state = 2;
                            homework.setState(state);   //提交作业
                        }
                        else {
                            state = 3;
                            homework.setState(state);   //更新提交
                        }
                    }
                    else{   //已截止
                        if (commit.equals("未提交")){
                            state = 4;
                            homework.setState(state);   //未提交
                        }
                        else {
                            state = 5;
                            homework.setState(state);   //已提交
                        }
                    }
                }
                list.add(homework);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    //将字符串时间格式转为long形
    public long getToLong(String DateTime) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date time = sdf.parse(DateTime);
        return time.getTime();
    }
    public String longtoString(long time){
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
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

//    标题栏按钮点击事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
//            返回按钮
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_cart:
                initPopupWindow();
                break;

        }
        return super.onOptionsItemSelected(item);
    }



//    右上角按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(identity.equals("T")) {
            getMenuInflater().inflate(R.menu.main_add_btn,menu);
            return true;
        }else
            return false;
    }


//    弹出框初始化
    public void initPopupWindow(){
        View popupView  = HomeworkActivity.this.getLayoutInflater().inflate(R.layout.popwindow_homework,null);
        popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT,true);

        popupWindow.setContentView(popupView);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        Button new_homework = (Button)popupView.findViewById(R.id.popwindow_new_homework_button);
        Button cancel = (Button) popupView.findViewById(R.id.popwindow_cancel_button);

//                菜单背景色
        ColorDrawable colorDrawable = new ColorDrawable(0xffffffff);
        popupWindow.setBackgroundDrawable(colorDrawable);



        View rootView =  LayoutInflater.from(HomeworkActivity.this).inflate(R.layout.activity_homework,null);

//        设置屏幕透明度
        backgroundAlpha(0.5f);

        popupWindow.setOnDismissListener(new popupDismissListener());
//        位置
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);

//                点击事件
        new_homework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeworkActivity.this,HworkAddActivity.class);
                popupWindow.dismiss();
                intent.putExtra("course_code",course_code);
                intent.putExtra("identity",identity);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }

    /**
     * 改回背景透明度
     */
    class popupDismissListener implements PopupWindow.OnDismissListener{
        @Override
        public void onDismiss() {
            backgroundAlpha(1.0f);
        }
    }

    /**
     * 设置屏幕背景透明度
     * @param bgAlaph
     */
    public void backgroundAlpha(float bgAlaph){
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = bgAlaph;   //0.0-1.0
        getWindow().setAttributes(layoutParams);
    }
}