package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mi_class.R;
import com.example.mi_class.adapter.AnnouncementAdapter;
import com.example.mi_class.adapter.CourseAdapter;
import com.example.mi_class.domain.Announcement;
import com.example.mi_class.domain.Course;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementActivity extends AppCompatActivity {

    private List<Announcement> announcement_list;
    private AnnouncementAdapter announcement_adapter;
    private ListView announcement_list_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_announcement);
        announcement_list_view = (ListView) findViewById(R.id.announcement_list_view);

        // 设置标题
        setTitle("公告");
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 默认数据显示用
        Announcement announcement = new Announcement("通知","考试时间为周三下午。","2020.10.7 13:23");
        announcement_list = new ArrayList<Announcement>();
        announcement_list.add(announcement);
        announcement_list.add(announcement);

        announcement_adapter = new AnnouncementAdapter(AnnouncementActivity.this, R.layout.announcement_list,announcement_list);     //初始化适配器
        announcement_list_view.setAdapter(announcement_adapter); //设置适配器，显示查询结果
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