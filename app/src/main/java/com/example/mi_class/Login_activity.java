package com.example.mi_class;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi_class.activity.UserInfoActivity;
import com.example.mi_class.tool.AES;
import com.example.mi_class.tool.HttpUtils;
import com.example.mi_class.tool.Match;

import java.util.HashMap;

import static com.example.mi_class.tool.MD5.md5;

public class Login_activity extends AppCompatActivity {

    private TextView login_go_sign_up, login_go_revise_password;
    private ImageView clear_phone_number_login, set_password_show_login;
    private EditText phone_number_login, password_login;
    private Button back;
    private HashMap<String, String> params;
    private Handler handler;

    private boolean is_show_password = true;// 输入框密码是否是隐藏的，默认为true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //去掉标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        login_go_sign_up = (TextView)findViewById(R.id.login_go_sign_up);
        login_go_revise_password = (TextView)findViewById(R.id.login_go_revise_password);

        clear_phone_number_login = (ImageView) findViewById(R.id.clear_phone_number_login);
        set_password_show_login = (ImageView) findViewById(R.id.set_password_show_login);

        phone_number_login = (EditText) findViewById(R.id.phone_number_login);
        password_login = (EditText) findViewById(R.id.password_login);

        back = findViewById(R.id.back);
        //点击没有账户，跳转到注册界面
        login_go_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_activity.this,Sign_up_activity.class);
                //关掉返回栈中目标活动和当前活动之间的activity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        //点击忘记密码，跳转到修改密码界面
        login_go_revise_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login_activity.this,Forget_password_activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case 200:
                        String info = msg.getData().getString("info");
                        if(info.equals("400")){
                            //学生
                            SharedPreferences pf = getSharedPreferences("user_login_info", Context.MODE_PRIVATE);
                            SharedPreferences.Editor ed = pf.edit();
                            ed.putString("phone",phone_number_login.getText().toString());
                            ed.putString("identity","S");
                            ed.commit();
                            System.out.println("S");
                            Intent intent = new Intent(Login_activity.this,MainActivity.class);
                            startActivity(intent);
                            Login_activity.this.finish();
                        }else if(info.equals("500")){
                            //教师
                            SharedPreferences pf = getSharedPreferences("user_login_info", Context.MODE_PRIVATE);
                            SharedPreferences.Editor ed = pf.edit();
                            ed.putString("phone",phone_number_login.getText().toString());
                            ed.putString("identity","T");
                            ed.commit();
                            System.out.println("T");
                            Intent intent = new Intent(Login_activity.this,MainActivity.class);
                            startActivity(intent);
                            Login_activity.this.finish();
                        }
                        else if(info.equals("401")){
                            Toast.makeText(Login_activity.this,"账号或密码错误",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(Login_activity.this,"异常错误",Toast.LENGTH_LONG).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        //点击登录跳转到mainActivity
        back.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_activity.this, MainActivity.class));

                String phone = phone_number_login.getText().toString();
                String pwd = password_login.getText().toString();
//                System.err.println(phone);
//                System.err.println(pwd);
                if(phone.equals(""))
                {
                    Toast.makeText(Login_activity.this,"请输入手机号",Toast.LENGTH_LONG).show();
                }
                else if(!phone.equals("") && !Match.match_mobile(phone)){
                    Toast.makeText(Login_activity.this,"请输入正确的手机号",Toast.LENGTH_LONG).show();
                }
                else if(pwd.equals(""))
                {
                    Toast.makeText(Login_activity.this,"请输入密码",Toast.LENGTH_LONG).show();
                }else{
                    params = new HashMap<String, String>();
                    params.put("user_pwd",md5(md5(pwd)));
                    HashMap t = AES.encode(phone);
                    params.put("key",(String)t.get("key"));
                    params.put("user_phone",(String)t.get("value"));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String res = HttpUtils.sendPostMessage(params,"utf-8","login");
                            Message m = new Message();
                            Bundle b = new Bundle();
                            System.out.println(res);
                            b.putString("info",res);
                            m.setData(b);
                            m.what = 200;
                            handler.sendMessage(m);
                        }
                    }).start();
                }
            }
        });

        //手机号输入框获取焦点时边框变色，判断是否输入内容，显示清除按钮
        phone_number_login.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String phone_number = phone_number_login.getText().toString();
                if(hasFocus){ //获取焦点
                    phone_number_login.setBackgroundResource(R.drawable.edit_back_onfocus);
                    if(phone_number.length()!=0) {
                        clear_phone_number_login.setVisibility(View.VISIBLE); //可见
                    }
                    phone_number_login.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (editable.length()!=0){
                                clear_phone_number_login.setVisibility(View.VISIBLE); //可见
                            }
                            else
                                clear_phone_number_login.setVisibility(View.GONE);
                        }
                    });
                }
                else{
                    phone_number_login.setBackgroundResource(R.drawable.edit_back);
                    clear_phone_number_login.setVisibility(View.GONE);  //隐藏并且不占用空间
                }
            }
        });

        //点击清除输入的手机号
        clear_phone_number_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone_number_login.setText("");
            }
        });


        //密码框获取焦点变色
        password_login.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){ //获取焦点
                    password_login.setBackgroundResource(R.drawable.edit_back_onfocus);
                }
                else{
                    password_login.setBackgroundResource(R.drawable.edit_back);
                }
            }
        });

        //点击切换密码显示、隐藏
        set_password_show_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_show_password){//显示
                    set_password_show_login.setImageResource(R.drawable.eyes_open);
                    password_login.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    is_show_password = false;
                }
                else{   //隐藏
                    set_password_show_login.setImageResource(R.drawable.eyes_close);
                    password_login.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    is_show_password = true;
                }
                //定位光标到最后
                int index = password_login.getText().toString().length();
                password_login.setSelection(index);
            }
        });
    }
}