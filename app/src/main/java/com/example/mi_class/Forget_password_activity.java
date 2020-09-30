package com.example.mi_class;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi_class.tool.AES;
import com.example.mi_class.tool.HttpUtils;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.example.mi_class.tool.MD5.md5;
import static com.example.mi_class.tool.Match.match_mobile;

public class Forget_password_activity extends AppCompatActivity {

    private TextView forget_go_login;
    private EventHandler eh;
    private HashMap<String,String> params;
    private EditText edit_phone_number;
    private EditText password_forget;
    private Button forget_get_code;
    private Button forget_confirm_code;
    private EditText forget_verify_code;
    Activity a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //去掉标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forget_password);
        //视图初始化
        initView();
    }
    public void initView()
    {
        a = this;
        forget_confirm_code = findViewById(R.id.forget_confirm_code);
        edit_phone_number = findViewById(R.id.edit_phone_number);
        password_forget = findViewById(R.id.password_forget);
        forget_get_code = findViewById(R.id.forget_get_code);
        forget_verify_code = findViewById(R.id.forget_verify_code);
        forget_go_login = (TextView) findViewById(R.id.forget_go_login);
        forget_go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Forget_password_activity.this, Login_activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        forget_confirm_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("验证");
                String code = forget_verify_code.getText().toString();
                String phone = edit_phone_number.getText().toString();
                SMSSDK.submitVerificationCode("86", phone, code);
            }
        });
        forget_get_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = edit_phone_number.getText().toString();
                if(match_mobile(phone))
                {
                    System.out.println("发送");
                    SMSSDK.getVerificationCode("86", phone);
                }else{
                    Toast.makeText(a,"请输入正确手机号码",Toast.LENGTH_LONG).show();
                }

            }
        });
        //验证码 handler
        eh=new EventHandler(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void afterEvent(int event, int result, Object data) {
                // TODO 此处不可直接处理UI线程，处理后续操作需传到主线程中操作
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 处理成功的结果
                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                    // 国家代码，如“86”
                    String country = (String) phoneMap.get("country");
                    // 手机号码，如“13800138000”
                    String phone = (String) phoneMap.get("phone");
                    System.out.println(phone);

                    String s = password_forget.getText().toString();
                    params = new HashMap<String, String>();
                    params.put("name","");
                    params.put("password",md5(md5(s)));
                    HashMap t = AES.encode(phone);
                    params.put("key",(String)t.get("key"));
                    params.put("phone",(String)t.get("value"));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String res = HttpUtils.sendPostMessage(params,"utf-8");
                            Message m = new Message();
                            Bundle b = new Bundle();
                            b.putString("info",res);
                            m.setData(b);
                            m.what = 2;
                            //handler.sendMessage(m);
                        }
                    }).start();

                    // TODO 利用国家代码和手机号码进行后续的操作
                } else{
                    Toast.makeText(a,"验证码错误",Toast.LENGTH_LONG).show();
                    System.out.println("验证失败");
                }
                //mHandler.sendMessage(msg);

            }
        };

//注册一个事件回调监听，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eh);

    }

}