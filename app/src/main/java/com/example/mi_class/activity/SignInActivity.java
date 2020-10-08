package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.mi_class.R;
import com.example.mi_class.adapter.SignInAdapter;
import com.example.mi_class.domain.SignIn;

import java.util.ArrayList;
import java.util.List;

public class SignInActivity extends AppCompatActivity {

    private List<SignIn> sign_list;
    private SignInAdapter sign_adapter;
    private ListView sign_list_view;

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

        // 默认数据显示用
        SignIn signIn = new SignIn("实验签到","2020.10.7 18:23","已签到");
        SignIn signIn1 = new SignIn("上课签到","2020.10.7 13:30","迟到");
        sign_list  = new ArrayList<SignIn>();
        sign_list.add(signIn);
        sign_list.add(signIn1);

        sign_adapter = new SignInAdapter(SignInActivity.this, R.layout.sign_in_list,sign_list);     //初始化适配器
        sign_list_view.setAdapter(sign_adapter); //设置适配器，显示查询结果

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
}