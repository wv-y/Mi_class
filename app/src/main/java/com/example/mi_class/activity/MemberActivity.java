package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mi_class.MainActivity;
import com.example.mi_class.R;
import com.example.mi_class.adapter.MemberAdapter;
import com.example.mi_class.domain.Member;


import java.util.ArrayList;

public class MemberActivity extends AppCompatActivity {

    private ArrayList<Member> member_list;

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
        ListView member_list_view = findViewById(R.id.member_list_view);
        member_list = this.getIntent().getParcelableArrayListExtra("memberList");
        MemberAdapter member_adapter = new MemberAdapter(MemberActivity.this, R.layout.member_list, member_list);     //初始化适配器
        member_list_view.setAdapter(member_adapter);

        // 列表点击事件
        member_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Member member = member_list.get(i);
                System.out.println("成员:"+member.getName()+"手机号："+member.getPhone()+"头像："+member.getPortrait());
                SharedPreferences sp = getSharedPreferences("user_login_info",MODE_PRIVATE);
                String ph = sp.getString("phone","");
                if(!member.getPhone().equals(ph)){
                    Intent intent = new Intent(MemberActivity.this,ChatActivity.class);
                    intent.putExtra("chat_name",member.getPhone());
                    intent.putExtra("nick_name",member.getName());
                    intent.putExtra("pic_id",member.getPortrait());
                    SharedPreferences sp1 = getSharedPreferences(member.getPhone()+"_info",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp1.edit();
                    editor.putInt("pic_id",member.getPortrait());
                    editor.putString("nick_name",member.getName());
                    editor.commit();
                    startActivity(intent);
                }
//                Toast.makeText(MemberActivity.this,"点击了第"+i+"个",Toast.LENGTH_SHORT).show();

            }
        });
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