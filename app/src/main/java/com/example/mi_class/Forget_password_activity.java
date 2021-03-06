package com.example.mi_class;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private EditText password_forget,password_again_forget;
    private Button forget_get_code;
    private Button forget_confirm_code;
    private EditText forget_verify_code;

    private ImageView paw_show_forget, paw_again_show_forget, clear_phone_number_forget;
    Activity a;
    private boolean is_show_password_again = true,is_show_password =true;
    private Handler handler;
    private int time = 60;

    private static final int BUTTON_TRUE = 1;
    private static final int BUTTON_FALSE = 2;
    private String phone, paw, paw_again, code; //用户输入
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
        password_again_forget = findViewById(R.id.password_again_forget);
        forget_get_code = findViewById(R.id.forget_get_code);
        forget_verify_code = findViewById(R.id.forget_verify_code);
        forget_go_login = (TextView) findViewById(R.id.forget_go_login);

        paw_show_forget = findViewById(R.id.paw_show_forget);
        paw_again_show_forget = findViewById(R.id.paw_again_show_forget);
        clear_phone_number_forget = findViewById(R.id.clear_phone_number_forget);

        forget_go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Forget_password_activity.this, Login_activity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        forget_confirm_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close_keyboard(view);
                if(code_true()) {
                    System.out.println("验证");
                    String code = forget_verify_code.getText().toString();
                    String phone = edit_phone_number.getText().toString();
                    SMSSDK.submitVerificationCode("86", phone, code);
                }
            }
        });
        forget_get_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close_keyboard(view);
                if(phone_paw_true()){
                    System.out.println("发送");
                    SMSSDK.getVerificationCode("86", phone);
                    Toast.makeText(a,"已发送验证码",Toast.LENGTH_LONG).show();
                    // 倒计时60s
                    time = 60;
                    new Thread(new button_get_code()).start();
                }
            }
        });
        // 倒计时
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.d("handler",handler.toString());
                switch (msg.what){
                    case BUTTON_TRUE:
                        forget_get_code.setClickable(true);
                        forget_get_code.setBackgroundResource(R.drawable.but_bg);
                        forget_get_code.setText("获取验证码");
                        break;
                    case BUTTON_FALSE:
                        forget_get_code.setClickable(false);
                        forget_get_code.setBackgroundResource(R.drawable.button_back_not_click);
                        forget_get_code.setText("重新获取("+time+"s)");
                        break;
                    case 3:
                        if(msg.getData().getString("info").equals("201")){
                            //修改成功
                            Toast.makeText(Forget_password_activity.this,"修改成功",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Forget_password_activity.this,Login_activity.class);
                            startActivity(intent);
                            SMSSDK.unregisterEventHandler(eh);
                            //Forget_password_activity.this.finish();
                        }else {
                            //错误
                            Toast.makeText(Forget_password_activity.this, "修改失败 请重试", Toast.LENGTH_LONG).show();
                        }
                        break;
                    default:
                        break;
                }
            }
        };

        //验证码 handler
        eh=new EventHandler(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void afterEvent(int event, int result, Object data) {
                // TODO 此处不可直接处理UI线程，处理后续操作需传到主线程中操作
//                Message msg = new Message();
//                msg.arg1 = event;
//                msg.arg2 = result;
//                msg.obj = data;
                System.out.println("我是Mob的handler\n当前事件："+event);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    System.out.println("Mob验证成功");
                    if(event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE)
                    {
                        System.out.println("Mob验证提交后");
                        // 处理成功的结果
                        HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                        // 国家代码，如“86”
                        String country = (String) phoneMap.get("country");
                        // 手机号码，如“13800138000”
                        String phone = (String) phoneMap.get("phone");
                        System.out.println(phone);

                        String s = password_forget.getText().toString();
                        params = new HashMap<String, String>();
                        params.put("user_pwd",md5(md5(s)));
                        HashMap t = AES.encode(phone);
                        params.put("key",(String)t.get("key"));
                        params.put("user_phone",(String)t.get("value"));
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String res = HttpUtils.sendPostMessage(params,"utf-8","forget_pwd");
                                Message m = new Message();
                                Bundle b = new Bundle();
                                b.putString("info",res);
                                System.err.println(res);
                                m.setData(b);
                                m.what = 3;
                                handler.sendMessage(m);
                            }
                        }).start();
                    }


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

        // 获取焦点
        edit_phone_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String phone = edit_phone_number.getText().toString();
                if(b){
                    edit_phone_number.setBackgroundResource(R.drawable.edit_back_onfocus);
                    if(phone.length()!=0){
                        clear_phone_number_forget.setVisibility(View.VISIBLE); //可见
                    }
                    edit_phone_number.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (editable.length()!=0){
                                clear_phone_number_forget.setVisibility(View.VISIBLE); //可见
                            }
                            else
                                clear_phone_number_forget.setVisibility(View.GONE);
                        }
                    });
                }
                else{
                    edit_phone_number.setBackgroundResource(R.drawable.edit_back);
                    clear_phone_number_forget.setVisibility(View.GONE);  //隐藏并且不占用空间
                }
            }
        });
        clear_phone_number_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_phone_number.setText("");
            }
        });
        //密码框获取焦点变色
        password_forget.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){ //获取焦点
                    password_forget.setBackgroundResource(R.drawable.edit_back_onfocus);
                }
                else{
                    password_forget.setBackgroundResource(R.drawable.edit_back);
                }
            }
        });
        password_again_forget.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) //获取焦点
                    password_again_forget.setBackgroundResource(R.drawable.edit_back_onfocus);
                else
                    password_again_forget.setBackgroundResource(R.drawable.edit_back);
            }
        });
        forget_verify_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b)
                    forget_verify_code.setBackgroundResource(R.drawable.edit_back_onfocus);
                else
                    forget_verify_code.setBackgroundResource(R.drawable.edit_back);
            }
        });

        // 显示或隐藏密码
        paw_show_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_show_password){//显示
                    paw_show_forget.setImageResource(R.drawable.eyes_open);
                    password_forget.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    is_show_password = false;
                }
                else{   //隐藏
                    paw_show_forget.setImageResource(R.drawable.eyes_close);
                    password_forget.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    is_show_password = true;
                }
                //定位光标到最后
                int index = password_forget.getText().toString().length();
                password_forget.setSelection(index);
            }
        });
        paw_again_show_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_show_password_again){//显示
                    paw_again_show_forget.setImageResource(R.drawable.eyes_open);
                    password_again_forget.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    is_show_password_again = false;
                }
                else{   //隐藏
                    paw_again_show_forget.setImageResource(R.drawable.eyes_close);
                    password_again_forget.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    is_show_password_again = true;
                }
                //定位光标到最后
                int index = password_again_forget.getText().toString().length();
                password_again_forget.setSelection(index);
            }
        });

    }

    class button_get_code implements Runnable{
        @Override
        public void run() {
            time --;
            if(time<=0){
                Message message = new Message();
                message.what = BUTTON_TRUE;
                handler.sendMessage(message);
            }else{
                Message message = new Message();
                message.what = BUTTON_FALSE;
                handler.sendMessage(message);
                handler.postDelayed(this,1000);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        SMSSDK.unregisterEventHandler(eh);
    }

    public boolean phone_paw_true(){
        phone = edit_phone_number.getText().toString();
        paw = password_forget.getText().toString();
        paw_again = password_again_forget.getText().toString();
        if(!match_mobile(phone)){
            edit_set_error(edit_phone_number);
            edit_set_init_back(password_forget);
            edit_set_init_back(password_again_forget);
            edit_set_init_back(forget_verify_code);
            Toast.makeText(this, "请正确填写手机号", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!paw.equals(paw_again)){
            edit_set_error(password_forget);
            edit_set_error(password_again_forget);
            edit_set_init_back(edit_phone_number);
            edit_set_init_back(forget_verify_code);
            Toast.makeText(this,"两次输入密码不一致",Toast.LENGTH_SHORT).show();
            return false;
        }else if(paw_again.equals("")){
            edit_set_error(password_forget);
            edit_set_error(password_again_forget);
            edit_set_init_back(edit_phone_number);
            edit_set_init_back(forget_verify_code);
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }else if((paw_again.length() < 6) || (paw_again.length() > 20)){
            edit_set_error(password_forget);
            edit_set_error(password_again_forget);
            edit_set_init_back(edit_phone_number);
            edit_set_init_back(forget_verify_code);
            Toast.makeText(this,"密码长度应在6~20位",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            edit_set_init_back(edit_phone_number);
            edit_set_init_back(password_forget);
            edit_set_init_back(password_again_forget);
            edit_set_init_back(forget_verify_code);
            return true;
        }
    }

    public boolean code_true(){
        code = forget_verify_code.getText().toString();
        if(!phone_paw_true()){
            return false;
        }else if(code.equals("")){
            edit_set_error(forget_verify_code);
            edit_set_init_back(edit_phone_number);
            edit_set_init_back(password_forget);
            edit_set_init_back(password_again_forget);
            Toast.makeText(this,"验证码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }else
            edit_set_init_back(edit_phone_number);
        edit_set_init_back(password_forget);
        edit_set_init_back(password_again_forget);
        edit_set_init_back(forget_verify_code);
        return true;
    }


    public void edit_set_error(EditText editText){
        editText.clearFocus();
        editText.setBackgroundResource(R.drawable.edit_back_error);
    }

    public void edit_set_init_back(EditText editText){
        editText.clearFocus();
        editText.setBackgroundResource(R.drawable.edit_back);
    }

    // 关闭软键盘
    public void close_keyboard(View view){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
}