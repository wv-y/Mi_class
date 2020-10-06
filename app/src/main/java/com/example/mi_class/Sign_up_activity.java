package com.example.mi_class;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mi_class.tool.AES;
import com.example.mi_class.tool.HttpUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.example.mi_class.tool.MD5.md5;
import static com.example.mi_class.tool.Match.match_mobile;

public class Sign_up_activity extends AppCompatActivity {

    private EditText phone_number_sign_up, password_sign_up, password_again_sign_up, sign_up_code;
    private TextView sign_go_login;
    private ImageView  teacher, student;
    private ImageView set_password_again_show_sign,set_password_show_sign,
            clear_phone_number_sign_up;

    private Button login_get_code;
    private EventHandler eh;
    private HashMap<String, String> params;
    private String flag = "";
    private Activity a;
    private Button sign_up;

    private String phone, paw, paw_again; //用户输入
    private boolean is_show_password = true; //是否隐藏密码
    private boolean is_show_password_again =true;

    private int time = 60;  // 60s后重新获取验证码
    private Handler handler;
    public static final int BUTTON_TRUE = 1;
    public  static final int BUTTON_FALSE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //去掉标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_up);
        initView();
//        try {   //BuildConfig.APPLICATION_ID   当前应用包名
//            PackageInfo packageInfo = getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID,
//                    PackageManager.GET_SIGNATURES);
//            String signValidString = getSignValidString(packageInfo.signatures[0].toByteArray());
//            Log.e("获取应用签名", BuildConfig.APPLICATION_ID + ":" + signValidString);
//        } catch (Exception e) {
//            Log.e("获取应用签名", "异常:" + e);
//        }

    }
    private String getSignValidString(byte[] paramArrayOfByte) throws NoSuchAlgorithmException, NoSuchAlgorithmException {
        MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
        localMessageDigest.update(paramArrayOfByte);
        return toHexString(localMessageDigest.digest());
    }

    public String toHexString(byte[] paramArrayOfByte) {
        if (paramArrayOfByte == null) {
            return null;
        }
        StringBuilder localStringBuilder = new StringBuilder(2 * paramArrayOfByte.length);
        for (int i = 0; ; i++) {
            if (i >= paramArrayOfByte.length) {
                return localStringBuilder.toString();
            }
            String str = Integer.toString(0xFF & paramArrayOfByte[i], 16);
            if (str.length() == 1) {
                str = "0" + str;
            }
            localStringBuilder.append(str);
        }
    }

    public void initView()
    {
        a = this;
        sign_up = findViewById(R.id.sign_up);
        phone_number_sign_up = findViewById(R.id.phone_number_sign_up);
        password_sign_up = findViewById(R.id.password_sign_up);
        password_again_sign_up = findViewById(R.id.password_again_sign_up);
        sign_up_code = findViewById(R.id.sign_up_code);
        sign_go_login = (TextView) findViewById(R.id.sign_go_login);
        student = findViewById(R.id.student);
        teacher = findViewById(R.id.teacher);
        login_get_code = findViewById(R.id.login_get_code);

        clear_phone_number_sign_up = findViewById(R.id.clear_phone_number_sign_up);
        set_password_show_sign = findViewById(R.id.set_password_show_sign);
        set_password_again_show_sign = findViewById(R.id.set_password_again_show_sign);

        //手机号清除按钮显示与隐藏
        phone_number_sign_up.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                phone = phone_number_sign_up.getText().toString();
                if(b){
                    phone_number_sign_up.setBackgroundResource(R.drawable.edit_back_onfocus);
                    if(phone.length()!=0){
                        clear_phone_number_sign_up.setVisibility(View.VISIBLE); //可见
                    }
                    phone_number_sign_up.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }
                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (editable.length()!=0){
                                clear_phone_number_sign_up.setVisibility(View.VISIBLE); //可见
                            }
                            else
                                clear_phone_number_sign_up.setVisibility(View.GONE);
                        }
                    });
                }
                else{
                    phone_number_sign_up.setBackgroundResource(R.drawable.edit_back);
                    clear_phone_number_sign_up.setVisibility(View.GONE);  //隐藏并且不占用空间
                }
            }
        });

        //点击清除输入的手机号
        clear_phone_number_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone_number_sign_up.setText("");
            }
        });

        //密码框获取焦点变色
        password_sign_up.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){ //获取焦点
                    password_sign_up.setBackgroundResource(R.drawable.edit_back_onfocus);
                }
                else{
                    password_sign_up.setBackgroundResource(R.drawable.edit_back);
                }
            }
        });
        password_again_sign_up.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){ //获取焦点
                    password_again_sign_up.setBackgroundResource(R.drawable.edit_back_onfocus);
                }
                else{
                    password_again_sign_up.setBackgroundResource(R.drawable.edit_back);
                }
            }
        });

        // 输入验证码文本框变色
        sign_up_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    sign_up_code.setBackgroundResource(R.drawable.edit_back_onfocus);
                }
                else
                    sign_up_code.setBackgroundResource(R.drawable.edit_back);
            }
        });

        // 显示或隐藏密码
        set_password_show_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_show_password){//显示
                    set_password_show_sign.setImageResource(R.drawable.eyes_open);
                    password_sign_up.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    is_show_password = false;
                }
                else{   //隐藏
                    set_password_show_sign.setImageResource(R.drawable.eyes_close);
                    password_sign_up.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    is_show_password = true;
                }
                //定位光标到最后
                int index = password_sign_up.getText().toString().length();
                password_sign_up.setSelection(index);
            }
        });
        // 显示或隐藏密码
        set_password_again_show_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(is_show_password_again){//显示
                    set_password_again_show_sign.setImageResource(R.drawable.eyes_open);
                    password_again_sign_up.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    is_show_password_again = false;
                }
                else{   //隐藏
                    set_password_again_show_sign.setImageResource(R.drawable.eyes_close);
                    password_again_sign_up.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    is_show_password_again = true;
                }
                //定位光标到最后
                int index = password_again_sign_up.getText().toString().length();
                password_again_sign_up.setSelection(index);
            }
        });




//        RegisterPage page = new RegisterPage();
//        //如果使用我们的ui，没有申请模板编号的情况下需传null
//        page.setTempCode(null);
//        page.setRegisterCallback(new EventHandler() {
//            public void afterEvent(int event, int result, Object data) {
//                if (result == SMSSDK.RESULT_COMPLETE) {
//                    // 处理成功的结果
//                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
//                    // 国家代码，如“86”
//                    String country = (String) phoneMap.get("country");
//                    // 手机号码，如“13800138000”
//                    String phone = (String) phoneMap.get("phone");
//                    // TODO 利用国家代码和手机号码进行后续的操作
//                } else{
//                    // TODO 处理错误的结果
//                }
//            }
//        });
//        page.show(a);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("验证");
                String code = sign_up_code.getText().toString();
                String phone = phone_number_sign_up.getText().toString();
                SMSSDK.submitVerificationCode("86", phone, code);
            }
        });
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = "S";
                student.setImageResource(R.drawable.selected);
                teacher.setImageResource(R.drawable.select_student_or_teacher);
            }
        });
        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = "T";
                teacher.setImageResource(R.drawable.selected);
                student.setImageResource(R.drawable.select_student_or_teacher);
            }
        });
        sign_go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Sign_up_activity.this, Login_activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        login_get_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 验证两次输入的密码是否相同
                String paw = password_sign_up.getText().toString();
                String paw_again = password_again_sign_up.getText().toString();
                if(paw.equals(paw_again)){
                    if(paw_again.length()==0){
                        password_sign_up.setBackgroundResource(R.drawable.edit_back_error);
                        password_again_sign_up.setBackgroundResource(R.drawable.edit_back_error);
                        Toast.makeText(a,"密码不能为空",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        password_again_sign_up.setBackgroundResource(R.drawable.edit_back);
                        password_sign_up.setBackgroundResource(R.drawable.edit_back);
                        String phone = phone_number_sign_up.getText().toString();
                        if(match_mobile(phone))
                        {
                            System.out.println("发送");
                            SMSSDK.getVerificationCode("86", phone);
                            Toast.makeText(a,"已发送验证码",Toast.LENGTH_SHORT).show();

                            // 将按钮置为不可用同时倒计时
                            time = 60;
                            new Thread(new button_get_code()).start();

                        }else{
                            Toast.makeText(a,"请输入正确手机号码",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    password_sign_up.setBackgroundResource(R.drawable.edit_back_error);
                    password_again_sign_up.setBackgroundResource(R.drawable.edit_back_error);
                    Toast.makeText(a,"两次密码输入不一致",Toast.LENGTH_SHORT).show();
                }
            }
        });

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case BUTTON_TRUE:
                        login_get_code.setClickable(true);
                        login_get_code.setBackgroundResource(R.drawable.but_bg);
                        login_get_code.setText("获取验证码");
                        break;
                    case BUTTON_FALSE:
                        login_get_code.setClickable(false);
                        login_get_code.setBackgroundResource(R.drawable.button_back_not_click);
                        login_get_code.setText("重新获取("+time+"s)");
                        break;
                    default:
                        break;
                }
            }
        };

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
                    System.out.println("验证成功");
                    // 处理成功的结果
                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                    // 国家代码，如“86”
                    String country = (String) phoneMap.get("country");
                    // 手机号码，如“13800138000”
                    String phone = (String) phoneMap.get("phone");
                    System.out.println(phone);

                    String s = password_sign_up.getText().toString();
                    params = new HashMap<String, String>();
                    params.put("name","");
                    params.put("password",md5(md5(s)));
                    params.put("identity",flag);
                    HashMap t = AES.encode(phone);
                    params.put("key",(String)t.get("key"));
                    params.put("phone",(String)t.get("value"));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String res = HttpUtils.sendPostMessage(params,"utf-8","login");
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

    // 倒计时60s线程
    class button_get_code implements Runnable{
        public void run(){
            time --;
            if(time<=0){
                Message message = new Message();
                message.what = BUTTON_TRUE;
                handler.sendMessage(message);
            } else{
                Message message = new Message();
                message.what = BUTTON_FALSE;
                handler.sendMessage(message);
                handler.postDelayed(this,1000);
                /*try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
        }
    }

}