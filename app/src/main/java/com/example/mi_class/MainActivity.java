package com.example.mi_class;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.app.AlertDialog;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.mi_class.adapter.MainPagerAdapter;
import com.example.mi_class.domain.User;
import com.example.mi_class.domain.message_temp;
import com.example.mi_class.fragment.CourseFragment;
import com.example.mi_class.fragment.MessageFragment;
import com.example.mi_class.fragment.UserFragment;
import com.example.mi_class.mainToolbar.TabContainerView;
import com.example.mi_class.tool.HttpUtils;
import com.example.mi_class.tool.Match;
import com.example.mi_class.tool.MyWebSocket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private int fragmentIndex = 0;
    private String url = "ws://192.168.43.165:8080/ws/";
    public static User user;
//    private String url = "ws://192.168.43.165:8080/zb/";

    //String ph;
    List<message_temp> temp_ms_data;
    public static Handler handler;
    private final int[][] icons = {
            {R.drawable.ic_courses,R.drawable.ic_courses_checked},
            {R.drawable.ic_message,R.drawable.ic_message_checked},
            {R.drawable.ic_user,R.drawable.ic_user_checked}
    };

    private final ArrayList<Fragment> fragments = new ArrayList<>(Arrays.asList(
            new CourseFragment(),
            new MessageFragment(),
            new UserFragment()
    ));
    private static final int getMsData = 100;
    private static final int teaAddCourse = 300;
    private static final int stuAddCourse = 301;
    private int[] TAB_COLORS = {
            R.color.main_bottom_tab_textcolor_normal,
            R.color.main_bottom_tab_textcolor_selected};


    private ViewPager viewPager ;
    private String identity,ph;
    private ImageView tea_dialog_cha,stu_dialog_cha;
    private View dialogView;
    private AlertDialog alterDialog;
    private boolean connection_flag;
    private boolean tea_add_course_flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);
        setTitle("全部课程");
        viewPager = findViewById(R.id.view_pager);
        //避免自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        System.out.println("开始：主活动创建");
        initToolbar();
        init_data(); // 从缓存获得手机号和身份
        //开始连接ws
        MyWebSocket.OK = true;
        if(MyWebSocket.myWebSocket == null)
        {
            System.out.println("开始：准备连接ws");
            connServer();
        }

        //加载历史记录
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                String info;
                switch (msg.what){
                    case getMsData:
                        SharedPreferences preferences = getSharedPreferences(ph+"_ms",MODE_PRIVATE);
                        SharedPreferences.Editor ed = preferences.edit();
                        ed.putString("message_list",(String)msg.getData().getString("res"));
                        System.out.println("ook拿到数据:"+(String)msg.getData().getString("res"));
                        break;
                    case teaAddCourse:
                        info = msg.getData().getString("info");
                        Log.d("insertCourse_info",info);
                        if(info.equals("1")){
                            // 关闭对话框
                            alterDialog.cancel();
                            // 向课程碎片发送消息重新加载布局
                            Message message = new Message();
                            message.what = 330;
                            CourseFragment.course_handler.sendMessage(message);
                            Toast.makeText(MainActivity.this,"创建成功",Toast.LENGTH_LONG).show();
                        }else if(info.equals("0")){
                            Toast.makeText(MainActivity.this,"创建失败",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this,"网络错误",Toast.LENGTH_LONG).show();
                        }
                        break;
                    case stuAddCourse:
                        info = msg.getData().getString("info");
                        Log.d("studentAdd",info);
                        if(info.equals("11")){
                            alterDialog.cancel();
                            Message message = new Message();
                            message.what = 330;
                            CourseFragment.course_handler.sendMessage(message);
                            Toast.makeText(MainActivity.this,"添加成功",Toast.LENGTH_LONG).show();
                        }else if(info.equals("0")) {
                            Toast.makeText(MainActivity.this,"添加失败",Toast.LENGTH_LONG).show();
                        }else if(info.equals("10")) {
                            Toast.makeText(MainActivity.this,"无该门课程",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };




    }


    //websocket 连接
    public void connServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //ph = getSharedPreferences("user_login_info",MODE_PRIVATE).getString("phone","");
                    init_data();
                    if(ph.equals("")){
                        //无登陆态
                    }else{
                        //有登陆
                        String u = url + ph;
                        System.out.println(u);
                        if(MyWebSocket.myWebSocket == null){
                            MyWebSocket.myWebSocket = new MyWebSocket(u);
                            if (MyWebSocket.myWebSocket.connectBlocking()) {
                                connection_flag = true;
                                Log.i("s", "run: 连接服务器成功");
                            } else {
                                connection_flag = false;
                                Log.i("s", "run: 连接服务器失败");
//                                Thread.sleep(1000);
//                                Message m = new Message();
//                                m.what = MessageFragment.getMsData;
//                                if(MessageFragment.handler != null && !MyWebSocket.myWebSocket.getReadyState().equals(ReadyState.OPEN))
//                                {
//                                    System.out.println("msgFragment");
//                                    MessageFragment.handler.sendMessage(m);
//                                }



                            }
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void initToolbar() {        //加载导航栏
        MainPagerAdapter mAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(mAdapter);

        TabContainerView tabContainerView = findViewById(R.id.main_toolbar);
        tabContainerView.setOnPageChangeListener(this);

        tabContainerView.initContainer(getResources().getStringArray(R.array.tab_main_title), icons, TAB_COLORS, true);

        int width = getResources().getDimensionPixelSize(R.dimen.tab_icon_width);
        int height = getResources().getDimensionPixelSize(R.dimen.tab_icon_height);
        tabContainerView.setContainerLayout(R.layout.tab_container_view, R.id.iv_tab_icon, R.id.tv_tab_text, width, height);
        tabContainerView.setViewPager(viewPager);
        viewPager.setCurrentItem(getIntent().getIntExtra("tab", 0));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        getActionBar().setTitle();
        fragmentIndex = position;
    }

    @Override
    public void onPageSelected(int position) {
        fragmentIndex = position;
        int index = 0;
        int len = fragments.size();
        while (index < len) {
            fragments.get(index).onHiddenChanged(index != position);
            index++;
        }
        switch (position){
            case 0 : setTitle("全部课程");break;
            case 1 : setTitle("消息");break;
            case 2 : setTitle("我的");break;
         }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_add_btn, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if (item.getItemId() == R.id.action_cart) {//监听菜单按钮
            showCourseDialog();
           }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem item = menu.findItem(R.id.action_cart);
        item.setVisible(fragmentIndex == 0);

        return super.onPrepareOptionsMenu(menu);
    }
    public void showCourseDialog(){
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        if(identity.equals("S")){
            //Toast.makeText(this, "学生添加课程", Toast.LENGTH_SHORT).show();
            dialogView = inflater.inflate(R.layout.activity_dialog_student,null);
            alterDialog = new AlertDialog.Builder(MainActivity.this).create();
            //alterDialog.setView(dialogView);
            alterDialog.show();
            alterDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            alterDialog.setCancelable(false);
            Window window = alterDialog.getWindow();
            //去掉背景白色实现对话框四个角完全曲化
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setContentView(dialogView);
            stu_dialog_cha = dialogView.findViewById(R.id.stu_dialog_cha);
            final EditText insert_course_code = dialogView.findViewById(R.id.insert_course_code);
            final Button stu_btn_submit = (Button) dialogView.findViewById(R.id.stu_btn_submit);
            stu_dialog_cha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alterDialog.cancel();
                }
            });
            insert_course_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    change_edit_style(b,insert_course_code);
                }
            });
            // 学生点击确定按钮
            stu_btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("shiihisihi");
                    String code = insert_course_code.getText().toString();
                    Log.d("studentque","stu"+code+ph);
                    if(code.equals("")){
                        insert_course_code.setBackgroundResource(R.drawable.edit_back_error);
                        Toast.makeText(MainActivity.this,"请输入课程码",Toast.LENGTH_SHORT).show();
                    }else if(code.length()!=6){
                        insert_course_code.setBackgroundResource(R.drawable.edit_back_error);
                        Toast.makeText(MainActivity.this,"请输入六位课程码",Toast.LENGTH_SHORT).show();
                    }else if(Match.char_mobile(code)){
                        //添加课程
                        Log.d("studentque","stu"+code+ph);
                        stu_add_course(code ,ph);
                    }else {
                        insert_course_code.setBackgroundResource(R.drawable.edit_back_error);
                        Toast.makeText(MainActivity.this,"不能输入特殊字符",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            //Toast.makeText(this, "教师添加课程", Toast.LENGTH_SHORT).show();
            dialogView = inflater.inflate(R.layout.activity_dialog_teacher,null);
            alterDialog = new AlertDialog.Builder(MainActivity.this).create();
            //alterDialog.setView(dialogView);
            alterDialog.show();
            alterDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            alterDialog.setCancelable(false);
            Window window = alterDialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setContentView(dialogView);
            tea_dialog_cha = dialogView.findViewById(R.id.tea_dialog_cha);
            final EditText insert_course_introduce = dialogView.findViewById(R.id.insert_course_introduce);
            final EditText insert_course_name = dialogView.findViewById(R.id.insert_course_name);
            final TextView course_introduce_num = dialogView.findViewById(R.id.course_introduce_num);
            final Button tea_btn_submit = (Button) dialogView.findViewById(R.id.tea_btn_submit);
            tea_dialog_cha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alterDialog.cancel();
                }
            });
            // 获得焦点改变视图
            insert_course_introduce.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    change_edit_style(b,insert_course_introduce);
                }
            });
            insert_course_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    change_edit_style(b,insert_course_name);
                }
            });
            // 限制课程描述字数小于300
            insert_course_introduce.addTextChangedListener(new TextWatcher() {
                CharSequence temp;
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    temp = charSequence;
                    if(temp.length()==300){
                        insert_course_introduce.setBackgroundResource(R.drawable.edit_back_error);
                    } else{
                        insert_course_introduce.setBackgroundResource(R.drawable.edit_back_onfocus);
                    }
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    course_introduce_num.setText(charSequence.length()+"/300");
                    if(temp.length()==300){
                        insert_course_introduce.setBackgroundResource(R.drawable.edit_back_error);
                        //Toast.makeText(getApplicationContext(),"课程描述最多300字",Toast.LENGTH_SHORT).show();
                    } else{
                        insert_course_introduce.setBackgroundResource(R.drawable.edit_back_onfocus);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int editStart = insert_course_introduce.getSelectionStart();
                    int editEnd = insert_course_introduce.getSelectionEnd();
                    if (temp.length() > 300) {
                        editable.delete(editStart - 1, editEnd);
                        insert_course_introduce.setText(editable);
                        insert_course_introduce.setSelection(editable.length());
                    }
                }
            });

            // 老师点击确定按钮
            tea_btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String course_name,course_introduce;
                    course_name = insert_course_name.getText().toString();
                    course_introduce = insert_course_introduce.getText().toString();
                    if(course_name.equals("")){
                        Toast.makeText(MainActivity.this,"课程名称不能为空",Toast.LENGTH_SHORT).show();
                        insert_course_name.setBackgroundResource(R.drawable.edit_back_error);
                    }else if(Match.char_mobile(course_name) && Match.char_mobile(course_introduce)){  //防止特殊字符
                        //System.out.println("res"+(Match.char_mobile(course_name) && Match.char_mobile(course_introduce)));
                        // 创建课程
                        tea_add_course(course_name,course_introduce,ph);
                    }else{
                        Toast.makeText(MainActivity.this,"不能输入特殊字符",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // 编辑框获得焦点的样式
    public void change_edit_style(boolean b,EditText editText){
        if(b)
            editText.setBackgroundResource(R.drawable.edit_back_onfocus);
        else
            editText.setBackgroundResource(R.drawable.edit_back);
    }

    // 从本地缓存初始化手机号和身份
    public void init_data(){
        SharedPreferences sp = getSharedPreferences("user_login_info",MODE_PRIVATE);
        ph = sp.getString("phone","");
        identity = sp.getString("identity","");
    }

    // 老师创建课程
    public void tea_add_course(String name,String introduce,String phone_number){
        final Map<String,String> params = new HashMap<String,String>();

        //Base64Utils.encodeToString("a".getBytes());
        //new String(Base64Utils.decodeFromString("s"));
        params.put("teacher_phone",phone_number);
        params.put("course_name",name);
        params.put("course_introduce",introduce);
        Log.d("insterCourse",name);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","insertCourse");
                Log.d("inster",res);
                Message message = new Message();
                Bundle b = new Bundle();
                b.putString("info",res);
                message.setData(b);
                message.what = 300;
                handler.sendMessage(message);
            }
        }).start();
    }

    public void stu_add_course(String code ,String phone){
        Log.d("studentque","stu2"+code+phone);
        final Map<String,String> params = new HashMap<>();
        params.put("course_id",code);
        params.put("stu_phone",phone);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","insertCourseLog");
                Message message = new Message();
                Bundle b = new Bundle();
                b.putString("info",res);
                message.setData(b);
                message.what = 301;
                handler.sendMessage(message);
            }
        }).start();
    }

}