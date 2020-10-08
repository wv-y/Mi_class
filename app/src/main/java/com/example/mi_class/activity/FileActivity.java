package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.mi_class.R;
import com.example.mi_class.adapter.FileAdapter;
import com.example.mi_class.domain.File;


import java.util.ArrayList;
import java.util.List;

public class FileActivity extends AppCompatActivity {

    private List<File> file_list;
    private FileAdapter file_adapter;
    private ListView file_list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_file);

        // 设置标题
        setTitle("课程文件");
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        file_list_view = (ListView) findViewById(R.id.file_list_view);

        // 默认数据显示用
        File file = new File("课程资料一.doc","未下载","400KB");
        File file1 = new File("课程资料二.doc","未下载","2M");

        file_list = new ArrayList<File>();
        file_list.add(file);
        file_list.add(file1);

        file_adapter = new FileAdapter(FileActivity.this, R.layout.file_list, file_list);     //初始化适配器
        file_list_view.setAdapter(file_adapter);
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