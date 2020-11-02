package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi_class.R;
import com.example.mi_class.adapter.SignInDetailListAdapter;
import com.example.mi_class.domain.SignIn;
import com.example.mi_class.domain.SignInDetailList;
import com.example.mi_class.tool.HttpUtils;
import com.example.mi_class.tool.QrcodeTool;
import com.example.mi_class.tool.process_dialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SignInDetailActivity extends AppCompatActivity {

    private RecyclerView sign_recyclerview;
    private TextView sign_code_text,sign_code;
    private ImageView sign_two;
    private RelativeLayout sign_text;
    private SignInDetailListAdapter sign_detail_list_adapter;
    private List<SignInDetailList> sign_detail_list;
    private Map<String,String> params;
    private Handler handler;
    private final static int NEW_SIGN_DETAIL = 390;
    private final static int SET_SIGN_DETAIL = 391;
    private String detail_list;
    private String way;
    private String sign_value, course_id, start_time,phone;
    private AlertDialog dialog;
    private process_dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_sign_in_detail);
        setTitle("签到列表");

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        sign_recyclerview = findViewById(R.id.sign_list_recycler_view);
        sign_text = findViewById(R.id.sign_text);
        sign_code_text = findViewById(R.id.sign_code_text);
        sign_code = findViewById(R.id.sign_code);
        sign_two = findViewById(R.id.two_code);

        initInfo();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        sign_recyclerview.setLayoutManager(layoutManager);

        sign_detail_list_adapter = new SignInDetailListAdapter(SignInDetailActivity.this,sign_detail_list);
        sign_recyclerview.setAdapter(sign_detail_list_adapter);

        sign_detail_list_adapter.setOnItemClickListener(onItemClickListener);

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String info;
                switch (msg.what){
                    case SET_SIGN_DETAIL:
                        progressDialog.dismiss();
                        info = msg.getData().getString("info");
                        if(info.equals("-999")){
                            Toast.makeText(SignInDetailActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.cancel();
                            Toast.makeText(SignInDetailActivity.this,info,Toast.LENGTH_SHORT).show();
                            get_sign_detail(course_id,start_time);
                        }
                        break;
                    case NEW_SIGN_DETAIL:
                        info = msg.getData().getString("info");
                        if(info.equals("-999")){
                            Toast.makeText(SignInDetailActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                        } else if(info.equals("[]") || info == null){
                            Toast.makeText(SignInDetailActivity.this,"尚未有学生加入",Toast.LENGTH_SHORT).show();
                        } else {
                            detail_list = info;
                            sign_detail_list = get_detail_list(detail_list);
                            sign_detail_list_adapter = new SignInDetailListAdapter(SignInDetailActivity.this,sign_detail_list);
                            sign_recyclerview.setAdapter(sign_detail_list_adapter);
                            sign_detail_list_adapter.setOnItemClickListener(onItemClickListener);
                           // sign_detail_list_adapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        };
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


    private SignInDetailListAdapter.OnItemClickListener onItemClickListener = new SignInDetailListAdapter.OnItemClickListener(){

        @Override
        public void onItemClick(View v,int position) {
            if(v.getId() == R.id.set_sign) {
                show_dialog(sign_detail_list.get(position).getName(),sign_detail_list.get(position).getStyle());
                phone = sign_detail_list.get(position).getPhone();
                System.out.println("phone_result"+phone);
                System.out.println("position"+phone);
            }
        }
    };

    public void initInfo(){
        Intent intent = getIntent();
        detail_list = intent.getStringExtra("sign_list");
        way = intent.getStringExtra("way_tea");
        sign_value = intent.getStringExtra("sign_code");
        course_id = intent.getStringExtra("course_id");
        start_time = intent.getStringExtra("start_time");
        sign_detail_list = get_detail_list(detail_list);
        //System.out.println("way"+way);
        //System.out.println("code"+sign_value);
        switch (way){
            case "GPS":
                sign_text.setVisibility(View.GONE);
                break;
            case "num":
                sign_code.setText(sign_value);
                break;
            case "two":
                sign_code_text.setText("二维码：");
                sign_two.setImageBitmap(QrcodeTool.getQRCode(sign_value,400,400));
                break;
        }
    }

    // 解析传递过来的签到列表
    private List<SignInDetailList> get_detail_list(String str){
        try {
            List<SignInDetailList> list = new ArrayList<>();
            JSONArray array = new JSONArray(str);
            for(int i=0;i<array.length();i++){
                SignInDetailList detailList = new SignInDetailList();
                JSONObject object = array.getJSONObject(i);
                detailList.setImage(object.getInt("pic_id"));
                detailList.setName(object.getString("stu_name"));
                detailList.setId(object.getString("stu_id"));
                detailList.setStyle(object.getString("value"));
                detailList.setPhone(object.getString("stu_phone"));
                list.add(detailList);
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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
                Message m = new Message();
                Bundle b = new Bundle();
                b.putString("info", res);
                m.setData(b);
                m.what = NEW_SIGN_DETAIL;
                handler.sendMessage(m);
            }
        }).start();
    }

   /* 功能：老师修改学生签到状态
    接口：/qiandao/change
    参数：包装类{course_id(String) 课程id，fb_time(String) 发布时间，student_phone(String) 学生手机号，value(String) 签到状态}
    返回值：String "签到不存在" 或 "修改成功"*/
    // 修改签到状态
    public void set_sign(String code, String start_time, String phone, String value){
        params = new HashMap<>();
        params.put("course_id",code);
        params.put("fb_time",start_time);
        params.put("student_phone",phone);
        params.put("value",value);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = HttpUtils.sendPostMessage(params,"utf-8","qiandao/change");
                Message m = new Message();
                Bundle b = new Bundle();
                b.putString("info",res);
                m.setData(b);
                m.what = SET_SIGN_DETAIL;
                handler.sendMessage(m);
            }
        }).start();
    }

    // 修改签到状态对话框
    public void show_dialog(String name, final String style){
        LayoutInflater inflater = LayoutInflater.from(SignInDetailActivity.this);
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_set_sign,null);
        final ImageView dialog_close;
        Button submit;
        final TextView cancel, name_view;
        final Button style_edit;
        final AlertDialog alertDialog = new AlertDialog.Builder(SignInDetailActivity.this).create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        //去掉背景白色实现对话框四个角完全曲化
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.setContentView(dialogView);
        dialog_close = dialogView.findViewById(R.id.dialog_close);
        name_view = dialogView.findViewById(R.id.stu_name);
        style_edit = dialogView.findViewById(R.id.sign_view);
        cancel = dialogView.findViewById(R.id.cancel);
        submit = dialogView.findViewById(R.id.submit);
        name_view.setText(name);
        style_edit.setText(style);
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
        style_edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getX() >= (style_edit.getWidth() - style_edit
                            .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        style_edit.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_down), null);

                        final String[] list = {"已签到", "未签到", "迟到", "请假"};//要填充的数据
                        final ListPopupWindow listPopupWindow;
                        listPopupWindow = new ListPopupWindow(SignInDetailActivity.this);
                        listPopupWindow.setAdapter(new ArrayAdapter<String>(SignInDetailActivity.this,android.R.layout.simple_list_item_1, list));//用android内置布局，或设计自己的样式
                        listPopupWindow.setAnchorView(style_edit);//以哪个控件为基准，在该处以logId为基准
                        listPopupWindow.setModal(true);

                        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置项点击监听
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                style_edit.setText(list[i]);//把选择的选项内容展示在EditText上
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
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = alertDialog;
                alertDialog.cancel();
                set_sign(course_id,start_time,phone,style_edit.getText().toString());
                progressDialog = new process_dialog(SignInDetailActivity.this,"正在修改...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        });
    }
}