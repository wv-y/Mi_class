package com.example.mi_class;

import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Login_activity extends AppCompatActivity {

    private TextView login_go_sign_up, login_go_revise_password;
    private ImageView clear_phone_number_login, set_password_show_login;
    private EditText phone_number_login, password_login;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_activity.this,MainActivity.class);
                startActivity(intent);
            }
        });



    }
}