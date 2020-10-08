package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.mi_class.R;
import com.example.mi_class.adapter.MemberAdapter;
import com.example.mi_class.domain.Member;


import java.util.ArrayList;
import java.util.List;

public class MemberActivity extends AppCompatActivity {

    private List<Member> member_list;
    private MemberAdapter member_adapter;
    private ListView member_list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_member);

        // 设置标题
        setTitle("成员列表");
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        member_list_view = (ListView) findViewById(R.id.member_list_view);

        // 默认数据显示用
        Member member = new Member("张三","T","2010231010");
        Member member1 = new Member("王玉华","S","2019011340");

        member_list = new ArrayList<Member>();
        member_list.add(member);
        member_list.add(member1);

        member_adapter = new MemberAdapter(MemberActivity.this, R.layout.member_list, member_list);     //初始化适配器
        member_list_view.setAdapter(member_adapter);

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