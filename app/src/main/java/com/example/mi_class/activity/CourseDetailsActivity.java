package com.example.mi_class.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.mi_class.R;
import com.example.mi_class.Start_activity;

public class CourseDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private String course_code;
    private ImageView announcement_button,sign_in_button,member_button,homework_button,file_button,more_button;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_course_details);

        //获得上个活动发送的信息
        Intent intent = getIntent();
        course_code = intent.getStringExtra("course_code");
        Log.d("123",course_code);

        // 设置标题
        setTitle(intent.getStringExtra("course_name"));
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        announcement_button = (ImageView)findViewById(R.id.announcement_button);
        sign_in_button = findViewById(R.id.sign_in_button);
        member_button = findViewById(R.id.member_button);
        homework_button = findViewById(R.id.homework_button);
        file_button = findViewById(R.id.file_button);
        more_button = findViewById(R.id.more_button);

        announcement_button.setOnClickListener(this);
        sign_in_button.setOnClickListener(this);
        member_button.setOnClickListener(this);
        homework_button.setOnClickListener(this);
        file_button.setOnClickListener(this);
        more_button.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.announcement_button:
                intent = new Intent(CourseDetailsActivity.this, AnnouncementActivity.class);
                startActivity(intent);
                break;
            case R.id.sign_in_button:
                intent = new Intent(CourseDetailsActivity.this, SignInActivity.class);
                startActivity(intent);
                break;
            case R.id.member_button:
                intent = new Intent(CourseDetailsActivity.this, MemberActivity.class);
                startActivity(intent);
                break;
            case R.id.homework_button:
                intent = new Intent(CourseDetailsActivity.this,HomeworkActivity.class);
                startActivity(intent);
                break;
            case R.id.file_button:
                intent = new Intent(CourseDetailsActivity.this,FileActivity.class);
                startActivity(intent);
                break;
            case R.id.more_button:

                break;
            default:
                break;
        }
    }
}