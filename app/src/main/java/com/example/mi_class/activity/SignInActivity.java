package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi_class.R;
import com.example.mi_class.adapter.SignInAdapter;
import com.example.mi_class.domain.SignIn;
import com.example.mi_class.tool.HttpUtils;
import com.example.mi_class.tool.QrcodeTool;
import com.example.mi_class.tool.process_dialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private List<SignIn> sign_list;
    private SignInAdapter sign_adapter;
    private ListView sign_list_view;
    private ImageView ann_sign_in;
    private TextView sign_in_null;
    private String identity, course_code, phone_number, string_sign_list;
    private String style, way_stu, way_tea, sign_code; // 签到方式
    private String sign_name, sign_time, start_time;
    private Map<String,String> params;
    private final static int ADD_SIGN = 360;
    private final static int TEA_GET_SIGN_LIST = 361;
    private final static int STU_GET_SIGN_LIST = 362;
    private final static int STU_QD = 363;
    private final static int GPS_TIME = 364;
    private final static int GO_SIGN_DETAIL = 365;
    private Handler handler;
    private AlertDialog dialog;
    private int index;
    private double longitude, latitude; // 经纬度
    private LocationManager locationManager;
    private process_dialog progressDialog;
    private int time = 5;
    private String gps_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_sign_in);

        // 设置标题
        setTitle("签到列表");
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        sign_list_view = (ListView) findViewById(R.id.sign_in_list_view);
        ann_sign_in = findViewById(R.id.add_sign_in);
        sign_in_null = findViewById(R.id.sign_in_null);

        init_data();
        ann_sign_in.setOnClickListener(this);

        sign_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(identity.equals("S")){
                    // 学生端
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    String now_time = df.format(new Date());
                    // 未超时
                    if(sign_list.get(i).getSign_style().equals("未签到")){
                        if(now_time.compareTo(sign_list.get(i).getEnd_time()) < 0 ){
                            switch (sign_list.get(i).getWay()){
                                case "num":
                                    way_stu = "num";
                                    stu_dialog_edit_code(i);
                                    break;
                                case "two":
                                    index = i;
                                    way_stu = "two";
                                    Intent intent=new Intent(SignInActivity.this,QrcodeTool.class);
                                    startActivityForResult(intent,3000);
                                    break;
                                case "GPS":
                                    index = i;
                                    way_stu = "GPS";
                                    openGPSSettings();
                                    break;
                            }
                        }else{
                            Toast.makeText(SignInActivity.this,"签到已截止",Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    // 老师端
                    way_tea = sign_list.get(i).getWay();
                    sign_code = sign_list.get(i).getValue();
                    start_time = sign_list.get(i).getStart_time();
                    get_sign_detail(course_code,sign_list.get(i).getStart_time());

                }
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String info;
                switch (msg.what){
                    case GPS_TIME:
                        progressDialog.dismiss();
                        locationManager.removeUpdates(mListener);
                        show_address();
                        break;
                    case ADD_SIGN:
                        // 老师发布签到
                        info = msg.getData().getString("info");
                        String way = msg.getData().getString("way");
                        if(info.equals("-999")){
                            Toast.makeText(SignInActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                        } else if(info.equals("课程不存在")){
                            Toast.makeText(SignInActivity.this,"课程不存在",Toast.LENGTH_SHORT).show();
                        } else {
                            System.out.println("wayinfo"+way);
                            // 刷新列表
                            tea_get_sign_list();
                            switch (way){
                                case "num":
                                    // 创建成功关闭创建签到的会话框
                                    dialog.cancel();
                                    // 显示签到码
                                    show_code_dialog(info);
                                    break;
                                case "two":
                                    dialog.cancel();
                                    // 显示二维码
                                    show_dialog_two(info);
                                    break;
                                case "GPS":
                                    dialog.cancel();
                                    Toast.makeText(SignInActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                        break;
                    case TEA_GET_SIGN_LIST:
                        info = msg.getData().getString("info");
                        if(info.equals("[]")){
                            sign_in_null.setVisibility(View.VISIBLE);
                        } else {
                            sign_in_null.setVisibility(View.GONE);
                        }
                        System.out.println("info"+info);
                        sign_list = tea_get_local_sign_list(info);
                        sign_adapter = new SignInAdapter(SignInActivity.this, R.layout.sign_in_list,sign_list);     //初始化适配器
                        sign_list_view.setAdapter(sign_adapter); //设置适配器，显示查询结果
                        break;
                    case STU_GET_SIGN_LIST:
                        info = msg.getData().getString("info");
                        sign_list = stu_get_local_sign_list(info);
                        sign_adapter = new SignInAdapter(SignInActivity.this,R.layout.sign_in_list,sign_list);
                        sign_list_view.setAdapter(sign_adapter);
                        break;
                    case STU_QD:
                        info = msg.getData().getString("info");
                        Log.d("qiandao",info);
                        if(info.equals("-999")){
                            Toast.makeText(SignInActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                        } else if(info.equals("签到成功")) {
                            switch (way_stu){
                                case "num":
                                    dialog.cancel();
                                    Toast.makeText(SignInActivity.this,info,Toast.LENGTH_SHORT).show();
                                    stu_get_sign_list();
                                    break;
                                case "two":
                                    Toast.makeText(SignInActivity.this,info,Toast.LENGTH_SHORT).show();
                                    stu_get_sign_list();
                                    break;
                                case "GPS":
                                    dialog.cancel();
                                    Toast.makeText(SignInActivity.this,info,Toast.LENGTH_SHORT).show();
                                    stu_get_sign_list();
                                    break;
                            }
                        } else {
                            Toast.makeText(SignInActivity.this,info,Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case GO_SIGN_DETAIL:
                        info = msg.getData().getString("info");
                        if(info.equals("-999")){
                            Toast.makeText(SignInActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                        } else if(info.equals("[]") || info == null){
                            Toast.makeText(SignInActivity.this,"尚未有学生加入",Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent( SignInActivity.this,SignInDetailActivity.class);
                            intent.putExtra("sign_code",sign_code);
                            intent.putExtra("way_tea",way_tea);
                            intent.putExtra("start_time",start_time);
                            intent.putExtra("course_id",course_code);
                            intent.putExtra("sign_list",info);
                            startActivity(intent);
                        }
                        break;
                }
            }
        };

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3000)
            if(resultCode==RESULT_OK){
                System.out.println("result"+data.getStringExtra("qrcode"));
                stu_sign_in(index,data.getStringExtra("qrcode"));

            }

    }

    public void init_data(){
        Intent intent = getIntent();
        identity = intent.getStringExtra("identity");
        course_code = intent.getStringExtra("course_code");
        string_sign_list = intent.getStringExtra("sign_list");
        phone_number = intent.getStringExtra("phone_number");
        // 学生隐藏添加按钮
        if(identity.equals("S"))
            ann_sign_in.setVisibility(View.GONE);
        if(string_sign_list.equals("[]")){
            sign_in_null.setVisibility(View.VISIBLE);
        } else {
            sign_in_null.setVisibility(View.GONE);
            // 解析签到列表
            if(identity.equals("T")) {
                sign_list = tea_get_local_sign_list(string_sign_list);
            }
            else{
                sign_list = stu_get_local_sign_list(string_sign_list);
            }
            sign_adapter = new SignInAdapter(SignInActivity.this, R.layout.sign_in_list,sign_list);     //初始化适配器
            sign_list_view.setAdapter(sign_adapter);
        }
    }

    @Override
    public void onStart() {
        if(identity.equals("T"))
            tea_get_sign_list();
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
        if (view.getId()==R.id.add_sign_in){
            // 发布签到
            show_dialog_add();
        }
    }

    // 签到码
    public void show_code_dialog(String str){
        LayoutInflater inflater = LayoutInflater.from(SignInActivity.this);
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_show_sign_code,null);
        final ImageView dialog_close;
        TextView code;
        final AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity.this).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        //去掉背景白色实现对话框四个角完全曲化
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);
        dialog_close = dialogView.findViewById(R.id.dialog_close);
        code = dialogView.findViewById(R.id.code);
        code.setText(str);
        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

    }

    // 学生填写签到码对话框
    public void stu_dialog_edit_code(final int i){
        LayoutInflater inflater = LayoutInflater.from(SignInActivity.this);
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_write_sign_code,null);
        final ImageView dialog_close;
        final EditText code;
        Button submit;
        final AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity.this).create();
        alertDialog.show();
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        //去掉背景白色实现对话框四个角完全曲化
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);
        dialog_close = dialogView.findViewById(R.id.dialog_close);
        submit = dialogView.findViewById(R.id.btn_submit);
        code = dialogView.findViewById(R.id.sign_code);
        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    code.setBackgroundResource(R.drawable.edit_back_onfocus);
                }else {
                    code.setBackgroundResource(R.drawable.edit_back);
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(code.getText().toString().equals("")){
                    Toast.makeText(SignInActivity.this,"签到码不能为空",Toast.LENGTH_SHORT).show();
                    code.setBackgroundResource(R.drawable.edit_back_error);
                }else {
                    dialog = alertDialog;
                    stu_sign_in(i, code.getText().toString());
                }
            }
        });

    }

    public void show_dialog_add(){
        LayoutInflater inflater = LayoutInflater.from(SignInActivity.this);
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_sign_in,null);
        style = "num";
        sign_name = "数字签到";
        sign_time = "300000";
        final ImageView dialog_close,num_sign_in,two_code_sign_in,GPS_sign_in;
        Button submit;
        final TextView cancel;
        final Button sign_time_view;
        final AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity.this).create();
        alertDialog.show();
        //alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        //去掉背景白色实现对话框四个角完全曲化
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);
        dialog_close = dialogView.findViewById(R.id.dialog_close);
        num_sign_in = dialogView.findViewById(R.id.num_sign_in);
        two_code_sign_in = dialogView.findViewById(R.id.two_code_sign_in);
        GPS_sign_in = dialogView.findViewById(R.id.GPS_sign_in);
        cancel = dialogView.findViewById(R.id.cancel);
        submit = dialogView.findViewById(R.id.submit);
        sign_time_view = dialogView.findViewById(R.id.sign_time);

        sign_time_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getX() >= (sign_time_view.getWidth() - sign_time_view
                            .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        sign_time_view.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_down), null);

                        final String[] list = {"5分钟后", "10分钟后", "30分钟后"};//要填充的数据
                        final ListPopupWindow listPopupWindow;
                        listPopupWindow = new ListPopupWindow(SignInActivity.this);
                        listPopupWindow.setAdapter(new ArrayAdapter<String>(SignInActivity.this,android.R.layout.simple_list_item_1, list));//用android内置布局，或设计自己的样式
                        listPopupWindow.setAnchorView(sign_time_view);//以哪个控件为基准，在该处以logId为基准
                        listPopupWindow.setModal(true);

                        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置项点击监听
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                sign_time_view.setText(list[i]);//把选择的选项内容展示在EditText上
                                sign_time = time_long(sign_time_view.getText().toString());
                                listPopupWindow.dismiss();//如果已经选择了，隐藏起来
                            }
                        });
                        listPopupWindow.show();//把ListPopWindow展示出来

                        return true;
                    }
                }
                return false;

            }
        });


        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        num_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                style = "num";
                sign_name = "数字签到";
                num_sign_in.setImageResource(R.drawable.selected);
                two_code_sign_in.setImageResource(R.drawable.select_student_or_teacher);
                GPS_sign_in.setImageResource(R.drawable.select_student_or_teacher);
            }
        });
        two_code_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                style = "two";
                sign_name = "扫码签到";
                two_code_sign_in.setImageResource(R.drawable.selected);
                num_sign_in.setImageResource(R.drawable.select_student_or_teacher);
                GPS_sign_in.setImageResource(R.drawable.select_student_or_teacher);
            }
        });
        GPS_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                style = "GPS";
                sign_name = "定位签到";
                num_sign_in.setImageResource(R.drawable.select_student_or_teacher);
                two_code_sign_in.setImageResource(R.drawable.select_student_or_teacher);
                GPS_sign_in.setImageResource(R.drawable.selected);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = alertDialog;
                switch (style){
                    case "num":
                        add_sign();
                        break;
                    case "two":
                        add_sign();
                        break;
                    case "GPS":
                        alertDialog.cancel();
                        openGPSSettings();
                        // 判断GPS权限
                        break;
                }
            }
        });
    }

    // 老师端显示二维码
    public void show_dialog_two(String code){
        LayoutInflater inflater = LayoutInflater.from(SignInActivity.this);
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_two,null);
        final ImageView dialog_close,two_code;
        final AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity.this).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        //去掉背景白色实现对话框四个角完全曲化
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);
        dialog_close = dialogView.findViewById(R.id.dialog_close);
        two_code = dialogView.findViewById(R.id.two);
        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        // 生成二维码并显示
        two_code.setImageBitmap(QrcodeTool.getQRCode(code,400,400));
    }

    // 老师发布签到
    public void add_sign(){
        params = new HashMap<>();
        params.put("course_id",course_code);
        params.put("name",sign_name);
        params.put("way",style);
        params.put("jz_long",sign_time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","qiandao/newQd");
                Message m = new Message();
                Bundle b = new Bundle();
                b.putString("info",res);
                b.putString("way",style);
                m.setData(b);
                m.what = ADD_SIGN;
                handler.sendMessage(m);
            }
        }).start();

    }

    // 计算持续时间
    public String time_long(String str){
        int time_sign = 0;
        if(str.equals("5分钟后")){
            time_sign = 5*60000;
        }
        if (str.equals("10分钟后")){
            time_sign = 10*60000;
        }
        if(str.equals("30分钟后")){
            time_sign = 30*60000;
        }
        return Integer.toString(time_sign);
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
                message.what = TEA_GET_SIGN_LIST;
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
                m.what = STU_GET_SIGN_LIST;
                handler.sendMessage(m);
            }
        }).start();
    }

    // 老师端解析得到的签到列表
    public List<SignIn> tea_get_local_sign_list(String str){
        List<SignIn> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(str);
            for(int i =0;i<array.length();i++){
                SignIn sign = new SignIn();
                JSONObject object = array.getJSONObject(i);
                sign.setSign_name(object.getString("name"));
                sign.setStart_time(object.getString("fb_time"));
                sign.setEnd_time(object.getString("jz_time"));
                sign.setWay(object.getString("way"));
                sign.setValue(object.getString("value"));
                sign.setSize(object.getString("qd_size"));
                list.add(sign);
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 学生端解析得到的签到列表
    public List<SignIn> stu_get_local_sign_list(String str){
        List<SignIn> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(str);
            for(int i =0;i<array.length();i++){
                SignIn sign = new SignIn();
                JSONObject object = array.getJSONObject(i);
                sign.setSign_name(object.getString("name"));
                sign.setStart_time(object.getString("fb_time"));
                sign.setEnd_time(object.getString("jz_time"));
                sign.setWay(object.getString("way"));
                sign.setSign_style(object.getString("style"));
                sign.setSize("");
                list.add(sign);
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 学生签到
   public void stu_sign_in(int i,String str){
        params = new HashMap<>();
        params.put("student_phone",phone_number);
        params.put("course_id",course_code);
        params.put("fb_time",sign_list.get(i).getStart_time());
        params.put("code",str);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","qiandao/qd");
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("info",res);
                message.setData(bundle);
                message.what = STU_QD;
                handler.sendMessage(message);
            }
        }).start();
   }

   // 老师获取单次签到详情
   public void get_sign_detail(String code,String time){
        params = new HashMap<>();
        params.put("course_id",code);
        params.put("fb_time",time);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params, "utf-8", "qiandao/getValue");
                System.out.println("result :"+res);
                Message m = new Message();
                Bundle b = new Bundle();
                b.putString("info", res);
                m.setData(b);
                m.what = GO_SIGN_DETAIL;
                handler.sendMessage(m);
            }
        }).start();
    }

    // 判断GPS模块是否开启
    public void openGPSSettings() {
        locationManager  = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        String[] PERMISSIONS_STORAGE={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET};
        int permission = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,PERMISSIONS_STORAGE,1);
            //没权限 申请权限
        }else { //判断是否打开GPS
            if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                if(isConnectNet(SignInActivity.this)){ //判断是否打开网络
                   // progressDialog = ProgressDialog.show(SignInActivity.this, "请稍等", "GPS定位中，请稍候...",true);
                    progressDialog = new process_dialog(SignInActivity.this,"GPS定位中，请稍后...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    time = 5;
                    getLocation(SignInActivity.this);
                    get_GPS(); //线程3s

                }else {
                    show_dialog_setGPS("network");
                }
            }else {
                // 请求打开GPS
                show_dialog_setGPS("GPS");
            }
        }
    }

    // 判断网络是否打开
    public static boolean isConnectNet(Context context){
        boolean bisConnFlag=false;
        ConnectivityManager conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if(network!=null){
            bisConnFlag=conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    }

    // 显示请求打开GPS对话框
    public void show_dialog_setGPS(final String str){
        LayoutInflater inflater = LayoutInflater.from(SignInActivity.this);
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_set_gps,null);
        ImageView dialog_close;
        TextView cancel,title;
        Button submit;
        final AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity.this).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        //去掉背景白色实现对话框四个角完全曲化
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);
        dialog_close = dialogView.findViewById(R.id.dialog_close);
        cancel = dialogView.findViewById(R.id.cancel);
        submit = dialogView.findViewById(R.id.submit);
        title = dialogView.findViewById(R.id.title);
        if(str.equals("network")){
            title.setText("请打开网络");
        }
        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(str.equals("network")){
                   Intent intent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                   startActivityForResult(intent, 0);
                   alertDialog.cancel();
               }else {
                   Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                   startActivityForResult(intent, 0); //此为设置完成后返回到获取界面
                   alertDialog.cancel();
               }
            }
        });
    }

    public void get_GPS(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (time>=0) {
                    time--;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(time == 0){
                        Message m = new Message();
                        m.what = GPS_TIME;
                        handler.sendMessage(m);
                    }
                }
            }
        }).start();
    }

    // 进行GPS定位
    @SuppressLint("MissingPermission")
    public void getLocation(Activity activity) {
        // 获取位置管理服务
        locationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
        // 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        String provider;
        List<String> providerList = locationManager.getAllProviders();
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else {
            // 当没有可用的位置提供器时，弹出Toast提示用户
            Toast.makeText(this, "请打开网络", Toast.LENGTH_SHORT).show();
            return;
        }
        System.out.println("pro"+provider);
        Location location = locationManager.getLastKnownLocation(provider);
        if(location==null){
            System.out.println("shishi");
            locationManager.requestLocationUpdates(provider,10, 0, mListener);
        }else {
            //获取纬度
            Double latitude1 = location.getLatitude();
            //获取经度
            Double longitude1 = location.getLongitude();
            Log.e("Latitude", String.valueOf(latitude1));
            Log.e("Longitude", String.valueOf(longitude1));
            locationManager.requestLocationUpdates(provider,10, 0, mListener);
        }
    }

    // 定位监听器
    private LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            // 定位到结果
            Log.d("GPS", "()");
            String tv1;
            if (location != null) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                tv1 = "经度： "+ longitude +" 维度" + latitude;
                Log.d("经度",longitude+"");
                Log.d("维度",latitude+"");
                System.out.println("GPS"+tv1);
                get_address(location);
            } else {
                gps_address = "无法获得地理信息";
                tv1 ="(无法获取地理信息)";
                System.out.println(tv1);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 定位状态改变（可用/临时不可用）
            Log.d("GPS", "onStatusChanged()");

        }

        @Override
        public void onProviderEnabled(String provider) {
            // 定位服务可用（设置-》位置服务-》打开定位）
            Log.d("GPS", "onProviderEnabled()");
            //  String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息
        }

        @Override
        public void onProviderDisabled(String provider) {
            // 定位服务不可用（设置-》位置服务-》关闭定位）
            Log.d("GPS", "onProviderDisabled()");
        }

    };

    //通过location 获得地理位置
    public void get_address(Location location){
        StringBuilder stringBuilder = new StringBuilder();
        Geocoder gc = new Geocoder(SignInActivity.this, Locale.getDefault());
        List<Address> locationList = null;
        try {
            locationList = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address address = locationList.get(0);//得到Address实例
            if (address == null) {
                gps_address = "无法获得地理信息";
                return;
            }

            stringBuilder.append("当前位置：\n");

           /* String countryName = address.getCountryName();//得到国家名称
            if (!TextUtils.isEmpty(countryName)) {
                stringBuilder.append(countryName);
            }

            String adminArea = address.getAdminArea();//省
            if (!TextUtils.isEmpty(adminArea)) {
                stringBuilder.append(adminArea);
            }*/

            String locality = address.getLocality();//得到城市名称
            if (!TextUtils.isEmpty(locality)) {
                stringBuilder.append(locality);
            }
            for (int i = 0; address.getAddressLine(i) != null; i++) {
                String addressLine = address.getAddressLine(i);
                if(!TextUtils.isEmpty(addressLine)) {
                    if(!TextUtils.isEmpty(locality)) {
                        if (addressLine.contains(locality)) {
                            addressLine = addressLine.replace(locality, "");
                        }
                    }
                    if(!TextUtils.isEmpty(addressLine)) {
                        stringBuilder.append(addressLine);
                    }
                }
            }
            /*String featureName = address.getFeatureName();//得到周边信息
            if(!TextUtils.isEmpty(featureName)) {
                stringBuilder.append(featureName).append("\n");
            }*/
            gps_address = stringBuilder.toString();
            System.out.println(stringBuilder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 显示地理位置
    public void show_address(){
        LayoutInflater inflater = LayoutInflater.from(SignInActivity.this);
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_delete_confirm,null);
        ImageView dialog_close;
        TextView cancel,title;
        Button submit;
        final AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity.this).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        //去掉背景白色实现对话框四个角完全曲化
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);
        dialog_close = dialogView.findViewById(R.id.dialog_close);
        cancel = dialogView.findViewById(R.id.cancel);
        submit = (Button) dialogView.findViewById(R.id.confirm);
        title = dialogView.findViewById(R.id.text_title);
        /*if(gps_address == null || gps_address.equals("")){
            title.setText("当前无法获得地理位置...");
        }else{
            title.setText(gps_address);
        }*/
        title.setText(gps_address);
        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 发布GPS签到
                if(identity.equals("T")){
                    dialog = alertDialog;
                    String gps = Double.toString(longitude)+","+Double.toString(latitude);
                    new_gps_sign(course_code,sign_name,style,sign_time,gps);
                }else {
                    dialog = alertDialog;
                    String gps = Double.toString(longitude)+","+Double.toString(latitude);
                   stu_qd_gps(phone_number,course_code,sign_list.get(index).getStart_time(),gps);
                }
            }
        });
    }

    // 老师发布GPS签到 qiandao/newQdGPS
    public void new_gps_sign(String code,String name,String way,String end_time,String gps){
        params = new HashMap<>();
        params.put("course_id",code);
        params.put("name",name);
        params.put("way",way);
        params.put("jz_long",end_time);
        params.put("gps",gps);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","qiandao/newQdGPS");
                Message m = new Message();
                Bundle b = new Bundle();
                b.putString("info",res);
                b.putString("way","GPS");
                m.setData(b);
                m.what = ADD_SIGN;
                handler.sendMessage(m);
            }
        }).start();
    }

    // 学生GPS签到
    public void stu_qd_gps(String phone,String code,String time,String gps){
        params = new HashMap<>();
        params.put("student_phone",phone);
        params.put("course_id",code);
        params.put("fb_time",time);
        params.put("gps",gps);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","qiandao/qdGPS");
                Message m = new Message();
                Bundle b = new Bundle();
                b.putString("info",res);
                m.setData(b);
                m.what = STU_QD;
                handler.sendMessage(m);
            }
        }).start();

    }
}