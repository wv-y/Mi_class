package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.example.mi_class.R;
import com.example.mi_class.adapter.HworkFragementAdapter;
import com.example.mi_class.domain.File;
import com.example.mi_class.domain.StuLogInfo;
//import com.example.mi_class.fragment.HomeworkFragement;
import com.example.mi_class.fragment.HomeworkFragement;
import com.example.mi_class.tool.HttpFile;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HworkCommitActivity extends AppCompatActivity {


    private TabLayout tabLayout = null;
    private ViewPager viewPager;
    private RecyclerView stu_recyclerView;
    private List<Fragment> fragments = new ArrayList<>();
    private List<String>tabTitles = new ArrayList<>();
    private String course_code,fb_time;
    private List<StuLogInfo> stuLogInfos = new ArrayList<>();
    private int state;  // 0全部，1已提交，2未提交

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setTitle("提交详情");
        setContentView(R.layout.activity_hwork_commit);
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tabLayout = (TabLayout) findViewById(R.id.homework_tab_layout);
        viewPager = (ViewPager) findViewById(R.id.homework_view_pager);
        initView();
    }

    private void initView(){
        Intent intent = getIntent();
        String stu_info_list = intent.getStringExtra("stu_info_list"); //学生列表
        System.out.println("stu_info_list"+stu_info_list);

        stuLogInfos = get_stu_list(stu_info_list);
        course_code = intent.getStringExtra("course_code"); //课程id

        fb_time = intent.getStringExtra("fb_time"); //发布时间

        tabTitles.add("全部");
        tabTitles.add("已提交");
        tabTitles.add("未提交");

        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        List<StuLogInfo> stuLogInfos1 = new ArrayList<>();
        List<StuLogInfo> stuLogInfos2 = new ArrayList<>();

        for(int i=0;i<stuLogInfos.size();i++){
            StuLogInfo stuLogInfo = new StuLogInfo();
            stuLogInfo = stuLogInfos.get(i);
            if(stuLogInfo.getValue().equals("未提交")){
                stuLogInfos2.add(stuLogInfo);
            }
            else{
                stuLogInfos1.add(stuLogInfo);
            }
        }

        HomeworkFragement homeworkFragement1 = HomeworkFragement.newInstance(stuLogInfos,course_code,fb_time);
        HomeworkFragement homeworkFragement2 = HomeworkFragement.newInstance(stuLogInfos1,course_code,fb_time);
        HomeworkFragement homeworkFragement3 = HomeworkFragement.newInstance(stuLogInfos2,course_code,fb_time);

        fragments.add(homeworkFragement1);
        fragments.add(homeworkFragement2);
        fragments.add(homeworkFragement3);
//        标题
        for(int i=0;i<=2;i++){
            tabLayout.addTab(tabLayout.newTab().setText(tabTitles.get(i)));
        }

//        适配器
        HworkFragementAdapter fragementAdapter = new HworkFragementAdapter(getSupportFragmentManager(),fragments,tabTitles);
//        给viewpager设置adapter
        viewPager.setAdapter(fragementAdapter);
//        将tablayout和viewPager关联起来
        tabLayout.setupWithViewPager(viewPager);
//        给tablayout设置适配器
        tabLayout.setTabsFromPagerAdapter(fragementAdapter);
    }

    //解析学生列表(String->List<StuLogInfo>
    public List<StuLogInfo> get_stu_list(String str){
        List<StuLogInfo> list = new ArrayList<>();
        try{
            JSONArray array = new JSONArray(str);
            for(int i=0;i<array.length();i++){
                StuLogInfo stuLogInfo = new StuLogInfo();
                JSONObject jsonObject = array.getJSONObject(i);
                stuLogInfo.setStu_id(jsonObject.getString("stu_id")); //学生id
                stuLogInfo.setStu_name(jsonObject.getString("stu_name"));   //学生姓名
                stuLogInfo.setStu_phone(jsonObject.getString("stu_phone")); //学生电话
                stuLogInfo.setValue(jsonObject.getString("value")); //提交状态
                list.add(stuLogInfo);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            返回按钮
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}