package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.mi_class.R;
import com.example.mi_class.adapter.HomeworkAdapter;
import com.example.mi_class.domain.Homework;
import java.util.ArrayList;
import java.util.List;

public class HomeworkActivity extends AppCompatActivity {

    private List<Homework> homework_list;
    private HomeworkAdapter homework_adapter;
    private ListView homework_list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_homework);

        // 设置标题
        setTitle("所有作业");
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        homework_list_view = (ListView) findViewById(R.id.homework_list_view);

        // 默认数据显示用
        Homework homework = new Homework("第一次作业","2020.10.7 12:30-2020.10.10 00:00","已提交");
        Homework homework1 = new Homework("第二次作业","2020.10.8 12:30-2020.10.10 00:00","未提交");
        homework_list  = new ArrayList<Homework>();
        homework_list.add(homework);
        homework_list.add(homework1);

        homework_adapter = new HomeworkAdapter(HomeworkActivity.this, R.layout.homework_list, homework_list);     //初始化适配器
        homework_list_view.setAdapter(homework_adapter);
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